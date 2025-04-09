package com.oltrysifp.arrowdrawer

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.oltrysifp.arrowdrawer.bitmap.loadBitmapFromUri
import com.oltrysifp.arrowdrawer.composable.ArrowMagnifier
import com.oltrysifp.arrowdrawer.composable.AttachToArrow
import com.oltrysifp.arrowdrawer.composable.inputs.BottomControls
import com.oltrysifp.arrowdrawer.composable.inputs.EditMenu
import com.oltrysifp.arrowdrawer.composable.onArrow.CentralContent
import com.oltrysifp.arrowdrawer.composable.onArrow.EndContent
import com.oltrysifp.arrowdrawer.composable.onArrow.StartContent
import com.oltrysifp.arrowdrawer.draw.drawAllAndExport
import com.oltrysifp.arrowdrawer.draw.drawArrow
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.ui.theme.ArrowDrawerTheme
import com.oltrysifp.arrowdrawer.util.log
import nl.birdly.zoombox.rememberMutableZoomState
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArrowDrawerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val zoomState = rememberMutableZoomState()
    val immutableZoomState = zoomState.value

    val mContext = LocalContext.current
    val displayMetrics = mContext.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels

    var imageUri: Uri? by remember { mutableStateOf(null) }
    var bitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }
    var loaded by remember { mutableStateOf(false) }

    var imageScale by remember { mutableFloatStateOf(1f) }

    var isDragging by remember { mutableStateOf(false) }

    val offsetThreshold = 50f
    var initialOffset by remember { mutableStateOf(Offset.Zero) }
    var generalOffset by remember { mutableStateOf(Offset.Zero) }
    var lineCreated by remember { mutableStateOf(false) }

    val globalLine = remember { mutableStateOf<Line?>(null) }

    val lineList = remember {
        mutableStateListOf<Line>()
    }
    val focusPoint = remember { mutableStateOf<MutableState<Offset>?>(null) }
    var focusedLine by remember { mutableStateOf<Line?>(null) }
    var editOpened by remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    LaunchedEffect(imageUri) {
        imageUri?.let {
            bitmap = loadBitmapFromUri(mContext, it)

            val bt = bitmap
            if (bt != null) {
                val scaleC: Float = screenWidth.toFloat() / bt.width
                imageScale = scaleC
                loaded = true
            }
        }
    }

    LaunchedEffect(globalLine.value) {
        globalLine.value?.let {
            for (line in lineList) {
                line.customCoefficient = it.customCoefficient
                line.customSize = it.customSize
                line.customUnit = it.customUnit
            }
        }
    }

    val onDragStart = { offset: Offset ->
        initialOffset = offset
        generalOffset = offset
    }

    val onDrag = { event: PointerEvent, fingersCount: Int ->
        val pan = event.calculatePan()
        val zoom = event.calculateZoom()

        if (fingersCount < 2) {
            generalOffset += pan

            val deltaOffset = Offset(
                x = abs(initialOffset.x - generalOffset.x),
                y = abs(initialOffset.y - generalOffset.y)
            )

            if (
                deltaOffset.x > offsetThreshold || deltaOffset.y > offsetThreshold
            ) {
                if (!lineCreated) {
                    lineList.add(
                        Line(
                            mutableStateOf(initialOffset),
                            mutableStateOf(initialOffset),

                            customCoefficient = globalLine.value?.customCoefficient,
                            customSize = globalLine.value?.customSize,
                            customUnit = globalLine.value?.customUnit,
                        )
                    )

                    val line = lineList.last()
                    focusedLine = line
                    focusPoint.value = line.end
                    lineCreated = true
                } else {
                    val line = lineList.last()

                    line.end.value += pan
                }
            }
        } else {
            val centroid = event.calculateCentroid()

            val newScale = zoom * zoomState.value.scale
            val newOffset = Offset(
                zoomState.value.offset.x + -pan.x * newScale + (newScale - zoomState.value.scale) * centroid.x,
                zoomState.value.offset.y + -pan.y * newScale + (newScale - zoomState.value.scale) * centroid.y,
            )
            if (newOffset != Offset.Unspecified) {
                val newZoom = zoomState.value.copy(
                    scale = newScale,
                    offset = newOffset
                )
                zoomState.value = newZoom
            }
        }
    }

    val onDragEnd = {
        focusPoint.value = null
        lineCreated = false
        initialOffset = Offset.Zero
        generalOffset = Offset.Zero
    }

    Box(
        Modifier
            .fillMaxSize()
    ) {
        focusedLine?.let {
            if (editOpened) {
                EditMenu(
                    it,
                    onExit = { newLine ->
                        editOpened = false

                        lineList[lineList.indexOf(focusedLine)] = newLine
                        focusedLine = newLine
                    },
                    onDelete = {
                        editOpened = false

                        lineList.remove(focusedLine)
                        focusedLine = null
                    },
                    onInherit = { line ->
                        globalLine.value = line
                    }
                )
            }
        }
        ArrowMagnifier(focusPoint, zoomState.value)

        bitmap?.let { bt ->
            Image(
                modifier = Modifier
                    .wrapContentSize(unbounded = true, align = Alignment.TopStart)
                    .graphicsLayer(
                        scaleX = immutableZoomState.scale,
                        scaleY = immutableZoomState.scale,
                        translationX = -immutableZoomState.offset.x,
                        translationY = -immutableZoomState.offset.y,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            // Wait for the first down event (gesture starts)
                            val firstDown = awaitFirstDown(requireUnconsumed = false)

                            val position = firstDown.position
                            var offset = Offset.Zero + position

                            do {
                                val event = awaitPointerEvent()
                                val fingersCount = event.changes.count()
                                val canceled = event.changes.fastAny { it.isConsumed }

                                if (!canceled) {
                                    if (!isDragging) {
                                        isDragging = true
                                        if (fingersCount < 2) {
                                            offset += event.calculatePan()
                                            onDragStart(offset)
                                        }
                                    } else {
                                        onDrag(
                                            event,
                                            fingersCount
                                        )
                                    }

                                    event.changes.fastForEach {
                                        if (it.positionChanged()) {
                                            it.consume()
                                        }
                                    }
                                }

                                if (canceled || event.changes.fastAll { !it.pressed }) {
                                    isDragging = false // Drag ended
                                    onDragEnd()
                                }
                            } while (isDragging)
                        }
                    }
                    .then(
                        if (immutableZoomState.childRect == null) {
                            Modifier.onGloballyPositioned { layoutCoordinates ->
                                val positionInParent = layoutCoordinates.positionInParent()
                                val childRect = Rect(
                                    positionInParent.x,
                                    positionInParent.y,
                                    positionInParent.x + layoutCoordinates.size.width,
                                    positionInParent.y + layoutCoordinates.size.height
                                )
                                zoomState.value = immutableZoomState.copy(
                                    childRect = childRect
                                )
                            }
                        } else Modifier
                    ),
                bitmap = bt.asImageBitmap(),
                contentScale = ContentScale.None,
                contentDescription = "image",
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            for (line in lineList) {
                val lineCopy = line.attachedCopy(zoomState.value.scale, -zoomState.value.offset)

                drawArrow(
                    lineCopy
                )
            }
        }

        for (line in lineList) {
            val lineCopy = line.attachedCopy(zoomState.value.scale, -zoomState.value.offset)

            AttachToArrow(
                lineCopy,
                startContent = {
                    StartContent(
                        focusedLine,
                        line,
                        focusPoint,
                        zoomState.value
                    )
                },
                endContent = {
                    EndContent(
                        focusedLine,
                        line,
                        focusPoint,
                        zoomState.value
                    )
                },
                centerContent = { properties ->
                    CentralContent(
                        properties,
                        line,
                        zoomState.value.scale,
                        onFocus = {
                            focusedLine = line
                        }
                    )
                }
            )
        }

        val saveContext = LocalContext.current

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            BottomControls(
                loaded,
                focusedLine,

                onEdit = {
                    editOpened = true
                },
                onLoad = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onExport = {
                    drawAllAndExport(
                        bitmap,
                        lineList,
                        imageScale,

                        saveContext
                    )
                }
            )
        }
    }
}
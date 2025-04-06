package com.oltrysifp.arrowdrawer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Half.toFloat
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
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.fastAll
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import com.oltrysifp.arrowdrawer.composable.AttachToArrow
import com.oltrysifp.arrowdrawer.composable.onArrow.CentralContent
import com.oltrysifp.arrowdrawer.composable.inputs.BottomControls
import com.oltrysifp.arrowdrawer.composable.inputs.EditMenu
import com.oltrysifp.arrowdrawer.draw.drawArrow
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.ui.theme.ArrowDrawerTheme
import com.oltrysifp.arrowdrawer.bitmap.loadBitmapFromUri
import com.oltrysifp.arrowdrawer.composable.ArrowMagnifier
import com.oltrysifp.arrowdrawer.composable.onArrow.EndContent
import com.oltrysifp.arrowdrawer.composable.onArrow.StartContent
import com.oltrysifp.arrowdrawer.draw.drawAllAndExport
import com.oltrysifp.arrowdrawer.util.log
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

@SuppressLint("UnrememberedMutableState")
@Composable
fun MainScreen() {
    val mContext = LocalContext.current
    val displayMetrics = mContext.resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels
    val screenHeight = displayMetrics.heightPixels

    var imageUri: Uri? by remember { mutableStateOf(null) }
    var bitmap by remember(imageUri) { mutableStateOf<Bitmap?>(null) }
    var loaded by remember { mutableStateOf(false) }

    var imageOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var imageScale by remember { mutableFloatStateOf(1f) }
    var screenImageScale by remember { mutableFloatStateOf(1f) }

    var isDrawMode by remember { mutableStateOf(true) }
    var isDragging by remember { mutableStateOf(false) }
    var lastZoomingPoint by remember { mutableStateOf(Offset(0f,0f)) }
    var zoomingPoint by remember { mutableStateOf(Offset(0f,0f)) }

    val offsetThreshold = 50f
    var initialOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var generalOffset by remember { mutableStateOf(Offset(0f, 0f)) }
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
        if (isDrawMode) {
            initialOffset = offset
            generalOffset = offset
        }
    }

    val onDrag = { offset: Offset, zoom: Float ->
        if (isDrawMode) {
            generalOffset += offset

            if (
                !(abs(initialOffset.x - generalOffset.x) < offsetThreshold &&
                abs(initialOffset.y - generalOffset.y) < offsetThreshold)
            ) {
                if (!lineCreated) {
                    lineList.add(
                        Line(
                            mutableStateOf((initialOffset) / screenImageScale - imageOffset/screenImageScale),
                            mutableStateOf((generalOffset) / screenImageScale - imageOffset/screenImageScale),

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

                    line.end.value += offset / screenImageScale
                }
            }
        } else {
            screenImageScale *= zoom

            imageOffset += Offset(
                offset.x,
                offset.y
            )
        }
    }

    val onDragEnd = {
        focusPoint.value = null
        lineCreated = false
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
        ArrowMagnifier(focusPoint, screenImageScale, imageOffset)

        bitmap?.let { bt ->
            Box(
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            // Wait for the first down event (gesture starts)
                            val firstDown = awaitFirstDown(requireUnconsumed = false)

                            val position = firstDown.position
                            var offset = Offset.Zero + position

                            lastZoomingPoint = zoomingPoint
                            zoomingPoint = Offset(
                                (position.x-imageOffset.x) / bt.width,
                                (position.y-imageOffset.y) / bt.height
                            )

                            do {
                                val event = awaitPointerEvent()
                                val canceled = event.changes.fastAny { it.isConsumed }

                                if (!canceled) {
                                    val zoom = event.calculateZoom()
                                    val pan = event.calculatePan()

                                    if (!isDragging) {
                                        log("start")
                                        isDragging = true // Drag started
                                        offset += pan
                                        onDragStart(offset)
                                    } else {
                                        onDrag(
                                            pan,
                                            zoom
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
            ) {
                Image(
                    modifier = Modifier
                        .wrapContentSize(unbounded = true, align = Alignment.TopStart)
                        .offset {
                            IntOffset(
                                imageOffset.x.toInt(),
                                imageOffset.y.toInt()
                            )
                        }
                        .graphicsLayer {
                            log(zoomingPoint)
                            log(lastZoomingPoint.y-zoomingPoint.y)
                            transformOrigin = TransformOrigin(zoomingPoint.x,zoomingPoint.y)
                            scaleX *= imageScale * screenImageScale
                            scaleY *= imageScale * screenImageScale
                        },
                    bitmap = bt.asImageBitmap(),
                    contentScale = ContentScale.None,
                    contentDescription = "image",
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            for (line in lineList) {
                val lineCopy = line.attachedCopy(screenImageScale, imageOffset)

                drawArrow(
                    lineCopy
                )
            }
        }

        for (line in lineList) {
            val lineCopy = line.attachedCopy(screenImageScale, imageOffset)

            AttachToArrow(
                lineCopy,
                startContent = {
                    StartContent(
                        focusedLine,
                        line,
                        focusPoint,
                        screenImageScale
                    )
                },
                endContent = {
                    EndContent(
                        focusedLine,
                        line,
                        focusPoint,
                        screenImageScale
                    )
                },
                centerContent = { properties ->
                    CentralContent(
                        properties,
                        line,
                        screenImageScale,
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
                isDrawMode,
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
                },
                onModeSwitch = { isDraw ->
                    isDrawMode = isDraw
                }
            )
        }
    }
}
package com.oltrysifp.arrowdrawer

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oltrysifp.arrowdrawer.composable.ArrowMagnifier
import com.oltrysifp.arrowdrawer.composable.AttachControlsToLine
import com.oltrysifp.arrowdrawer.composable.EdgeToEdgeConfig
import com.oltrysifp.arrowdrawer.composable.inputs.BottomControls
import com.oltrysifp.arrowdrawer.composable.inputs.CanvasSettings
import com.oltrysifp.arrowdrawer.composable.inputs.EditMenu
import com.oltrysifp.arrowdrawer.composable.inputs.SureToInherit
import com.oltrysifp.arrowdrawer.composable.zoom.rememberMutableZoomState
import com.oltrysifp.arrowdrawer.draw.onBitmap.drawAllAndExport
import com.oltrysifp.arrowdrawer.draw.onScreen.DrawAllOnScreen
import com.oltrysifp.arrowdrawer.models.Action
import com.oltrysifp.arrowdrawer.models.AddAction
import com.oltrysifp.arrowdrawer.models.DeleteAction
import com.oltrysifp.arrowdrawer.models.Line
import com.oltrysifp.arrowdrawer.models.Project
import com.oltrysifp.arrowdrawer.models.enums.InheritType
import com.oltrysifp.arrowdrawer.models.redoAction
import com.oltrysifp.arrowdrawer.models.undoAction
import com.oltrysifp.arrowdrawer.repositories.ProjectRepository
import com.oltrysifp.arrowdrawer.ui.theme.ArrowDrawerTheme
import com.oltrysifp.arrowdrawer.util.Constants
import com.oltrysifp.arrowdrawer.viewModels.ProjectViewModel
import com.oltrysifp.arrowdrawer.viewModels.ProjectViewModelFactory
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    private val viewModel: ProjectViewModel by viewModels {
        ProjectViewModelFactory(ProjectRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = intent.extras ?: return
        val projectName = b.getString("project") ?: return

        setContent {
            EdgeToEdgeConfig(this)
            ArrowDrawerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        val currentProject by viewModel.currentProject.collectAsState()

                        LaunchedEffect(Unit) {
                            viewModel.loadProject(projectName)
                        }

                        currentProject?.let {
                            MainScreen(it, viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.forceSave()
    }
}

@Composable
fun MainScreen(
    project: Project,
    viewModel: ProjectViewModel = viewModel()
) {
    val mContext = LocalContext.current
    val activity = remember { (mContext as? ComponentActivity) } ?: return

    val canvasSettings by viewModel.canvasSettings.collectAsState()

    val lineList = remember { mutableStateListOf<Line>() }
    LaunchedEffect(Unit) {
        lineList.clear() // clear existing lines
        lineList.addAll(project.objects) // load the lines into lineList
    }

    val zoomState = rememberMutableZoomState()
    val immutableZoomState = zoomState.value
    var initialOffset by remember { mutableStateOf(Offset.Zero) }
    var generalOffset by remember { mutableStateOf(Offset.Zero) }

    var isDragging by remember { mutableStateOf(false) }
    var drawMode by remember { mutableStateOf(false) }

    val focusPoint = remember { mutableStateOf<Offset?>(null) }
    var focusedLine by remember { mutableStateOf<Line?>(null) }
    val actionStack = remember { mutableStateListOf<Action>() }
    val redoStack = remember { mutableStateListOf<Action>() }

    var editOpened by remember { mutableStateOf(false) }
    var settingsOpened by remember { mutableStateOf(false) }

    var inheritType by remember { mutableStateOf(InheritType.NONE) }
    var inheritPicker: Line? by remember { mutableStateOf(null) }

    var imageMinScale by remember { mutableFloatStateOf(0.75f) }

    var bitmap: Bitmap? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
        val displayMetrics = mContext.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        zoomState.value = zoomState.value.copy(
            scale = screenWidth.toFloat() / project.image.width,
            offset = zoomState.value.offset
        )
        imageMinScale = zoomState.value.scale * 0.75f
        bitmap = project.image
    }

    LaunchedEffect(focusPoint.value) {
        if (focusPoint.value == null) {
            viewModel.updateLines(lineList.toList())
            viewModel.triggerSave()
        }
    }

    val onDragStart = { offset: Offset ->
        redoStack.clear()
        initialOffset = offset
        generalOffset = offset
    }

    val onDrag = { event: PointerEvent ->
        val pan = event.calculatePan()
        val zoom = event.calculateZoom()

        if (drawMode) {
            generalOffset += pan

            val deltaOffset = Offset(
                x = abs(initialOffset.x - generalOffset.x),
                y = abs(initialOffset.y - generalOffset.y)
            )

            if (deltaOffset.x > Constants.OFFSET_THRESHOLD ||
                deltaOffset.y > Constants.OFFSET_THRESHOLD) {
                if (focusPoint.value == null) {
                    val newLine = Line(
                        initialOffset,
                        generalOffset,

                        customCoefficient = canvasSettings.customCoefficient,
                        customSize = canvasSettings.customSize,
                        customUnit = canvasSettings.customUnit,

                        color = canvasSettings.color,
                        thickness = canvasSettings.thickness,
                        fontSize = canvasSettings.fontSize
                    )
                    lineList.add(newLine)
                    actionStack.add(AddAction(newLine))
                    focusedLine = newLine
                    focusPoint.value = newLine.end
                } else {
                    val line = focusedLine!!
                    line.end += pan
                    focusPoint.value = line.end
                }
            }
        } else {
            val centroid = event.calculateCentroid()
            if (centroid != Offset.Unspecified) {
                val newScale = (zoom * zoomState.value.scale).coerceIn(imageMinScale, Constants.MAX_ZOOM)
                val newOffset = Offset(
                    zoomState.value.offset.x + -pan.x * newScale + (newScale - zoomState.value.scale) * centroid.x,
                    zoomState.value.offset.y + -pan.y * newScale + (newScale - zoomState.value.scale) * centroid.y,
                )
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
        initialOffset = Offset.Zero
        generalOffset = Offset.Zero
    }

    Box(
        Modifier
            .fillMaxSize()
    ) {
        ArrowMagnifier(focusPoint, zoomState.value)
        inheritPicker?.let { inherit ->
            focusedLine?.let { focused ->
                SureToInherit(
                    inherit,

                    onInherit = { newSettings ->
                        if (inheritType == InheritType.CANVAS) {
                            viewModel.updateCurrentProjectSettings(newSettings)
                            viewModel.triggerSave()
                        } else {
                            focused.thickness = newSettings.thickness
                            focused.color = newSettings.color

                            focused.customUnit = newSettings.customUnit
                            focused.customCoefficient = newSettings.customCoefficient
                        }
                        inheritType = InheritType.NONE
                        inheritPicker = null
                    },
                    onCancel = {
                        inheritPicker = null
                        inheritType = InheritType.NONE
                    }
                )
            }
        }

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
                                val canceled = event.changes.fastAny { it.isConsumed }

                                if (!canceled) {
                                    if (!isDragging) {
                                        isDragging = true
                                        if (drawMode) {
                                            offset += event.calculatePan()
                                            onDragStart(offset)
                                        }
                                    } else {
                                        onDrag(
                                            event
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
                                    drawMode = false
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

        DrawAllOnScreen(lineList, zoomState)

        for (line in lineList) {
            AttachControlsToLine(
                line,
                zoomState,
                focusedLine,
                focusPoint,
                actionStack,
                inheritType,

                {inheritPicker = it},
                {focusedLine = it}
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            BottomControls(
                actionStack,
                redoStack,
                focusedLine,
                drawMode,

                onUndo = {
                    val lastIndex = actionStack.lastIndex
                    if (lastIndex != -1) {
                        val actionToUndo = actionStack[lastIndex]
                        undoAction(actionToUndo, lineList, focusedLine) { focusedLine = it }
                        actionStack.removeAt(lastIndex)
                        redoStack.add(actionToUndo)
                    }
                },
                onRedo = {
                    val lastIndex = redoStack.lastIndex
                    if (lastIndex != -1) {
                        val actionToRedo = redoStack[lastIndex]
                        redoAction(actionToRedo, lineList, focusedLine) { focusedLine = it }
                        redoStack.removeAt(lastIndex)
                        actionStack.add(actionToRedo)
                    }
                },
                onEdit = { editOpened = true },
                onExport = {
                    bitmap?.let { bt ->
                        drawAllAndExport(
                            bt,
                            lineList,
                            mContext,
                            activity
                        )
                    }
                },
                onSettings = { settingsOpened = true },
                onAdd = { drawMode = !drawMode }
            )
        }

        if (editOpened) {
            focusedLine?.let {
                EditMenu(
                    it,
                    onExit = { newLine ->
                        editOpened = false
                        it.mutate(newLine)
                    },
                    onDelete = {
                        editOpened = false

                        actionStack.add(DeleteAction(it))
                        lineList.remove(focusedLine)
                        focusedLine = null
                    },
                    onInherit = {
                        inheritType = InheritType.LINE
                        editOpened = false
                    }
                )
            }
        }

        if (settingsOpened) {
            CanvasSettings(
                canvasSettings,

                onExit = { newSettings ->
                    viewModel.updateCurrentProjectSettings(newSettings)
                    viewModel.triggerSave()
                    settingsOpened = false
                },
                onInherit = {
                    settingsOpened = false
                    inheritType = InheritType.CANVAS
                }
            )
        }
    }
}
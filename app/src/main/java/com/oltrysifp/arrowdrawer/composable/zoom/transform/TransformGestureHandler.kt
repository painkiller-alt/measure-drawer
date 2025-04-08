package nl.birdly.zoombox.gesture.transform

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlinx.coroutines.CoroutineScope
import nl.birdly.zoombox.ZoomState
import nl.birdly.zoombox.gesture.condition.AnyTouchCondition
import nl.birdly.zoombox.gesture.condition.TouchCondition
import nl.birdly.zoombox.util.detectTransformGestures

class TransformGestureHandler(
    private val onCondition: TouchCondition = AnyTouchCondition(),
    private val onPinchGesture: OnPinchGestureHandler = OnPinchToZoomGestureHandler()
) {

    suspend operator fun invoke(
        pointerInputScope: PointerInputScope,
        zoomStateProvider: () -> ZoomState,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        pointerInputScope.detectTransformGestures(
            zoomStateProvider,
            pointerInputScope,
            onCondition = onCondition
        ) { centroid: Offset, pan: Offset, gestureZoom: Float ->
            onPinchGesture(
                centroid,
                pan,
                zoomStateProvider(),
                gestureZoom,
                onZoomUpdated
            )
        }
    }
}

package nl.birdly.zoombox.gesture.transform

import androidx.compose.ui.geometry.Offset
import nl.birdly.zoombox.ZoomState

class OnPinchToZoomGestureHandler : OnPinchGestureHandler {

    override fun invoke(
        // The position in pixels of the centre zoom position where 0,0 is the top left corner
        centroid: Offset,
        pan: Offset,
        zoomState: ZoomState,
        gestureZoom: Float,
        onZoomUpdated: (ZoomState) -> Unit
    ) {
        val newScale = gestureZoom * zoomState.scale
        onZoomUpdated(zoomState.copy(
            scale = newScale,
            offset = Offset(
                zoomState.offset.x + -pan.x * newScale + (newScale - zoomState.scale) * centroid.x,
                zoomState.offset.y + -pan.y * newScale + (newScale - zoomState.scale) * centroid.y,
            )
        ))
    }
}
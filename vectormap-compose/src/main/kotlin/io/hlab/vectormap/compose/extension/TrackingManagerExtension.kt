package io.hlab.vectormap.compose.extension

import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.TrackingManager
import io.hlab.vectormap.compose.state.CameraPositionState

internal fun TrackingManager.startTrackingCompat(label: Label, cameraPositionState: CameraPositionState?) {
    startTracking(label)
    cameraPositionState?.onLabelTrackStarted(label.position)
}

internal fun TrackingManager.stopTrackingCompat(cameraPositionState: CameraPositionState?) {
    stopTracking()
    cameraPositionState?.onLabelTrackStopped()
}
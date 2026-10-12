package com.example.ui.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TrackpadConfig
import com.example.ui.theme.ControlTrackpadBase
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import kotlin.math.roundToInt

@Composable
fun VirtualTrackpad(
    config: TrackpadConfig,
    isEditMode: Boolean,
    isSelected: Boolean,
    screenWidthPx: Float,
    screenHeightPx: Float,
    opacity: Float,
    onDeltaMove: (dx: Float, dy: Float) -> Unit,
    onDragUpdate: (newXRatio: Float, newYRatio: Float) -> Unit,
    onSelectForEdit: () -> Unit
) {
    if (!config.enabled) return

    val density = LocalDensity.current
    val widthPx = with(density) { config.widthDp.dp.toPx() }
    val heightPx = with(density) { config.heightDp.dp.toPx() }

    val centerX = config.xRatio * screenWidthPx
    val centerY = config.yRatio * screenHeightPx
    val posX = centerX - (widthPx / 2f)
    val posY = centerY - (heightPx / 2f)

    Box(
        modifier = Modifier
            .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
            .size(config.widthDp.dp, config.heightDp.dp)
            .then(
                if (isEditMode) {
                    Modifier.pointerInput(config, screenWidthPx, screenHeightPx) {
                        detectDragGestures(
                            onDragStart = { onSelectForEdit() },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newX = ((centerX + dragAmount.x) / screenWidthPx).coerceIn(0.15f, 0.85f)
                                val newY = ((centerY + dragAmount.y) / screenHeightPx).coerceIn(0.15f, 0.85f)
                                onDragUpdate(newX, newY)
                            }
                        )
                    }
                } else {
                    Modifier.pointerInput(config) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val dx = dragAmount.x * config.sensitivity
                                val dy = dragAmount.y * config.sensitivity
                                onDeltaMove(dx, dy)
                            }
                        )
                    }
                }
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Background box with rounded corners
            drawRoundRect(
                color = ControlTrackpadBase.copy(alpha = 0.35f * opacity),
                size = Size(size.width, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // Outline border
            drawRoundRect(
                color = if (isSelected) Color.Yellow else GamingCyan.copy(alpha = 0.45f * opacity),
                size = Size(size.width, size.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                style = Stroke(width = if (isSelected) 3f else 1.5f)
            )

            // Corner decorative brackets
            val bracketLen = 16f
            val bracketColor = GamingBlue.copy(alpha = 0.8f * opacity)
            // Top-left
            drawLine(bracketColor, Offset(0f, 0f), Offset(bracketLen, 0f), 2.5f)
            drawLine(bracketColor, Offset(0f, 0f), Offset(0f, bracketLen), 2.5f)
            // Top-right
            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), 2.5f)
            drawLine(bracketColor, Offset(size.width, 0f), Offset(size.width, bracketLen), 2.5f)
            // Bottom-left
            drawLine(bracketColor, Offset(0f, size.height), Offset(bracketLen, size.height), 2.5f)
            drawLine(bracketColor, Offset(0f, size.height), Offset(0f, size.height - bracketLen), 2.5f)
            // Bottom-right
            drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), 2.5f)
            drawLine(bracketColor, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), 2.5f)

            // Center subtle crosshair
            val cx = size.width / 2f
            val cy = size.height / 2f
            val crosshairLen = 12f
            drawLine(GamingCyan.copy(alpha = 0.4f * opacity), Offset(cx - crosshairLen, cy), Offset(cx + crosshairLen, cy), 1.5f)
            drawLine(GamingCyan.copy(alpha = 0.4f * opacity), Offset(cx, cy - crosshairLen), Offset(cx, cy + crosshairLen), 1.5f)
        }

        Text(
            text = "CAMERA / AIM",
            color = Color.White.copy(alpha = 0.4f * opacity),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.BottomCenter).offset(y = (-8).dp)
        )
    }
}

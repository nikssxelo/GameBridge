package com.example.ui.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.DirectionalKey
import com.example.model.JoystickConfig
import com.example.ui.theme.ControlJoystickBase
import com.example.ui.theme.ControlJoystickStick
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun VirtualJoystick(
    config: JoystickConfig,
    isEditMode: Boolean,
    isSelected: Boolean,
    screenWidthPx: Float,
    screenHeightPx: Float,
    opacity: Float,
    onDirectionKeysChanged: (activeKeys: Set<DirectionalKey>) -> Unit,
    onDragUpdate: (newXRatio: Float, newYRatio: Float) -> Unit,
    onSelectForEdit: () -> Unit
) {
    if (!config.enabled) return

    val density = LocalDensity.current
    val sizePx = with(density) { config.sizeDp.dp.toPx() }
    val baseRadius = sizePx / 2f
    val stickRadius = baseRadius * 0.42f
    val maxTravel = baseRadius - stickRadius

    val centerX = config.xRatio * screenWidthPx
    val centerY = config.yRatio * screenHeightPx
    val posX = centerX - baseRadius
    val posY = centerY - baseRadius

    var stickOffset by remember { mutableStateOf(Offset.Zero) }
    var currentActiveKeys by remember { mutableStateOf(emptySet<DirectionalKey>()) }

    Box(
        modifier = Modifier
            .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
            .size(config.sizeDp.dp)
            .then(
                if (isEditMode) {
                    Modifier.pointerInput(config, screenWidthPx, screenHeightPx) {
                        detectDragGestures(
                            onDragStart = { onSelectForEdit() },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newX = ((centerX + dragAmount.x) / screenWidthPx).coerceIn(0.08f, 0.92f)
                                val newY = ((centerY + dragAmount.y) / screenHeightPx).coerceIn(0.08f, 0.92f)
                                onDragUpdate(newX, newY)
                            }
                        )
                    }
                } else {
                    Modifier.pointerInput(config) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val localOffset = offset - Offset(baseRadius, baseRadius)
                                val dist = hypot(localOffset.x, localOffset.y)
                                val clampedOffset = if (dist > maxTravel) {
                                    val angle = atan2(localOffset.y, localOffset.x)
                                    Offset(cos(angle) * maxTravel, sin(angle) * maxTravel)
                                } else {
                                    localOffset
                                }
                                stickOffset = clampedOffset
                                val keys = computeDirectionKeys(clampedOffset, maxTravel, config)
                                if (keys != currentActiveKeys) {
                                    currentActiveKeys = keys
                                    onDirectionKeysChanged(keys)
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newLocal = stickOffset + dragAmount
                                val dist = hypot(newLocal.x, newLocal.y)
                                val clampedOffset = if (dist > maxTravel) {
                                    val angle = atan2(newLocal.y, newLocal.x)
                                    Offset(cos(angle) * maxTravel, sin(angle) * maxTravel)
                                } else {
                                    newLocal
                                }
                                stickOffset = clampedOffset
                                val keys = computeDirectionKeys(clampedOffset, maxTravel, config)
                                if (keys != currentActiveKeys) {
                                    currentActiveKeys = keys
                                    onDirectionKeysChanged(keys)
                                }
                            },
                            onDragEnd = {
                                stickOffset = Offset.Zero
                                if (currentActiveKeys.isNotEmpty()) {
                                    currentActiveKeys = emptySet()
                                    onDirectionKeysChanged(emptySet())
                                }
                            },
                            onDragCancel = {
                                stickOffset = Offset.Zero
                                if (currentActiveKeys.isNotEmpty()) {
                                    currentActiveKeys = emptySet()
                                    onDirectionKeysChanged(emptySet())
                                }
                            }
                        )
                    }
                }
            )
    ) {
        Canvas(modifier = Modifier.size(config.sizeDp.dp)) {
            val center = Offset(baseRadius, baseRadius)

            // Base circle
            drawCircle(
                color = ControlJoystickBase.copy(alpha = 0.45f * opacity),
                radius = baseRadius,
                center = center
            )

            // Outer ring
            drawCircle(
                color = if (isSelected) Color.Yellow else GamingBlue.copy(alpha = 0.5f * opacity),
                radius = baseRadius,
                center = center,
                style = Stroke(width = if (isSelected) 3f else 2f)
            )

            // Directional notches / arrows
            val notchLen = 14f
            val notchAlpha = 0.6f * opacity
            // Up notch
            drawLine(
                color = GamingCyan.copy(alpha = notchAlpha),
                start = Offset(baseRadius, 10f),
                end = Offset(baseRadius, 10f + notchLen),
                strokeWidth = 2.5f
            )
            // Down notch
            drawLine(
                color = GamingCyan.copy(alpha = notchAlpha),
                start = Offset(baseRadius, sizePx - 10f),
                end = Offset(baseRadius, sizePx - 10f - notchLen),
                strokeWidth = 2.5f
            )
            // Left notch
            drawLine(
                color = GamingCyan.copy(alpha = notchAlpha),
                start = Offset(10f, baseRadius),
                end = Offset(10f + notchLen, baseRadius),
                strokeWidth = 2.5f
            )
            // Right notch
            drawLine(
                color = GamingCyan.copy(alpha = notchAlpha),
                start = Offset(sizePx - 10f, baseRadius),
                end = Offset(sizePx - 10f - notchLen, baseRadius),
                strokeWidth = 2.5f
            )

            // Thumb stick
            val thumbCenter = center + stickOffset
            drawCircle(
                color = ControlJoystickStick.copy(alpha = 0.75f * opacity),
                radius = stickRadius,
                center = thumbCenter
            )
            drawCircle(
                color = GamingCyan.copy(alpha = 0.9f * opacity),
                radius = stickRadius,
                center = thumbCenter,
                style = Stroke(width = 2.5f)
            )
            // Inner glowing dot
            drawCircle(
                color = Color.White.copy(alpha = 0.9f * opacity),
                radius = 5f,
                center = thumbCenter
            )
        }
    }
}

private fun computeDirectionKeys(
    offset: Offset,
    maxTravel: Float,
    config: JoystickConfig
): Set<DirectionalKey> {
    val distance = hypot(offset.x, offset.y)
    val normDist = (distance / maxTravel).coerceIn(0f, 1f)
    if (normDist < config.deadZone) return emptySet()

    val result = mutableSetOf<DirectionalKey>()
    val nx = offset.x / distance
    val ny = offset.y / distance

    // Check vertical component
    if (ny < -0.38f) {
        result.add(config.upKey)
    } else if (ny > 0.38f) {
        result.add(config.downKey)
    }

    // Check horizontal component
    if (nx < -0.38f) {
        result.add(config.leftKey)
    } else if (nx > 0.38f) {
        result.add(config.rightKey)
    }

    return result
}

package com.example.ui.overlay

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionType
import com.example.model.VirtualButtonConfig
import com.example.ui.theme.ControlButtonActive
import com.example.ui.theme.ControlButtonBorder
import com.example.ui.theme.ControlButtonDefault
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import kotlin.math.roundToInt

@Composable
fun VirtualButtonView(
    button: VirtualButtonConfig,
    isEditMode: Boolean,
    isSelected: Boolean,
    screenWidthPx: Float,
    screenHeightPx: Float,
    opacity: Float,
    onDown: () -> Unit,
    onUp: () -> Unit,
    onDragUpdate: (newXRatio: Float, newYRatio: Float) -> Unit,
    onSelectForEdit: () -> Unit
) {
    val density = LocalDensity.current
    var isPressed by remember { mutableStateOf(false) }

    val sizePx = with(density) { button.sizeDp.dp.toPx() }
    val posX = (button.xRatio * screenWidthPx) - (sizePx / 2f)
    val posY = (button.yRatio * screenHeightPx) - (sizePx / 2f)

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        label = "btn_press_scale"
    )

    val isMouse = button.actionType == ActionType.MOUSE_BUTTON
    val accentColor = if (isMouse) GamingCyan else GamingBlue

    val bgBrush = if (isPressed) {
        Brush.radialGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.85f * opacity),
                ControlButtonActive.copy(alpha = 0.95f * opacity)
            )
        )
    } else {
        Brush.radialGradient(
            colors = listOf(
                ControlButtonDefault.copy(alpha = 0.45f * opacity),
                Color(0xFF0F172A).copy(alpha = 0.65f * opacity)
            )
        )
    }

    val borderColor = when {
        isSelected -> Color.Yellow
        isPressed -> accentColor
        else -> ControlButtonBorder.copy(alpha = 0.6f * opacity)
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(posX.roundToInt(), posY.roundToInt()) }
            .size(button.sizeDp.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(bgBrush)
            .border(
                width = if (isSelected) 3.dp else 1.8.dp,
                color = borderColor,
                shape = CircleShape
            )
            .then(
                if (isEditMode) {
                    Modifier
                        .pointerInput(button.id, screenWidthPx, screenHeightPx) {
                            detectDragGestures(
                                onDragStart = { onSelectForEdit() },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newX = ((posX + (sizePx / 2f) + dragAmount.x) / screenWidthPx)
                                        .coerceIn(0.05f, 0.95f)
                                    val newY = ((posY + (sizePx / 2f) + dragAmount.y) / screenHeightPx)
                                        .coerceIn(0.05f, 0.95f)
                                    onDragUpdate(newX, newY)
                                }
                            )
                        }
                        .pointerInput(button.id) {
                            detectTapGestures(
                                onTap = { onSelectForEdit() }
                            )
                        }
                } else {
                    Modifier.pointerInput(button.id) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                onDown()
                                try {
                                    awaitRelease()
                                } finally {
                                    isPressed = false
                                    onUp()
                                }
                            }
                        )
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = button.label,
            color = if (isPressed) Color.White else Color(0xFFF1F5F9).copy(alpha = opacity.coerceAtLeast(0.4f)),
            fontSize = if (button.label.length > 5) 10.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )

        // Mouse indicator badge
        if (isMouse) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(GamingCyan)
            )
        }
    }
}

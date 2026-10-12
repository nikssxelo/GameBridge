package com.example.ui.overlay

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ActionType
import com.example.model.ControlProfile
import com.example.model.DirectionalKey
import com.example.model.VirtualButtonConfig
import com.example.ui.dialogs.EditButtonDialog
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface

@Composable
fun VirtualOverlay(
    profile: ControlProfile,
    isVisible: Boolean,
    isEditMode: Boolean,
    onKeyDown: (key: String, code: String, keyCode: Int) -> Unit,
    onKeyUp: (key: String, code: String, keyCode: Int) -> Unit,
    onMouseDown: (button: Int) -> Unit,
    onMouseUp: (button: Int) -> Unit,
    onMouseMove: (dx: Float, dy: Float) -> Unit,
    onProfileChanged: (ControlProfile) -> Unit,
    onSaveLayout: () -> Unit,
    onExitEditMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    val context = LocalContext.current
    var editingButton by remember { mutableStateOf<VirtualButtonConfig?>(null) }
    var selectedElementId by remember { mutableStateOf<String?>(null) }

    // Haptic feedback trigger
    val triggerHaptic = remember(context, profile.hapticFeedback) {
        {
            if (profile.hapticFeedback) {
                runCatching {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                        manager?.defaultVibrator?.vibrate(
                            VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(20)
                    }
                }
            }
        }
    }

    // Keep track of directional keys currently active on the joystick
    var previousActiveJoyKeys by remember { mutableStateOf(emptySet<DirectionalKey>()) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // Edit mode background guide grid
        if (isEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .border(2.dp, GamingCyan.copy(alpha = 0.5f))
            )
        }

        // Virtual Joystick
        VirtualJoystick(
            config = profile.joystick,
            isEditMode = isEditMode,
            isSelected = selectedElementId == "joystick",
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            opacity = profile.opacity,
            onDirectionKeysChanged = { newKeys ->
                // Identify released keys
                val released = previousActiveJoyKeys - newKeys
                released.forEach { dir ->
                    onKeyUp(dir.key, dir.code, dir.keyCode)
                }

                // Identify newly pressed keys
                val pressed = newKeys - previousActiveJoyKeys
                if (pressed.isNotEmpty()) {
                    triggerHaptic()
                }
                pressed.forEach { dir ->
                    onKeyDown(dir.key, dir.code, dir.keyCode)
                }

                previousActiveJoyKeys = newKeys
            },
            onDragUpdate = { newX, newY ->
                val updatedJoy = profile.joystick.copy(xRatio = newX, yRatio = newY)
                onProfileChanged(profile.copy(joystick = updatedJoy))
            },
            onSelectForEdit = {
                selectedElementId = "joystick"
            }
        )

        // Virtual Trackpad (Camera look)
        VirtualTrackpad(
            config = profile.trackpad,
            isEditMode = isEditMode,
            isSelected = selectedElementId == "trackpad",
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            opacity = profile.opacity,
            onDeltaMove = { dx, dy ->
                onMouseMove(dx, dy)
            },
            onDragUpdate = { newX, newY ->
                val updatedTrack = profile.trackpad.copy(xRatio = newX, yRatio = newY)
                onProfileChanged(profile.copy(trackpad = updatedTrack))
            },
            onSelectForEdit = {
                selectedElementId = "trackpad"
            }
        )

        // Action Buttons
        profile.buttons.forEach { button ->
            VirtualButtonView(
                button = button,
                isEditMode = isEditMode,
                isSelected = selectedElementId == button.id,
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx,
                opacity = profile.opacity,
                onDown = {
                    triggerHaptic()
                    if (button.actionType == ActionType.MOUSE_BUTTON) {
                        onMouseDown(button.mouseButton)
                    } else {
                        onKeyDown(button.key, button.code, button.keyCode)
                    }
                },
                onUp = {
                    if (button.actionType == ActionType.MOUSE_BUTTON) {
                        onMouseUp(button.mouseButton)
                    } else {
                        onKeyUp(button.key, button.code, button.keyCode)
                    }
                },
                onDragUpdate = { newX, newY ->
                    val updatedList = profile.buttons.map {
                        if (it.id == button.id) it.copy(xRatio = newX, yRatio = newY) else it
                    }
                    onProfileChanged(profile.copy(buttons = updatedList))
                },
                onSelectForEdit = {
                    selectedElementId = button.id
                    editingButton = button
                }
            )
        }

        // Edit Mode Floating Action Banner & Controls
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GamingNavySurface.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .border(1.dp, GamingCyan.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GamingCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("EDIT MODE", color = GamingCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Add Button action
                    Button(
                        onClick = {
                            val newBtnId = "btn_${System.currentTimeMillis() % 10000}"
                            val newButton = VirtualButtonConfig(
                                id = newBtnId,
                                label = "KEY",
                                actionType = ActionType.KEYBOARD,
                                key = "e",
                                code = "KeyE",
                                keyCode = 69,
                                xRatio = 0.5f,
                                yRatio = 0.5f,
                                sizeDp = 58f
                            )
                            val updatedButtons = profile.buttons + newButton
                            onProfileChanged(profile.copy(buttons = updatedButtons))
                            selectedElementId = newBtnId
                            editingButton = newButton
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Button", fontSize = 12.sp)
                    }

                    // Save action
                    Button(
                        onClick = {
                            onSaveLayout()
                            onExitEditMode()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Done", fontSize = 12.sp)
                    }

                    // Exit without saving
                    OutlinedButton(
                        onClick = onExitEditMode,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    // Modal dialog to customize selected button
    editingButton?.let { btn ->
        EditButtonDialog(
            button = btn,
            onDismiss = { editingButton = null },
            onSave = { updated ->
                val updatedButtons = profile.buttons.map {
                    if (it.id == updated.id) updated else it
                }
                onProfileChanged(profile.copy(buttons = updatedButtons))
                editingButton = null
            },
            onDelete = {
                val updatedButtons = profile.buttons.filter { it.id != btn.id }
                onProfileChanged(profile.copy(buttons = updatedButtons))
                editingButton = null
            }
        )
    }
}

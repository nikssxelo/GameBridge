package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ControlProfile
import com.example.model.DefaultProfiles
import com.example.ui.theme.AccentRed
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface
import com.example.ui.theme.GamingNavySurfaceVariant

@Composable
fun ProfileSelectorDialog(
    currentProfile: ControlProfile,
    currentUrl: String,
    onDismiss: () -> Unit,
    onSelectPreset: (presetId: String) -> Unit,
    onSaveForThisUrl: () -> Unit,
    onResetToDefaults: () -> Unit,
    onUpdateProfileSettings: (opacity: Float, haptic: Boolean, sensitivity: Float, joystickSize: Float) -> Unit
) {
    var opacity by remember { mutableFloatStateOf(currentProfile.opacity) }
    var haptic by remember { mutableStateOf(currentProfile.hapticFeedback) }
    var sensitivity by remember { mutableFloatStateOf(currentProfile.trackpad.sensitivity) }
    var joystickSize by remember { mutableFloatStateOf(currentProfile.joystick.sizeDp) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = GamingNavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Control Profiles & Settings",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Preset Profiles:",
                    color = GamingCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                DefaultProfiles.allDefaultPresets.forEach { preset ->
                    val isSelected = currentProfile.id == preset.id
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) GamingBlue.copy(alpha = 0.25f) else GamingNavySurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                onSelectPreset(preset.id)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = preset.name,
                                    color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = when (preset.id) {
                                        "preset_fps" -> "WASD + Camera Aim + LMB Fire + Jump/Sprint/Reload"
                                        "preset_platformer" -> "Directional + Space Jump + Run + Attack/Skills"
                                        else -> "Steering + Gas/Brake + Drift Handbrake + Boost"
                                    },
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = GamingCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sliders & Toggles
                Text(
                    text = "Overlay Tuning:",
                    color = GamingCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Opacity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Button Opacity", color = Color.LightGray, fontSize = 12.sp)
                    Text("${(opacity * 100).toInt()}%", color = GamingCyan, fontSize = 12.sp)
                }
                Slider(
                    value = opacity,
                    onValueChange = {
                        opacity = it
                        onUpdateProfileSettings(opacity, haptic, sensitivity, joystickSize)
                    },
                    valueRange = 0.2f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = GamingCyan, activeTrackColor = GamingBlue)
                )

                // Joystick Size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Joystick Scale", color = Color.LightGray, fontSize = 12.sp)
                    Text("${joystickSize.toInt()} dp", color = GamingCyan, fontSize = 12.sp)
                }
                Slider(
                    value = joystickSize,
                    onValueChange = {
                        joystickSize = it
                        onUpdateProfileSettings(opacity, haptic, sensitivity, joystickSize)
                    },
                    valueRange = 100f..180f,
                    colors = SliderDefaults.colors(thumbColor = GamingCyan, activeTrackColor = GamingBlue)
                )

                // Camera Sensitivity
                if (currentProfile.trackpad.enabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Aim Trackpad Sensitivity", color = Color.LightGray, fontSize = 12.sp)
                        Text(String.format("%.1fx", sensitivity), color = GamingCyan, fontSize = 12.sp)
                    }
                    Slider(
                        value = sensitivity,
                        onValueChange = {
                            sensitivity = it
                            onUpdateProfileSettings(opacity, haptic, sensitivity, joystickSize)
                        },
                        valueRange = 0.5f..3.0f,
                        colors = SliderDefaults.colors(thumbColor = GamingCyan, activeTrackColor = GamingBlue)
                    )
                }

                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Haptic Vibration on Tap", color = Color.LightGray, fontSize = 13.sp)
                    Switch(
                        checked = haptic,
                        onCheckedChange = {
                            haptic = it
                            onUpdateProfileSettings(opacity, haptic, sensitivity, joystickSize)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GamingBlue
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions for URL & Reset
                Button(
                    onClick = {
                        onSaveForThisUrl()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Layout for Current Website", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onResetToDefaults()
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Restore Default Layout", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingNavySurfaceVariant),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

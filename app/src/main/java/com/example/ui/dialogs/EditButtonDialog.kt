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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.model.ActionType
import com.example.model.CommonKeyCatalog
import com.example.model.VirtualButtonConfig
import com.example.ui.theme.AccentRed
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface
import com.example.ui.theme.GamingNavySurfaceVariant

@Composable
fun EditButtonDialog(
    button: VirtualButtonConfig,
    onDismiss: () -> Unit,
    onSave: (updatedButton: VirtualButtonConfig) -> Unit,
    onDelete: () -> Unit
) {
    var label by remember { mutableStateOf(button.label) }
    var actionType by remember { mutableStateOf(button.actionType) }
    var key by remember { mutableStateOf(button.key) }
    var code by remember { mutableStateOf(button.code) }
    var keyCode by remember { mutableIntStateOf(button.keyCode) }
    var mouseButton by remember { mutableIntStateOf(button.mouseButton) }
    var sizeDp by remember { mutableFloatStateOf(button.sizeDp) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GamingNavySurface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Customize Button",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Button",
                            tint = AccentRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Type Selector (Keyboard vs Mouse)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(GamingNavySurfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (actionType == ActionType.KEYBOARD) GamingBlue else Color.Transparent)
                            .clickable { actionType = ActionType.KEYBOARD }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keyboard Key",
                            color = if (actionType == ActionType.KEYBOARD) Color.White else Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (actionType == ActionType.MOUSE_BUTTON) GamingCyan else Color.Transparent)
                            .clickable { actionType = ActionType.MOUSE_BUTTON }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mouse Click",
                            color = if (actionType == ActionType.MOUSE_BUTTON) GamingNavyDark else Color.Gray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Label Input
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Button Label") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = GamingCyan,
                        unfocusedBorderColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (actionType == ActionType.KEYBOARD) {
                    Text(
                        text = "Quick Key Preset:",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(CommonKeyCatalog.commonKeys) { keyDef ->
                            val isSelected = code == keyDef.code
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) GamingBlue else GamingNavySurfaceVariant)
                                    .clickable {
                                        key = keyDef.key
                                        code = keyDef.code
                                        keyCode = keyDef.keyCode
                                        if (label.isBlank() || label.length <= 3) {
                                            label = keyDef.code.removePrefix("Key").take(4)
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = keyDef.label,
                                    color = if (isSelected) Color.White else Color.LightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Assigned: code=$code (keyCode=$keyCode)",
                        color = GamingCyan,
                        fontSize = 12.sp
                    )
                } else {
                    Text(
                        text = "Mouse Button:",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            0 to "Left Click (Fire)",
                            2 to "Right Click (Aim)",
                            1 to "Middle Click (Scroll)"
                        ).forEach { (btnIdx, title) ->
                            val isSelected = mouseButton == btnIdx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) GamingCyan else GamingNavySurfaceVariant)
                                    .clickable {
                                        mouseButton = btnIdx
                                        if (label.isBlank()) {
                                            label = if (btnIdx == 0) "LMB" else if (btnIdx == 2) "RMB" else "MMB"
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSelected) GamingNavyDark else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Button Size", color = Color.LightGray, fontSize = 12.sp)
                    Text("${sizeDp.toInt()} dp", color = GamingCyan, fontSize = 12.sp)
                }
                Slider(
                    value = sizeDp,
                    onValueChange = { sizeDp = it },
                    valueRange = 44f..90f,
                    colors = SliderDefaults.colors(
                        thumbColor = GamingCyan,
                        activeTrackColor = GamingBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updated = button.copy(
                                label = if (label.isBlank()) "BTN" else label,
                                actionType = actionType,
                                key = key,
                                code = code,
                                keyCode = keyCode,
                                mouseButton = mouseButton,
                                sizeDp = sizeDp
                            )
                            onSave(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingBlue)
                    ) {
                        Text("Save Button", color = Color.White)
                    }
                }
            }
        }
    }
}

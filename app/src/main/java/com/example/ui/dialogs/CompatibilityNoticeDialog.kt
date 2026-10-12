package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface
import com.example.ui.theme.GamingNavySurfaceVariant

@Composable
fun CompatibilityNoticeDialog(
    onDismiss: () -> Unit
) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AccentAmber.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = AccentAmber,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Virtual Controls Compatibility",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "GameBridge Architecture by Nikssxelo",
                            color = GamingCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "How GameBridge Works:",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = "GameBridge embeds modern Android WebView hardware acceleration and overlays custom touchscreen buttons and joysticks. When you tap controls, the app injects standard W3C KeyboardEvent and MouseEvent structures into the active page DOM.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                NoticeCard(
                    title = "1. Synthetic Event Limitations (isTrusted)",
                    description = "Some competitive multiplayer games or anti-cheat engines inspect the standard JavaScript 'event.isTrusted' property. Synthetic events generated from script have isTrusted=false and may be filtered out by strict games.",
                    iconColor = AccentAmber
                )

                Spacer(modifier = Modifier.height(8.dp))

                NoticeCard(
                    title = "2. Desktop Pointer Lock API",
                    description = "Certain 3D first-person desktop games expect physical mouse pointer locking (movementX/movementY via requestPointerLock). While GameBridge dispatches simulated mousemove events through our camera touch area, true hardware mouse locking requires physical USB/Bluetooth peripherals.",
                    iconColor = GamingCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                NoticeCard(
                    title = "3. Best Game Compatibility",
                    description = "HTML5 canvas games, 2D platformers, arcade games (WASD / Arrows / Space), racing games, and web emulators offer outstanding out-of-the-box compatibility! Use our Quick Launch bookmark list to explore verified games.",
                    iconColor = GamingBlue
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingBlue),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Got It, Let's Play!", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NoticeCard(
    title: String,
    description: String,
    iconColor: Color
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = GamingNavySurfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                color = iconColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = Color(0xFFE2E8F0),
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        }
    }
}

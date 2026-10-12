package com.example.ui.browser

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface
import com.example.ui.theme.GamingNavySurfaceVariant

data class QuickGame(
    val title: String,
    val category: String,
    val url: String,
    val recommendedPresetId: String,
    val description: String
)

val CURATED_GAMES = listOf(
    QuickGame(
        title = "Slither.io",
        category = "Action / Multiplayer",
        url = "https://slither.io",
        recommendedPresetId = "preset_fps",
        description = "Mouse steer, Space / Left-click boost"
    ),
    QuickGame(
        title = "1v1.LOL",
        category = "3D Shooter & Building",
        url = "https://1v1.lol",
        recommendedPresetId = "preset_fps",
        description = "WASD movement, Left-click fire, Space jump"
    ),
    QuickGame(
        title = "Paper.io 2",
        category = "Territory Multiplayer",
        url = "https://paper-io.com",
        recommendedPresetId = "preset_fps",
        description = "Analog steering and directional conquest"
    ),
    QuickGame(
        title = "Retro Bowl",
        category = "Sports / Football",
        url = "https://gamebridge-retrobowl.pages.dev",
        recommendedPresetId = "preset_platformer",
        description = "Space, W/A/S/D passing and sprinting"
    ),
    QuickGame(
        title = "2048 HTML5",
        category = "Puzzle Classic",
        url = "https://play2048.co",
        recommendedPresetId = "preset_platformer",
        description = "W, A, S, D and Arrow directional slides"
    ),
    QuickGame(
        title = "Cookie Clicker",
        category = "Casual Clicker",
        url = "https://orteil.dashnet.org/cookieclicker",
        recommendedPresetId = "preset_fps",
        description = "Left mouse click button spam & upgrades"
    ),
    QuickGame(
        title = "WebGL Water Simulation",
        category = "WebGL 3D Demo",
        url = "https://madebyevan.com/webgl-water",
        recommendedPresetId = "preset_fps",
        description = "Camera look trackpad and fluid physics"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLaunchSheet(
    onDismiss: () -> Unit,
    onLaunchGame: (game: QuickGame) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = GamingNavySurface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GamingBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = GamingCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GameBridge Quick Launch",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Curated Browser Games by Nikssxelo",
                            color = GamingCyan,
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(CURATED_GAMES) { game ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GamingNavySurfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLaunchGame(game)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = game.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GamingBlue.copy(alpha = 0.3f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = game.category,
                                            color = GamingCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = game.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = game.url,
                                    color = Color(0xFF64748B),
                                    fontSize = 10.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = "Play",
                                tint = GamingCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

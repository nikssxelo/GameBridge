package com.example.ui.browser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.GamingBlue
import com.example.ui.theme.GamingCyan
import com.example.ui.theme.GamingNavyDark
import com.example.ui.theme.GamingNavySurface
import com.example.ui.theme.GamingNavySurfaceVariant

enum class ScreenOrientationState {
    LANDSCAPE,
    PORTRAIT,
    SENSOR
}

@Composable
fun BrowserToolbar(
    currentUrl: String,
    pageTitle: String,
    isLoading: Boolean,
    loadProgress: Int,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isFullscreen: Boolean,
    isOverlayVisible: Boolean,
    isEditMode: Boolean,
    orientationState: ScreenOrientationState,
    onNavigateUrl: (url: String) -> Unit,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit,
    onReload: () -> Unit,
    onGoHome: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleOrientation: () -> Unit,
    onToggleOverlay: () -> Unit,
    onToggleEditMode: () -> Unit,
    onOpenPresets: () -> Unit,
    onOpenQuickLaunch: () -> Unit,
    onOpenCompatibilityNotice: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(!isFullscreen) }
    var inputUrl by remember(currentUrl) { mutableStateOf(currentUrl) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(GamingNavySurface.copy(alpha = if (isFullscreen) 0.85f else 0.98f))
    ) {
        // Collapsed Micro Bar (Floating pill header for maximum gameplay visibility)
        if (!isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isExpanded = true }
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GamingBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gamepad,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (pageTitle.isNotBlank()) pageTitle.take(20) else "GameBridge",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Right: Quick mini action pills
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Toggle Overlay
                    IconButton(
                        onClick = onToggleOverlay,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isOverlayVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Overlay",
                            tint = if (isOverlayVisible) GamingCyan else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Fullscreen Toggle
                    IconButton(
                        onClick = onToggleFullscreen,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = GamingBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Expand Bar
                    IconButton(
                        onClick = { isExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand Bar",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Expanded Complete Navigation Bar
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                // Top Row: Brand & Function toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(GamingBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "GameBridge",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "by Nikssxelo",
                                color = GamingCyan,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Header Action Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Quick Launch games
                        IconButton(
                            onClick = onOpenQuickLaunch,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
                                contentDescription = "Quick Games",
                                tint = GamingCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Presets & Settings
                        IconButton(
                            onClick = onOpenPresets,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = "Control Presets",
                                tint = GamingBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Edit Mode
                        IconButton(
                            onClick = onToggleEditMode,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Layout",
                                tint = if (isEditMode) Color.Yellow else Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Orientation toggle
                        IconButton(
                            onClick = onToggleOrientation,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ScreenRotation,
                                contentDescription = "Orientation: $orientationState",
                                tint = when (orientationState) {
                                    ScreenOrientationState.LANDSCAPE -> AccentGreen
                                    ScreenOrientationState.PORTRAIT -> GamingCyan
                                    ScreenOrientationState.SENSOR -> Color.LightGray
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Fullscreen
                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Compatibility Info
                        IconButton(
                            onClick = onOpenCompatibilityNotice,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Compatibility Notice",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Minimize bar
                        IconButton(
                            onClick = { isExpanded = false },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Collapse Toolbar",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Row: Navigation buttons + Address bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onGoBack,
                        enabled = canGoBack,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (canGoBack) Color.White else Color.DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onGoForward,
                        enabled = canGoForward,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (canGoForward) Color.White else Color.DarkGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onReload,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onGoHome,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Address input field
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.White),
                        leadingIcon = {
                            Icon(
                                imageVector = if (inputUrl.startsWith("https://")) Icons.Default.Lock else Icons.Default.Search,
                                contentDescription = null,
                                tint = if (inputUrl.startsWith("https://")) GamingCyan else Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clip(CircleShape)
                                    .background(GamingBlue)
                                    .clickable { onNavigateUrl(inputUrl) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "GO",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GamingNavyDark,
                            unfocusedContainerColor = GamingNavyDark,
                            focusedBorderColor = GamingCyan,
                            unfocusedBorderColor = GamingNavySurfaceVariant
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = { onNavigateUrl(inputUrl) }
                        )
                    )
                }
            }
        }

        // Web Page Load Progress Indicator
        if (isLoading) {
            LinearProgressIndicator(
                progress = { loadProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp),
                color = GamingCyan,
                trackColor = Color.Transparent
            )
        }
    }
}

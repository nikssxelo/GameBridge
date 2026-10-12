package com.example.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.data.ControlPreferencesRepository
import com.example.model.ControlProfile
import com.example.model.DefaultProfiles
import com.example.ui.browser.BrowserToolbar
import com.example.ui.browser.GameWebView
import com.example.ui.browser.QuickLaunchSheet
import com.example.ui.browser.ScreenOrientationState
import com.example.ui.browser.WebViewCommander
import com.example.ui.dialogs.CompatibilityNoticeDialog
import com.example.ui.dialogs.ProfileSelectorDialog
import com.example.ui.overlay.VirtualOverlay
import com.example.ui.theme.GamingNavyDark
import com.example.web.GameBridgeScript
import kotlinx.coroutines.launch

@Composable
fun GameBridgeApp() {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val repository = remember { ControlPreferencesRepository(context) }
    val commander = remember { WebViewCommander() }

    // Navigation and Page States
    var currentUrl by remember { mutableStateOf("https://slither.io") }
    var pageTitle by remember { mutableStateOf("GameBridge Gaming Browser") }
    var isLoading by remember { mutableStateOf(false) }
    var loadProgress by remember { mutableIntStateOf(0) }
    var canGoBack by remember { mutableStateOf(false) }
    var canGoForward by remember { mutableStateOf(false) }

    // Controls & Layout States
    var activeProfile by remember { mutableStateOf(repository.loadProfile(repository.getActiveProfileId())) }
    var isOverlayVisible by remember { mutableStateOf(true) }
    var isEditMode by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var orientationState by remember { mutableStateOf(ScreenOrientationState.SENSOR) }

    // Dialog visibility states
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showCompatibilityDialog by remember { mutableStateOf(false) }
    var showQuickLaunchSheet by remember { mutableStateOf(false) }

    // Update screen orientation when user toggles
    LaunchedEffect(orientationState) {
        activity?.let { act ->
            act.requestedOrientation = when (orientationState) {
                ScreenOrientationState.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                ScreenOrientationState.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                ScreenOrientationState.SENSOR -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    // Update system window bars for immersive fullscreen gaming
    LaunchedEffect(isFullscreen) {
        activity?.let { act ->
            val window = act.window
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (isFullscreen) {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // When page changes, automatically load any customized profile saved specifically for that host
    LaunchedEffect(currentUrl) {
        val mapped = repository.getProfileForUrl(currentUrl)
        activeProfile = mapped
    }

    // Android back button handling
    BackHandler {
        if (isEditMode) {
            isEditMode = false
        } else if (isFullscreen) {
            isFullscreen = false
        } else if (canGoBack) {
            commander.goBack()
        } else {
            activity?.finish()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = GamingNavyDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GamingNavyDark)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Browser Navigation Bar
                BrowserToolbar(
                    currentUrl = currentUrl,
                    pageTitle = pageTitle,
                    isLoading = isLoading,
                    loadProgress = loadProgress,
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    isFullscreen = isFullscreen,
                    isOverlayVisible = isOverlayVisible,
                    isEditMode = isEditMode,
                    orientationState = orientationState,
                    onNavigateUrl = { target ->
                        commander.loadUrl(target)
                    },
                    onGoBack = { commander.goBack() },
                    onGoForward = { commander.goForward() },
                    onReload = { commander.reload() },
                    onGoHome = { showQuickLaunchSheet = true },
                    onToggleFullscreen = { isFullscreen = !isFullscreen },
                    onToggleOrientation = {
                        orientationState = when (orientationState) {
                            ScreenOrientationState.SENSOR -> ScreenOrientationState.LANDSCAPE
                            ScreenOrientationState.LANDSCAPE -> ScreenOrientationState.PORTRAIT
                            ScreenOrientationState.PORTRAIT -> ScreenOrientationState.SENSOR
                        }
                    },
                    onToggleOverlay = { isOverlayVisible = !isOverlayVisible },
                    onToggleEditMode = { isEditMode = !isEditMode },
                    onOpenPresets = { showPresetsDialog = true },
                    onOpenQuickLaunch = { showQuickLaunchSheet = true },
                    onOpenCompatibilityNotice = { showCompatibilityDialog = true },
                    modifier = if (isFullscreen) Modifier else Modifier.statusBarsPadding()
                )

                // Main Game Screen Area with WebView and Controls
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    // Layer 1: Hardware-Accelerated WebView
                    GameWebView(
                        initialUrl = currentUrl,
                        commander = commander,
                        onUrlChanged = { newUrl ->
                            currentUrl = newUrl
                            repository.setLastVisitedUrl(newUrl)
                        },
                        onTitleChanged = { pageTitle = it },
                        onLoadingStateChanged = { loading, progress ->
                            isLoading = loading
                            loadProgress = progress
                        },
                        onNavStateChanged = { back, forward ->
                            canGoBack = back
                            canGoForward = forward
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Layer 2: Virtual Gaming Control Overlay
                    VirtualOverlay(
                        profile = activeProfile,
                        isVisible = isOverlayVisible,
                        isEditMode = isEditMode,
                        onKeyDown = { key, code, keyCode ->
                            val js = GameBridgeScript.buildKeyDownJs(key, code, keyCode)
                            commander.evaluateJs(js)
                        },
                        onKeyUp = { key, code, keyCode ->
                            val js = GameBridgeScript.buildKeyUpJs(key, code, keyCode)
                            commander.evaluateJs(js)
                        },
                        onMouseDown = { button ->
                            val js = GameBridgeScript.buildMouseDownJs(button)
                            commander.evaluateJs(js)
                        },
                        onMouseUp = { button ->
                            val js = GameBridgeScript.buildMouseUpJs(button)
                            commander.evaluateJs(js)
                        },
                        onMouseMove = { dx, dy ->
                            val js = GameBridgeScript.buildMouseMoveJs(dx, dy)
                            commander.evaluateJs(js)
                        },
                        onProfileChanged = { updated ->
                            activeProfile = updated
                        },
                        onSaveLayout = {
                            repository.saveProfile(activeProfile)
                            scope.launch {
                                snackbarHostState.showSnackbar("Layout saved successfully!")
                            }
                        },
                        onExitEditMode = {
                            isEditMode = false
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Presets & Tuning Dialog
    if (showPresetsDialog) {
        ProfileSelectorDialog(
            currentProfile = activeProfile,
            currentUrl = currentUrl,
            onDismiss = { showPresetsDialog = false },
            onSelectPreset = { presetId ->
                val newProfile = repository.loadProfile(presetId)
                activeProfile = newProfile
                repository.setActiveProfileId(presetId)
                Toast.makeText(context, "Loaded ${newProfile.name}", Toast.LENGTH_SHORT).show()
            },
            onSaveForThisUrl = {
                repository.saveProfile(activeProfile)
                repository.bindProfileToUrl(currentUrl, activeProfile.id)
                Toast.makeText(context, "Saved profile for this site", Toast.LENGTH_SHORT).show()
            },
            onResetToDefaults = {
                val reset = repository.resetProfileToDefault(activeProfile.id)
                activeProfile = reset
                Toast.makeText(context, "Reset to defaults", Toast.LENGTH_SHORT).show()
            },
            onUpdateProfileSettings = { opacity, haptic, sensitivity, joystickSize ->
                val updatedJoy = activeProfile.joystick.copy(sizeDp = joystickSize)
                val updatedTrack = activeProfile.trackpad.copy(sensitivity = sensitivity)
                activeProfile = activeProfile.copy(
                    opacity = opacity,
                    hapticFeedback = haptic,
                    joystick = updatedJoy,
                    trackpad = updatedTrack
                )
                repository.saveProfile(activeProfile)
            }
        )
    }

    // Compatibility Notice Dialog
    if (showCompatibilityDialog) {
        CompatibilityNoticeDialog(
            onDismiss = { showCompatibilityDialog = false }
        )
    }

    // Quick Launch Browser Game Sheet
    if (showQuickLaunchSheet) {
        QuickLaunchSheet(
            onDismiss = { showQuickLaunchSheet = false },
            onLaunchGame = { game ->
                currentUrl = game.url
                commander.loadUrl(game.url)
                val preset = repository.loadProfile(game.recommendedPresetId)
                activeProfile = preset
                Toast.makeText(context, "Launching ${game.title}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

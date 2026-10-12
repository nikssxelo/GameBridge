package com.example

import com.example.model.ActionType
import com.example.model.DefaultProfiles
import com.example.ui.browser.formatGameUrl
import com.example.web.GameBridgeScript
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameBridgeLogicTest {

    @Test
    fun defaultProfiles_haveCorrectPresets() {
        val fps = DefaultProfiles.getFpsProfile()
        assertEquals("preset_fps", fps.id)
        assertTrue(fps.joystick.enabled)
        assertTrue(fps.trackpad.enabled)
        assertTrue(fps.buttons.any { it.label == "FIRE" && it.actionType == ActionType.MOUSE_BUTTON })
        assertTrue(fps.buttons.any { it.label == "JUMP" && it.code == "Space" })

        val platformer = DefaultProfiles.getPlatformerProfile()
        assertEquals("preset_platformer", platformer.id)
        assertTrue(platformer.buttons.any { it.label.contains("JUMP") })

        val driving = DefaultProfiles.getDrivingProfile()
        assertEquals("preset_driving", driving.id)
        assertTrue(driving.buttons.any { it.label.contains("GAS") })
    }

    @Test
    fun scriptGenerator_buildsValidJavaScript() {
        val keyDownJs = GameBridgeScript.buildKeyDownJs("w", "KeyW", 87)
        assertTrue(keyDownJs.contains("__gbDispatchKey"))
        assertTrue(keyDownJs.contains("keydown"))
        assertTrue(keyDownJs.contains("KeyW"))
        assertTrue(keyDownJs.contains("87"))

        val mouseMoveJs = GameBridgeScript.buildMouseMoveJs(12.5f, -8.3f)
        assertTrue(mouseMoveJs.contains("__gbMouseMove"))
        assertTrue(mouseMoveJs.contains("12"))
        assertTrue(mouseMoveJs.contains("-8"))

        val mouseDownJs = GameBridgeScript.buildMouseDownJs(0)
        assertTrue(mouseDownJs.contains("__gbDispatchMouse"))
        assertTrue(mouseDownJs.contains("mousedown"))
        assertTrue(mouseDownJs.contains("0"))
    }

    @Test
    fun formatGameUrl_handlesVariousFormats() {
        assertEquals("https://slither.io", formatGameUrl("https://slither.io"))
        assertEquals("https://krunker.io", formatGameUrl("krunker.io"))
        assertTrue(formatGameUrl("retro bowl html5").contains("duckduckgo.com"))
    }
}

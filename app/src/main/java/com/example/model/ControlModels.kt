package com.example.model

enum class ActionType {
    KEYBOARD,
    MOUSE_BUTTON
}

data class KeyDefinition(
    val label: String,
    val key: String,
    val code: String,
    val keyCode: Int
)

data class VirtualButtonConfig(
    val id: String,
    val label: String,
    val actionType: ActionType = ActionType.KEYBOARD,
    val key: String = " ",
    val code: String = "Space",
    val keyCode: Int = 32,
    val mouseButton: Int = 0, // 0 = Left, 1 = Middle, 2 = Right
    val xRatio: Float, // 0.0 to 1.0 screen relative
    val yRatio: Float, // 0.0 to 1.0 screen relative
    val sizeDp: Float = 60f
)

data class DirectionalKey(
    val key: String,
    val code: String,
    val keyCode: Int
)

data class JoystickConfig(
    val enabled: Boolean = true,
    val xRatio: Float = 0.16f,
    val yRatio: Float = 0.72f,
    val sizeDp: Float = 140f,
    val deadZone: Float = 0.15f,
    val upKey: DirectionalKey = DirectionalKey("w", "KeyW", 87),
    val downKey: DirectionalKey = DirectionalKey("s", "KeyS", 83),
    val leftKey: DirectionalKey = DirectionalKey("a", "KeyA", 65),
    val rightKey: DirectionalKey = DirectionalKey("d", "KeyD", 68)
)

data class TrackpadConfig(
    val enabled: Boolean = true,
    val xRatio: Float = 0.76f,
    val yRatio: Float = 0.45f,
    val widthDp: Float = 170f,
    val heightDp: Float = 150f,
    val sensitivity: Float = 1.4f
)

data class ControlProfile(
    val id: String,
    val name: String,
    val opacity: Float = 0.75f,
    val hapticFeedback: Boolean = true,
    val joystick: JoystickConfig = JoystickConfig(),
    val trackpad: TrackpadConfig = TrackpadConfig(),
    val buttons: List<VirtualButtonConfig> = emptyList()
)

object CommonKeyCatalog {
    val commonKeys = listOf(
        KeyDefinition("Space (Jump)", " ", "Space", 32),
        KeyDefinition("Shift (Sprint)", "Shift", "ShiftLeft", 16),
        KeyDefinition("Ctrl (Crouch)", "Control", "ControlLeft", 17),
        KeyDefinition("Alt (Walk)", "Alt", "AltLeft", 18),
        KeyDefinition("E (Interact)", "e", "KeyE", 69),
        KeyDefinition("F (Action)", "f", "KeyF", 70),
        KeyDefinition("R (Reload)", "r", "KeyR", 82),
        KeyDefinition("Q (Ability)", "q", "KeyQ", 81),
        KeyDefinition("C (Crouch/Cam)", "c", "KeyC", 67),
        KeyDefinition("V (Melee/View)", "v", "KeyV", 86),
        KeyDefinition("Z (Prone/Skill)", "z", "KeyZ", 90),
        KeyDefinition("X (Special)", "x", "KeyX", 88),
        KeyDefinition("G (Grenade)", "g", "KeyG", 71),
        KeyDefinition("Tab (Score/Inv)", "Tab", "Tab", 9),
        KeyDefinition("Enter (Start)", "Enter", "Enter", 13),
        KeyDefinition("Escape (Menu)", "Escape", "Escape", 27),
        KeyDefinition("1 (Weapon 1)", "1", "Digit1", 49),
        KeyDefinition("2 (Weapon 2)", "2", "Digit2", 50),
        KeyDefinition("3 (Weapon 3)", "3", "Digit3", 51),
        KeyDefinition("4 (Weapon 4)", "4", "Digit4", 52),
        KeyDefinition("W (Up)", "w", "KeyW", 87),
        KeyDefinition("A (Left)", "a", "KeyA", 65),
        KeyDefinition("S (Down)", "s", "KeyS", 83),
        KeyDefinition("D (Right)", "d", "KeyD", 68),
        KeyDefinition("Arrow Up", "ArrowUp", "ArrowUp", 38),
        KeyDefinition("Arrow Down", "ArrowDown", "ArrowDown", 40),
        KeyDefinition("Arrow Left", "ArrowLeft", "ArrowLeft", 37),
        KeyDefinition("Arrow Right", "ArrowRight", "ArrowRight", 39)
    )
}

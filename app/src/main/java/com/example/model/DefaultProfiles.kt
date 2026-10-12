package com.example.model

object DefaultProfiles {

    fun getFpsProfile(): ControlProfile {
        return ControlProfile(
            id = "preset_fps",
            name = "FPS (WASD + Mouse)",
            opacity = 0.75f,
            hapticFeedback = true,
            joystick = JoystickConfig(
                enabled = true,
                xRatio = 0.16f,
                yRatio = 0.72f,
                sizeDp = 140f
            ),
            trackpad = TrackpadConfig(
                enabled = true,
                xRatio = 0.76f,
                yRatio = 0.45f,
                widthDp = 180f,
                heightDp = 150f,
                sensitivity = 1.5f
            ),
            buttons = listOf(
                VirtualButtonConfig(
                    id = "fps_fire",
                    label = "FIRE",
                    actionType = ActionType.MOUSE_BUTTON,
                    mouseButton = 0, // Left Click
                    xRatio = 0.88f,
                    yRatio = 0.74f,
                    sizeDp = 68f
                ),
                VirtualButtonConfig(
                    id = "fps_aim",
                    label = "AIM",
                    actionType = ActionType.MOUSE_BUTTON,
                    mouseButton = 2, // Right Click
                    xRatio = 0.75f,
                    yRatio = 0.78f,
                    sizeDp = 58f
                ),
                VirtualButtonConfig(
                    id = "fps_jump",
                    label = "JUMP",
                    actionType = ActionType.KEYBOARD,
                    key = " ",
                    code = "Space",
                    keyCode = 32,
                    xRatio = 0.90f,
                    yRatio = 0.52f,
                    sizeDp = 60f
                ),
                VirtualButtonConfig(
                    id = "fps_sprint",
                    label = "SPRINT",
                    actionType = ActionType.KEYBOARD,
                    key = "Shift",
                    code = "ShiftLeft",
                    keyCode = 16,
                    xRatio = 0.16f,
                    yRatio = 0.44f,
                    sizeDp = 54f
                ),
                VirtualButtonConfig(
                    id = "fps_reload",
                    label = "RELOAD (R)",
                    actionType = ActionType.KEYBOARD,
                    key = "r",
                    code = "KeyR",
                    keyCode = 82,
                    xRatio = 0.80f,
                    yRatio = 0.25f,
                    sizeDp = 52f
                ),
                VirtualButtonConfig(
                    id = "fps_interact",
                    label = "USE (E)",
                    actionType = ActionType.KEYBOARD,
                    key = "e",
                    code = "KeyE",
                    keyCode = 69,
                    xRatio = 0.92f,
                    yRatio = 0.35f,
                    sizeDp = 52f
                ),
                VirtualButtonConfig(
                    id = "fps_action",
                    label = "ACTION (F)",
                    actionType = ActionType.KEYBOARD,
                    key = "f",
                    code = "KeyF",
                    keyCode = 70,
                    xRatio = 0.68f,
                    yRatio = 0.30f,
                    sizeDp = 50f
                ),
                VirtualButtonConfig(
                    id = "fps_crouch",
                    label = "CROUCH (C)",
                    actionType = ActionType.KEYBOARD,
                    key = "c",
                    code = "KeyC",
                    keyCode = 67,
                    xRatio = 0.64f,
                    yRatio = 0.76f,
                    sizeDp = 50f
                )
            )
        )
    }

    fun getPlatformerProfile(): ControlProfile {
        return ControlProfile(
            id = "preset_platformer",
            name = "Platformer (Runner)",
            opacity = 0.8f,
            hapticFeedback = true,
            joystick = JoystickConfig(
                enabled = true,
                xRatio = 0.18f,
                yRatio = 0.72f,
                sizeDp = 140f
            ),
            trackpad = TrackpadConfig(
                enabled = false, // Trackpad disabled for 2D platformers
                xRatio = 0.5f,
                yRatio = 0.5f
            ),
            buttons = listOf(
                VirtualButtonConfig(
                    id = "plat_jump",
                    label = "JUMP (Space)",
                    actionType = ActionType.KEYBOARD,
                    key = " ",
                    code = "Space",
                    keyCode = 32,
                    xRatio = 0.88f,
                    yRatio = 0.72f,
                    sizeDp = 68f
                ),
                VirtualButtonConfig(
                    id = "plat_run",
                    label = "RUN (Shift)",
                    actionType = ActionType.KEYBOARD,
                    key = "Shift",
                    code = "ShiftLeft",
                    keyCode = 16,
                    xRatio = 0.76f,
                    yRatio = 0.80f,
                    sizeDp = 58f
                ),
                VirtualButtonConfig(
                    id = "plat_action",
                    label = "ATTACK (Z)",
                    actionType = ActionType.KEYBOARD,
                    key = "z",
                    code = "KeyZ",
                    keyCode = 90,
                    xRatio = 0.76f,
                    yRatio = 0.62f,
                    sizeDp = 58f
                ),
                VirtualButtonConfig(
                    id = "plat_special",
                    label = "SKILL (X)",
                    actionType = ActionType.KEYBOARD,
                    key = "x",
                    code = "KeyX",
                    keyCode = 88,
                    xRatio = 0.88f,
                    yRatio = 0.52f,
                    sizeDp = 56f
                ),
                VirtualButtonConfig(
                    id = "plat_up",
                    label = "UP (W)",
                    actionType = ActionType.KEYBOARD,
                    key = "w",
                    code = "KeyW",
                    keyCode = 87,
                    xRatio = 0.18f,
                    yRatio = 0.44f,
                    sizeDp = 52f
                )
            )
        )
    }

    fun getDrivingProfile(): ControlProfile {
        return ControlProfile(
            id = "preset_driving",
            name = "Driving / Racing",
            opacity = 0.8f,
            hapticFeedback = true,
            joystick = JoystickConfig(
                enabled = true,
                xRatio = 0.18f,
                yRatio = 0.72f,
                sizeDp = 140f
            ),
            trackpad = TrackpadConfig(
                enabled = false,
                xRatio = 0.5f,
                yRatio = 0.5f
            ),
            buttons = listOf(
                VirtualButtonConfig(
                    id = "drive_gas",
                    label = "GAS (W)",
                    actionType = ActionType.KEYBOARD,
                    key = "w",
                    code = "KeyW",
                    keyCode = 87,
                    xRatio = 0.88f,
                    yRatio = 0.65f,
                    sizeDp = 70f
                ),
                VirtualButtonConfig(
                    id = "drive_brake",
                    label = "BRAKE (S)",
                    actionType = ActionType.KEYBOARD,
                    key = "s",
                    code = "KeyS",
                    keyCode = 83,
                    xRatio = 0.74f,
                    yRatio = 0.75f,
                    sizeDp = 62f
                ),
                VirtualButtonConfig(
                    id = "drive_handbrake",
                    label = "DRIFT (Space)",
                    actionType = ActionType.KEYBOARD,
                    key = " ",
                    code = "Space",
                    keyCode = 32,
                    xRatio = 0.88f,
                    yRatio = 0.45f,
                    sizeDp = 60f
                ),
                VirtualButtonConfig(
                    id = "drive_nitro",
                    label = "BOOST (Shift)",
                    actionType = ActionType.KEYBOARD,
                    key = "Shift",
                    code = "ShiftLeft",
                    keyCode = 16,
                    xRatio = 0.74f,
                    yRatio = 0.55f,
                    sizeDp = 56f
                ),
                VirtualButtonConfig(
                    id = "drive_reset",
                    label = "RESET (R)",
                    actionType = ActionType.KEYBOARD,
                    key = "r",
                    code = "KeyR",
                    keyCode = 82,
                    xRatio = 0.88f,
                    yRatio = 0.28f,
                    sizeDp = 50f
                ),
                VirtualButtonConfig(
                    id = "drive_cam",
                    label = "CAM (C)",
                    actionType = ActionType.KEYBOARD,
                    key = "c",
                    code = "KeyC",
                    keyCode = 67,
                    xRatio = 0.76f,
                    yRatio = 0.35f,
                    sizeDp = 50f
                )
            )
        )
    }

    val allDefaultPresets = listOf(
        getFpsProfile(),
        getPlatformerProfile(),
        getDrivingProfile()
    )
}

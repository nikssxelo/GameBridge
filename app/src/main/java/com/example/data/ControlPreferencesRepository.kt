package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.model.ActionType
import com.example.model.ControlProfile
import com.example.model.DefaultProfiles
import com.example.model.DirectionalKey
import com.example.model.JoystickConfig
import com.example.model.TrackpadConfig
import com.example.model.VirtualButtonConfig
import org.json.JSONArray
import org.json.JSONObject

class ControlPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("gamebridge_control_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
        private const val KEY_LAST_URL = "last_visited_url"
        private const val PREFIX_CUSTOM_PROFILE = "profile_"
        private const val PREFIX_URL_MAPPING = "url_profile_"
    }

    fun getLastVisitedUrl(): String {
        return prefs.getString(KEY_LAST_URL, "https://nikssxelo.github.io") ?: "https://nikssxelo.github.io"
    }

    fun setLastVisitedUrl(url: String) {
        prefs.edit().putString(KEY_LAST_URL, url).apply()
    }

    fun getActiveProfileId(): String {
        return prefs.getString(KEY_ACTIVE_PROFILE_ID, "preset_fps") ?: "preset_fps"
    }

    fun setActiveProfileId(id: String) {
        prefs.edit().putString(KEY_ACTIVE_PROFILE_ID, id).apply()
    }

    fun getProfileForUrl(url: String): ControlProfile {
        val host = runCatching { Uri.parse(url).host }.getOrNull()
        if (!host.isNullOrBlank()) {
            val mappedProfileId = prefs.getString("$PREFIX_URL_MAPPING$host", null)
            if (mappedProfileId != null) {
                return loadProfile(mappedProfileId)
            }
        }
        return loadProfile(getActiveProfileId())
    }

    fun bindProfileToUrl(url: String, profileId: String) {
        val host = runCatching { Uri.parse(url).host }.getOrNull()
        if (!host.isNullOrBlank()) {
            prefs.edit().putString("$PREFIX_URL_MAPPING$host", profileId).apply()
        }
    }

    fun loadProfile(profileId: String): ControlProfile {
        // First check if user saved a customized version in SharedPreferences
        val jsonStr = prefs.getString("$PREFIX_CUSTOM_PROFILE$profileId", null)
        if (!jsonStr.isNullOrBlank()) {
            val parsed = parseProfileFromJson(jsonStr)
            if (parsed != null) return parsed
        }

        // Otherwise return matching preset or default FPS
        return when (profileId) {
            "preset_platformer" -> DefaultProfiles.getPlatformerProfile()
            "preset_driving" -> DefaultProfiles.getDrivingProfile()
            else -> DefaultProfiles.getFpsProfile()
        }
    }

    fun saveProfile(profile: ControlProfile) {
        val json = serializeProfileToJson(profile)
        prefs.edit()
            .putString("$PREFIX_CUSTOM_PROFILE${profile.id}", json.toString())
            .putString(KEY_ACTIVE_PROFILE_ID, profile.id)
            .apply()
    }

    fun resetProfileToDefault(profileId: String): ControlProfile {
        prefs.edit().remove("$PREFIX_CUSTOM_PROFILE$profileId").apply()
        return when (profileId) {
            "preset_platformer" -> DefaultProfiles.getPlatformerProfile()
            "preset_driving" -> DefaultProfiles.getDrivingProfile()
            else -> DefaultProfiles.getFpsProfile()
        }
    }

    private fun serializeProfileToJson(profile: ControlProfile): JSONObject {
        val root = JSONObject()
        root.put("id", profile.id)
        root.put("name", profile.name)
        root.put("opacity", profile.opacity.toDouble())
        root.put("hapticFeedback", profile.hapticFeedback)

        // Joystick
        val joyObj = JSONObject()
        joyObj.put("enabled", profile.joystick.enabled)
        joyObj.put("xRatio", profile.joystick.xRatio.toDouble())
        joyObj.put("yRatio", profile.joystick.yRatio.toDouble())
        joyObj.put("sizeDp", profile.joystick.sizeDp.toDouble())
        joyObj.put("deadZone", profile.joystick.deadZone.toDouble())
        joyObj.put("upKey", serializeDirectionalKey(profile.joystick.upKey))
        joyObj.put("downKey", serializeDirectionalKey(profile.joystick.downKey))
        joyObj.put("leftKey", serializeDirectionalKey(profile.joystick.leftKey))
        joyObj.put("rightKey", serializeDirectionalKey(profile.joystick.rightKey))
        root.put("joystick", joyObj)

        // Trackpad
        val trackObj = JSONObject()
        trackObj.put("enabled", profile.trackpad.enabled)
        trackObj.put("xRatio", profile.trackpad.xRatio.toDouble())
        trackObj.put("yRatio", profile.trackpad.yRatio.toDouble())
        trackObj.put("widthDp", profile.trackpad.widthDp.toDouble())
        trackObj.put("heightDp", profile.trackpad.heightDp.toDouble())
        trackObj.put("sensitivity", profile.trackpad.sensitivity.toDouble())
        root.put("trackpad", trackObj)

        // Buttons
        val buttonsArr = JSONArray()
        profile.buttons.forEach { btn ->
            val bObj = JSONObject()
            bObj.put("id", btn.id)
            bObj.put("label", btn.label)
            bObj.put("actionType", btn.actionType.name)
            bObj.put("key", btn.key)
            bObj.put("code", btn.code)
            bObj.put("keyCode", btn.keyCode)
            bObj.put("mouseButton", btn.mouseButton)
            bObj.put("xRatio", btn.xRatio.toDouble())
            bObj.put("yRatio", btn.yRatio.toDouble())
            bObj.put("sizeDp", btn.sizeDp.toDouble())
            buttonsArr.put(bObj)
        }
        root.put("buttons", buttonsArr)

        return root
    }

    private fun serializeDirectionalKey(dirKey: DirectionalKey): JSONObject {
        val obj = JSONObject()
        obj.put("key", dirKey.key)
        obj.put("code", dirKey.code)
        obj.put("keyCode", dirKey.keyCode)
        return obj
    }

    private fun parseProfileFromJson(jsonStr: String): ControlProfile? {
        return try {
            val root = JSONObject(jsonStr)
            val id = root.optString("id", "preset_custom")
            val name = root.optString("name", "Custom")
            val opacity = root.optDouble("opacity", 0.75).toFloat()
            val haptic = root.optBoolean("hapticFeedback", true)

            // Joystick
            val joyObj = root.optJSONObject("joystick")
            val joystick = if (joyObj != null) {
                JoystickConfig(
                    enabled = joyObj.optBoolean("enabled", true),
                    xRatio = joyObj.optDouble("xRatio", 0.16).toFloat(),
                    yRatio = joyObj.optDouble("yRatio", 0.72).toFloat(),
                    sizeDp = joyObj.optDouble("sizeDp", 140.0).toFloat(),
                    deadZone = joyObj.optDouble("deadZone", 0.15).toFloat(),
                    upKey = parseDirectionalKey(joyObj.optJSONObject("upKey"), DirectionalKey("w", "KeyW", 87)),
                    downKey = parseDirectionalKey(joyObj.optJSONObject("downKey"), DirectionalKey("s", "KeyS", 83)),
                    leftKey = parseDirectionalKey(joyObj.optJSONObject("leftKey"), DirectionalKey("a", "KeyA", 65)),
                    rightKey = parseDirectionalKey(joyObj.optJSONObject("rightKey"), DirectionalKey("d", "KeyD", 68))
                )
            } else {
                JoystickConfig()
            }

            // Trackpad
            val trackObj = root.optJSONObject("trackpad")
            val trackpad = if (trackObj != null) {
                TrackpadConfig(
                    enabled = trackObj.optBoolean("enabled", true),
                    xRatio = trackObj.optDouble("xRatio", 0.76).toFloat(),
                    yRatio = trackObj.optDouble("yRatio", 0.45).toFloat(),
                    widthDp = trackObj.optDouble("widthDp", 170.0).toFloat(),
                    heightDp = trackObj.optDouble("heightDp", 150.0).toFloat(),
                    sensitivity = trackObj.optDouble("sensitivity", 1.4).toFloat()
                )
            } else {
                TrackpadConfig()
            }

            // Buttons
            val buttons = mutableListOf<VirtualButtonConfig>()
            val buttonsArr = root.optJSONArray("buttons")
            if (buttonsArr != null) {
                for (i in 0 until buttonsArr.length()) {
                    val bObj = buttonsArr.getJSONObject(i)
                    val actionTypeName = bObj.optString("actionType", ActionType.KEYBOARD.name)
                    val actionType = runCatching { ActionType.valueOf(actionTypeName) }.getOrDefault(ActionType.KEYBOARD)

                    buttons.add(
                        VirtualButtonConfig(
                            id = bObj.optString("id", "btn_$i"),
                            label = bObj.optString("label", "BTN"),
                            actionType = actionType,
                            key = bObj.optString("key", " "),
                            code = bObj.optString("code", "Space"),
                            keyCode = bObj.optInt("keyCode", 32),
                            mouseButton = bObj.optInt("mouseButton", 0),
                            xRatio = bObj.optDouble("xRatio", 0.5).toFloat(),
                            yRatio = bObj.optDouble("yRatio", 0.5).toFloat(),
                            sizeDp = bObj.optDouble("sizeDp", 60.0).toFloat()
                        )
                    )
                }
            }

            ControlProfile(
                id = id,
                name = name,
                opacity = opacity,
                hapticFeedback = haptic,
                joystick = joystick,
                trackpad = trackpad,
                buttons = buttons
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseDirectionalKey(obj: JSONObject?, defaultKey: DirectionalKey): DirectionalKey {
        if (obj == null) return defaultKey
        return DirectionalKey(
            key = obj.optString("key", defaultKey.key),
            code = obj.optString("code", defaultKey.code),
            keyCode = obj.optInt("keyCode", defaultKey.keyCode)
        )
    }
}

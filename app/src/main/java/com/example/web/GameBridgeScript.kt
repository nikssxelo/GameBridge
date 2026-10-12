package com.example.web

object GameBridgeScript {

    /**
     * Injected once into the page to install the GameBridge input bridge runtime.
     * Evaluated on onPageStarted and onPageFinished.
     */
    val INJECTION_RUNTIME = """
        (function() {
            if (window.__gameBridgeInitialized) return;
            window.__gameBridgeInitialized = true;
            console.log('[GameBridge] Native Gaming Bridge Initialized by Nikssxelo');

            // Set up state tracking
            window.__gbActiveKeys = new Set();
            window.__gbMouseButtons = 0;
            window.__gbLastMousePos = { x: window.innerWidth / 2, y: window.innerHeight / 2 };

            // Helper to get most relevant interactive target
            function getBestTarget() {
                var el = document.activeElement;
                if (el && el !== document.body && el !== document.documentElement) {
                    return el;
                }
                var canvas = document.querySelector('canvas');
                if (canvas) return canvas;
                var iframe = document.querySelector('iframe');
                if (iframe && iframe.contentDocument) {
                    return iframe.contentDocument.body || iframe;
                }
                return document.body || document.documentElement || window;
            }

            // Keyboard dispatch
            window.__gbDispatchKey = function(type, key, code, keyCode) {
                var target = getBestTarget();
                var eventInit = {
                    key: key,
                    code: code,
                    keyCode: keyCode,
                    which: keyCode,
                    bubbles: true,
                    cancelable: true,
                    composed: true,
                    view: window
                };

                var evt;
                try {
                    evt = new KeyboardEvent(type, eventInit);
                } catch (e) {
                    evt = document.createEvent('KeyboardEvent');
                    if (evt.initKeyboardEvent) {
                        evt.initKeyboardEvent(type, true, true, window, key, 0, '', false, '');
                    }
                }

                // Explicitly define legacy properties commonly used by HTML5 games
                try {
                    Object.defineProperty(evt, 'keyCode', { get: function() { return keyCode; } });
                    Object.defineProperty(evt, 'which', { get: function() { return keyCode; } });
                    Object.defineProperty(evt, 'charCode', { get: function() { return type === 'keypress' ? keyCode : 0; } });
                } catch (e) {}

                if (type === 'keydown') {
                    window.__gbActiveKeys.add(code);
                } else if (type === 'keyup') {
                    window.__gbActiveKeys.delete(code);
                }

                // Dispatch to the active target, document, and window for maximum engine compatibility
                if (target && target.dispatchEvent) target.dispatchEvent(evt);
                document.dispatchEvent(evt);
                window.dispatchEvent(evt);
            };

            // Mouse button dispatch (LMB=0, MMB=1, RMB=2)
            window.__gbDispatchMouse = function(type, button, clientX, clientY) {
                var x = (typeof clientX === 'number' && clientX >= 0) ? clientX : window.__gbLastMousePos.x;
                var y = (typeof clientY === 'number' && clientY >= 0) ? clientY : window.__gbLastMousePos.y;
                window.__gbLastMousePos.x = x;
                window.__gbLastMousePos.y = y;

                var buttonBit = (button === 0) ? 1 : (button === 2 ? 2 : 4);
                if (type === 'mousedown') {
                    window.__gbMouseButtons |= buttonBit;
                } else if (type === 'mouseup') {
                    window.__gbMouseButtons &= ~buttonBit;
                }

                var target = document.elementFromPoint(x, y) || getBestTarget();

                var eventInit = {
                    button: button,
                    buttons: window.__gbMouseButtons,
                    clientX: x,
                    clientY: y,
                    screenX: x,
                    screenY: y,
                    bubbles: true,
                    cancelable: true,
                    composed: true,
                    view: window
                };

                var mouseEvt;
                try {
                    mouseEvt = new MouseEvent(type, eventInit);
                } catch (e) {
                    mouseEvt = document.createEvent('MouseEvents');
                    mouseEvt.initMouseEvent(type, true, true, window, 1, x, y, x, y, false, false, false, false, button, null);
                }

                if (target && target.dispatchEvent) target.dispatchEvent(mouseEvt);
                document.dispatchEvent(mouseEvt);
                window.dispatchEvent(mouseEvt);

                // If mousedown, focus element
                if (type === 'mousedown' && target && target.focus) {
                    try { target.focus(); } catch (e) {}
                }

                // Also trigger click event on mouseup
                if (type === 'mouseup' && button === 0) {
                    var clickEvt = new MouseEvent('click', eventInit);
                    if (target && target.dispatchEvent) target.dispatchEvent(clickEvt);
                } else if (type === 'mouseup' && button === 2) {
                    var ctxEvt = new MouseEvent('contextmenu', eventInit);
                    if (target && target.dispatchEvent) target.dispatchEvent(ctxEvt);
                }
            };

            // Mouse movement dispatch (for camera looking and aiming)
            window.__gbMouseMove = function(movementX, movementY) {
                var newX = Math.max(0, Math.min(window.innerWidth, window.__gbLastMousePos.x + movementX));
                var newY = Math.max(0, Math.min(window.innerHeight, window.__gbLastMousePos.y + movementY));
                window.__gbLastMousePos.x = newX;
                window.__gbLastMousePos.y = newY;

                var target = document.elementFromPoint(newX, newY) || getBestTarget();

                var eventInit = {
                    button: -1,
                    buttons: window.__gbMouseButtons,
                    clientX: newX,
                    clientY: newY,
                    screenX: newX,
                    screenY: newY,
                    movementX: movementX,
                    movementY: movementY,
                    bubbles: true,
                    cancelable: true,
                    composed: true,
                    view: window
                };

                var moveEvt;
                try {
                    moveEvt = new MouseEvent('mousemove', eventInit);
                    // Add legacy movement props
                    Object.defineProperty(moveEvt, 'movementX', { get: function() { return movementX; } });
                    Object.defineProperty(moveEvt, 'movementY', { get: function() { return movementY; } });
                    Object.defineProperty(moveEvt, 'mozMovementX', { get: function() { return movementX; } });
                    Object.defineProperty(moveEvt, 'webkitMovementX', { get: function() { return movementX; } });
                } catch (e) {
                    moveEvt = new MouseEvent('mousemove', eventInit);
                }

                if (target && target.dispatchEvent) target.dispatchEvent(moveEvt);
                document.dispatchEvent(moveEvt);
                window.dispatchEvent(moveEvt);
            };

            // Focus lock helper for canvas
            window.__gbFocusGame = function() {
                var canvas = document.querySelector('canvas') || getBestTarget();
                if (canvas && canvas.focus) {
                    try {
                        canvas.setAttribute('tabindex', '0');
                        canvas.focus();
                    } catch (e) {}
                }
            };
        })();
    """.trimIndent()

    fun buildKeyDownJs(key: String, code: String, keyCode: Int): String {
        val escapedKey = escapeJsString(key)
        val escapedCode = escapeJsString(code)
        return "window.__gbDispatchKey && window.__gbDispatchKey('keydown', '$escapedKey', '$escapedCode', $keyCode);"
    }

    fun buildKeyUpJs(key: String, code: String, keyCode: Int): String {
        val escapedKey = escapeJsString(key)
        val escapedCode = escapeJsString(code)
        return "window.__gbDispatchKey && window.__gbDispatchKey('keyup', '$escapedKey', '$escapedCode', $keyCode);"
    }

    fun buildMouseDownJs(button: Int): String {
        return "window.__gbDispatchMouse && window.__gbDispatchMouse('mousedown', $button, -1, -1);"
    }

    fun buildMouseUpJs(button: Int): String {
        return "window.__gbDispatchMouse && window.__gbDispatchMouse('mouseup', $button, -1, -1);"
    }

    fun buildMouseMoveJs(dx: Float, dy: Float): String {
        return "window.__gbMouseMove && window.__gbMouseMove(${dx.toInt()}, ${dy.toInt()});"
    }

    fun buildFocusGameJs(): String {
        return "window.__gbFocusGame && window.__gbFocusGame();"
    }

    private fun escapeJsString(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("'", "\\'")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }
}

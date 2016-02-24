/*
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package javafxports.android;

import com.sun.glass.ui.monocle.AndroidInputDeviceRegistry;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

public class KeyEventProcessor {

    private static KeyEventProcessor instance = new KeyEventProcessor();
    private int deadKey = 0;

    public static KeyEventProcessor getInstance() {
        return instance;
    }

    public void process(KeyEvent event) {

//        System.out.println("KeyEvent: " + event);

        int jfxModifiers = mapAndroidModifierToJfx(event.getMetaState());
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                KeyCode jfxKeyCode = mapAndroidKeyCodeToJfx(event.getKeyCode());
                Platform.runLater(new Runnable() {
                    public void run() {
                        AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.PRESS, jfxKeyCode.impl_getCode(), jfxKeyCode.impl_getChar().toCharArray(), jfxModifiers);
                    }
                });
                break;

            case KeyEvent.ACTION_UP:
                jfxKeyCode = mapAndroidKeyCodeToJfx(event.getKeyCode());
                Platform.runLater(new Runnable() {
                    public void run() {
                        AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.RELEASE, jfxKeyCode.impl_getCode(), jfxKeyCode.impl_getChar().toCharArray(), jfxModifiers);
                        int unicodeChar = event.getUnicodeChar();
                        if ((unicodeChar & KeyCharacterMap.COMBINING_ACCENT) != 0) {
                            deadKey = unicodeChar & KeyCharacterMap.COMBINING_ACCENT_MASK;
//                        System.out.println("KeyEvent: deadkey: " + deadKey);
                            return;
                        }

                        if (deadKey != 0 && unicodeChar != 0) {
                            unicodeChar = KeyCharacterMap.getDeadChar(deadKey, unicodeChar);
                            deadKey = 0;
                        }

                        if (unicodeChar != 0) {
//                        System.out.println("KeyEvent: unicodeChar: " + unicodeChar);
                            AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.TYPED, KeyCode.UNDEFINED.impl_getCode(), Character.toChars(unicodeChar), jfxModifiers);
                        }

                    }
                });
                break;

            case KeyEvent.ACTION_MULTIPLE:
                if (event.getKeyCode() == KeyEvent.KEYCODE_UNKNOWN) {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.TYPED, javafx.scene.input.KeyCode.UNDEFINED.impl_getCode(), event.getCharacters().toCharArray(),
                                    jfxModifiers);
                        }
                    });
                } else {
                    jfxKeyCode = mapAndroidKeyCodeToJfx(event.getKeyCode());
                    Platform.runLater(new Runnable() {
                        public void run() {
                            for (int i = 0; i < event.getRepeatCount(); i++) {
                                AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.PRESS, jfxKeyCode.impl_getCode(), null, jfxModifiers);
                                AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.RELEASE, jfxKeyCode.impl_getCode(), null, jfxModifiers);
                                AndroidInputDeviceRegistry.dispatchKeyEvent(com.sun.glass.events.KeyEvent.TYPED, jfxKeyCode.impl_getCode(), null, jfxModifiers);
                            }
                        }
                    });
                }

                break;
            default:
                System.err.println("DalvikInput.onKeyEvent Unknown Action " + event.getAction());
                break;
        }
    };

    private static KeyCode mapAndroidKeyCodeToJfx(int keycode) {
        switch (keycode) {
            case KeyEvent.KEYCODE_UNKNOWN: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_SOFT_LEFT: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_SOFT_RIGHT: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_HOME: return KeyCode.HOME;
            case KeyEvent.KEYCODE_BACK: return KeyCode.ESCAPE; // special back key mapped to ESC
            // case KeyEvent.KEYCODE_CALL: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_ENDCALL: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_0: return KeyCode.DIGIT0;
            case KeyEvent.KEYCODE_1: return KeyCode.DIGIT1;
            case KeyEvent.KEYCODE_2: return KeyCode.DIGIT2;
            case KeyEvent.KEYCODE_3: return KeyCode.DIGIT3;
            case KeyEvent.KEYCODE_4: return KeyCode.DIGIT4;
            case KeyEvent.KEYCODE_5: return KeyCode.DIGIT5;
            case KeyEvent.KEYCODE_6: return KeyCode.DIGIT6;
            case KeyEvent.KEYCODE_7: return KeyCode.DIGIT7;
            case KeyEvent.KEYCODE_8: return KeyCode.DIGIT8;
            case KeyEvent.KEYCODE_9: return KeyCode.DIGIT9;
            case KeyEvent.KEYCODE_STAR: return KeyCode.STAR;
            case KeyEvent.KEYCODE_POUND: return KeyCode.POUND;
            case KeyEvent.KEYCODE_DPAD_UP: return KeyCode.UP;
            case KeyEvent.KEYCODE_DPAD_DOWN: return KeyCode.DOWN;
            case KeyEvent.KEYCODE_DPAD_LEFT: return KeyCode.LEFT;
            case KeyEvent.KEYCODE_DPAD_RIGHT: return KeyCode.RIGHT;
            // case KeyEvent.KEYCODE_DPAD_CENTER: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_VOLUME_UP: return KeyCode.VOLUME_UP;
            case KeyEvent.KEYCODE_VOLUME_DOWN: return KeyCode.VOLUME_DOWN;
            case KeyEvent.KEYCODE_POWER: return KeyCode.POWER;
            // case KeyEvent.KEYCODE_CAMERA: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_CLEAR: return KeyCode.CLEAR;
            case KeyEvent.KEYCODE_A: return KeyCode.A;
            case KeyEvent.KEYCODE_B: return KeyCode.B;
            case KeyEvent.KEYCODE_C: return KeyCode.C;
            case KeyEvent.KEYCODE_D: return KeyCode.D;
            case KeyEvent.KEYCODE_E: return KeyCode.E;
            case KeyEvent.KEYCODE_F: return KeyCode.F;
            case KeyEvent.KEYCODE_G: return KeyCode.G;
            case KeyEvent.KEYCODE_H: return KeyCode.H;
            case KeyEvent.KEYCODE_I: return KeyCode.I;
            case KeyEvent.KEYCODE_J: return KeyCode.J;
            case KeyEvent.KEYCODE_K: return KeyCode.K;
            case KeyEvent.KEYCODE_L: return KeyCode.L;
            case KeyEvent.KEYCODE_M: return KeyCode.M;
            case KeyEvent.KEYCODE_N: return KeyCode.N;
            case KeyEvent.KEYCODE_O: return KeyCode.O;
            case KeyEvent.KEYCODE_P: return KeyCode.P;
            case KeyEvent.KEYCODE_Q: return KeyCode.Q;
            case KeyEvent.KEYCODE_R: return KeyCode.R;
            case KeyEvent.KEYCODE_S: return KeyCode.S;
            case KeyEvent.KEYCODE_T: return KeyCode.T;
            case KeyEvent.KEYCODE_U: return KeyCode.U;
            case KeyEvent.KEYCODE_V: return KeyCode.V;
            case KeyEvent.KEYCODE_W: return KeyCode.W;
            case KeyEvent.KEYCODE_X: return KeyCode.X;
            case KeyEvent.KEYCODE_Y: return KeyCode.Y;
            case KeyEvent.KEYCODE_Z: return KeyCode.Z;
            case KeyEvent.KEYCODE_COMMA: return KeyCode.COMMA;
            case KeyEvent.KEYCODE_PERIOD: return KeyCode.PERIOD;
            case KeyEvent.KEYCODE_ALT_LEFT: return KeyCode.ALT;
            case KeyEvent.KEYCODE_ALT_RIGHT: return KeyCode.ALT;
            case KeyEvent.KEYCODE_SHIFT_LEFT: return KeyCode.SHIFT;
            case KeyEvent.KEYCODE_SHIFT_RIGHT: return KeyCode.SHIFT;
            case KeyEvent.KEYCODE_TAB: return KeyCode.TAB;
            case KeyEvent.KEYCODE_SPACE: return KeyCode.SPACE;
            // case KeyEvent.KEYCODE_SYM: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_EXPLORER: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_ENVELOPE: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_ENTER: return KeyCode.ENTER;
            case KeyEvent.KEYCODE_DEL: return KeyCode.BACK_SPACE;
            case KeyEvent.KEYCODE_GRAVE: return KeyCode.DEAD_GRAVE;
            case KeyEvent.KEYCODE_MINUS: return KeyCode.MINUS;
            case KeyEvent.KEYCODE_EQUALS: return KeyCode.EQUALS;
            case KeyEvent.KEYCODE_LEFT_BRACKET: return KeyCode.BRACELEFT;
            case KeyEvent.KEYCODE_RIGHT_BRACKET: return KeyCode.BRACERIGHT;
            case KeyEvent.KEYCODE_BACKSLASH: return KeyCode.BACK_SLASH;
            case KeyEvent.KEYCODE_SEMICOLON: return KeyCode.SEMICOLON;
            // case KeyEvent.KEYCODE_APOSTROPHE: return KeyCode.;
            case KeyEvent.KEYCODE_SLASH: return KeyCode.SLASH;
            case KeyEvent.KEYCODE_AT: return KeyCode.AT;
            // case KeyEvent.KEYCODE_NUM: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_HEADSETHOOK: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_FOCUS: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_PLUS: return KeyCode.PLUS;
            case KeyEvent.KEYCODE_MENU: return KeyCode.CONTEXT_MENU;
            // case KeyEvent.KEYCODE_NOTIFICATION: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_SEARCH: return KeyCode.FIND;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: return KeyCode.PLAY;
            case KeyEvent.KEYCODE_MEDIA_STOP: return KeyCode.STOP;
            case KeyEvent.KEYCODE_MEDIA_NEXT: return KeyCode.TRACK_NEXT;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS: return KeyCode.TRACK_PREV;
            case KeyEvent.KEYCODE_MEDIA_REWIND: return KeyCode.REWIND;
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: return KeyCode.FAST_FWD;
            case KeyEvent.KEYCODE_MUTE: return KeyCode.MUTE;
            case KeyEvent.KEYCODE_PAGE_UP: return KeyCode.PAGE_UP;
            case KeyEvent.KEYCODE_PAGE_DOWN: return KeyCode.PAGE_DOWN;
            // case KeyEvent.KEYCODE_PICTSYMBOLS: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_SWITCH_CHARSET: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_BUTTON_A: return KeyCode.GAME_A;
            case KeyEvent.KEYCODE_BUTTON_B: return KeyCode.GAME_B;
            case KeyEvent.KEYCODE_BUTTON_C: return KeyCode.GAME_C;
            case KeyEvent.KEYCODE_BUTTON_X: return KeyCode.GAME_D;
            // case KeyEvent.KEYCODE_BUTTON_Y: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_Z: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_L1: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_R1: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_L2: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_R2: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_THUMBL: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_THUMBR: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_START: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_SELECT: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_BUTTON_MODE: return KeyCode.MODECHANGE;
            case KeyEvent.KEYCODE_ESCAPE: return KeyCode.ESCAPE;
            case KeyEvent.KEYCODE_CTRL_LEFT: return KeyCode.CONTROL;
            case KeyEvent.KEYCODE_CTRL_RIGHT: return KeyCode.CONTROL;
            case KeyEvent.KEYCODE_CAPS_LOCK: return KeyCode.CAPS;
            case KeyEvent.KEYCODE_SCROLL_LOCK: return KeyCode.SCROLL_LOCK;
            case KeyEvent.KEYCODE_META_LEFT: return KeyCode.META;
            case KeyEvent.KEYCODE_META_RIGHT: return KeyCode.META;
            // case KeyEvent.KEYCODE_FUNCTION: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_SYSRQ: return KeyCode.PRINTSCREEN;
            case KeyEvent.KEYCODE_BREAK: return KeyCode.PAUSE;
            case KeyEvent.KEYCODE_MOVE_HOME: return KeyCode.BEGIN;
            case KeyEvent.KEYCODE_MOVE_END: return KeyCode.END;
            case KeyEvent.KEYCODE_INSERT: return KeyCode.INSERT;
            // case KeyEvent.KEYCODE_FORWARD: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_MEDIA_PLAY: return KeyCode.PLAY;
            // case KeyEvent.KEYCODE_MEDIA_PAUSE: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_MEDIA_CLOSE: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_MEDIA_EJECT: return KeyCode.EJECT_TOGGLE;
            case KeyEvent.KEYCODE_MEDIA_RECORD: return KeyCode.RECORD;
            case KeyEvent.KEYCODE_F1: return KeyCode.F1;
            case KeyEvent.KEYCODE_F2: return KeyCode.F2;
            case KeyEvent.KEYCODE_F3: return KeyCode.F3;
            case KeyEvent.KEYCODE_F4: return KeyCode.F4;
            case KeyEvent.KEYCODE_F5: return KeyCode.F5;
            case KeyEvent.KEYCODE_F6: return KeyCode.F6;
            case KeyEvent.KEYCODE_F7: return KeyCode.F7;
            case KeyEvent.KEYCODE_F8: return KeyCode.F8;
            case KeyEvent.KEYCODE_F9: return KeyCode.F9;
            case KeyEvent.KEYCODE_F10: return KeyCode.F10;
            case KeyEvent.KEYCODE_F11: return KeyCode.F11;
            case KeyEvent.KEYCODE_F12: return KeyCode.F12;
            case KeyEvent.KEYCODE_NUM_LOCK: return KeyCode.NUM_LOCK;
            case KeyEvent.KEYCODE_NUMPAD_0: return KeyCode.NUMPAD0;
            case KeyEvent.KEYCODE_NUMPAD_1: return KeyCode.NUMPAD1;
            case KeyEvent.KEYCODE_NUMPAD_2: return KeyCode.NUMPAD2;
            case KeyEvent.KEYCODE_NUMPAD_3: return KeyCode.NUMPAD3;
            case KeyEvent.KEYCODE_NUMPAD_4: return KeyCode.NUMPAD4;
            case KeyEvent.KEYCODE_NUMPAD_5: return KeyCode.NUMPAD5;
            case KeyEvent.KEYCODE_NUMPAD_6: return KeyCode.NUMPAD6;
            case KeyEvent.KEYCODE_NUMPAD_7: return KeyCode.NUMPAD7;
            case KeyEvent.KEYCODE_NUMPAD_8: return KeyCode.NUMPAD8;
            case KeyEvent.KEYCODE_NUMPAD_9: return KeyCode.NUMPAD9;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE: return KeyCode.DIVIDE;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: return KeyCode.MULTIPLY;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT: return KeyCode.SUBTRACT;
            case KeyEvent.KEYCODE_NUMPAD_ADD: return KeyCode.ADD;
            case KeyEvent.KEYCODE_NUMPAD_DOT: return KeyCode.PERIOD;
            case KeyEvent.KEYCODE_NUMPAD_COMMA: return KeyCode.COMMA;
            case KeyEvent.KEYCODE_NUMPAD_ENTER: return KeyCode.ENTER;
            case KeyEvent.KEYCODE_NUMPAD_EQUALS: return KeyCode.EQUALS;
            case KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN: return KeyCode.LEFT_PARENTHESIS;
            case KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN: return KeyCode.RIGHT_PARENTHESIS;
            case KeyEvent.KEYCODE_VOLUME_MUTE: return KeyCode.MUTE;
            case KeyEvent.KEYCODE_INFO: return KeyCode.INFO;
            case KeyEvent.KEYCODE_CHANNEL_UP: return KeyCode.CHANNEL_UP;
            case KeyEvent.KEYCODE_CHANNEL_DOWN: return KeyCode.CHANNEL_DOWN;
            // case KeyEvent.KEYCODE_ZOOM_IN: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_ZOOM_OUT: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_TV: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_WINDOW: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_GUIDE: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_DVR: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BOOKMARK: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_CAPTIONS: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_SETTINGS: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_TV_POWER: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_TV_INPUT: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_STB_POWER: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_STB_INPUT: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_AVR_POWER: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_AVR_INPUT: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_PROG_RED: return KeyCode.COLORED_KEY_0;
            case KeyEvent.KEYCODE_PROG_GREEN: return KeyCode.COLORED_KEY_1;
            case KeyEvent.KEYCODE_PROG_YELLOW: return KeyCode.COLORED_KEY_2;
            case KeyEvent.KEYCODE_PROG_BLUE: return KeyCode.COLORED_KEY_3;
            // case KeyEvent.KEYCODE_APP_SWITCH: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_1: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_2: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_3: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_4: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_5: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_6: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_7: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_8: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_9: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_10: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_11: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_12: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_13: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_14: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_15: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BUTTON_16: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_LANGUAGE_SWITCH: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_MANNER_MODE: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_3D_MODE: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_CONTACTS: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_CALENDAR: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_MUSIC: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_CALCULATOR: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_ZENKAKU_HANKAKU: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_EISU: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_MUHENKAN: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_HENKAN: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_KATAKANA_HIRAGANA: return KeyCode.JAPANESE_HIRAGANA;
            // case KeyEvent.KEYCODE_YEN: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_RO: return KeyCode.UNDEFINED;
            case KeyEvent.KEYCODE_KANA: return KeyCode.KANA;
            // case KeyEvent.KEYCODE_ASSIST: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BRIGHTNESS_DOWN: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_BRIGHTNESS_UP: return KeyCode.UNDEFINED;
            // case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK: return
            // KeyCode.UNDEFINED;
            default:
                return KeyCode.UNDEFINED;
        }
    }

    private static int mapAndroidModifierToJfx(int androidMetaStates) {
        int jfxModifiers = 0;

        if ((androidMetaStates & KeyEvent.META_SHIFT_MASK) != 0) {
            jfxModifiers += com.sun.glass.events.KeyEvent.MODIFIER_SHIFT;
        }

        if ((androidMetaStates & KeyEvent.META_CTRL_MASK) != 0) {
            jfxModifiers += com.sun.glass.events.KeyEvent.MODIFIER_CONTROL;
        }

        if ((androidMetaStates & KeyEvent.META_ALT_MASK) != 0) {
            jfxModifiers += com.sun.glass.events.KeyEvent.MODIFIER_ALT;
        }

        if ((androidMetaStates & KeyEvent.META_META_ON) != 0) {
            jfxModifiers += com.sun.glass.events.KeyEvent.MODIFIER_WINDOWS;
        }
        return jfxModifiers;
    }

}
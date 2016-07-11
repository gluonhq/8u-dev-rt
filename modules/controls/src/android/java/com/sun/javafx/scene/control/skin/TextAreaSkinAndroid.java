/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.javafx.scene.control.skin;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.Scene;
import com.sun.glass.ui.android.DalvikInput;


public class TextAreaSkinAndroid extends TextAreaSkin {

    private final TextArea textArea;
    boolean isShowingSoftwareKeyboard = false;

    public TextAreaSkinAndroid(final TextArea textArea) {
        super(textArea);
        this.textArea = textArea;

        if (textArea.isFocused() && textArea.isEditable() && (textArea.getScene() != null)) {
            showSoftwareKeyboard();
        }

        textArea.sceneProperty().addListener( (o, a, b) -> evaluateVisibility());
        textArea.focusedProperty().addListener( (o, a, b) -> evaluateVisibility());
    }

    private void showSoftwareKeyboard() {
        if (! textArea.isEditable()) {
            return;
        }
        // even if we think we are showing the keyboard, we need to evaluate this method as the 
        // keyboard might be hidden by the user (height will be 0)
        if (!isShowingSoftwareKeyboard) {
            com.sun.glass.ui.android.SoftwareKeyboard.show();
            isShowingSoftwareKeyboard = true;
            try {
                com.sun.glass.ui.android.DalvikInput.setActiveNode(textArea);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        double kh = com.sun.glass.ui.android.DalvikInput.keyboardSize;
        adjustSize(kh);
        DalvikInput.setKeyboardHeightListener (e -> adjustSize(e));
    }


    private void evaluateVisibility() {
        Scene scene = textArea.getScene();
        boolean focused = textArea.isFocused();
        if ((scene != null) && focused) {
            showSoftwareKeyboard();
        } else {
            hideSoftwareKeyboard();
        }
    }

/**
 * in case a virtual keyboard is shown that would overlap the TextArea input,
 * we shift the scene so that the TextArea is rendered immediately above the
 * virtual keyboard.
 * In case shifting would cause the beginning of the TextArea to appear outside
 * the screen (which would happen in case the height of the TextArea plus the
 * height of the virtual keyboard is larger than the screensize), we cap the
 * shift to the translation of the TextArea (so that one appears at the top
 * of the screen.
 */
    private void adjustSize(double kh) {
        double tTot = textArea.getScene().getHeight();
        double trans = textArea.getLocalToSceneTransform().getTy();
        double th = textArea.getHeight();
        double ty = trans + th;
        if (ty > (tTot - kh) ) { // vk would overlap
            if (tTot < kh + th) { // not enough space for both
                textArea.getScene().getRoot().setTranslateY(-trans );
            } else {
                textArea.getScene().getRoot().setTranslateY(tTot - ty - kh);
            }
        } else if (kh < 1) { // vk disappeared.
            textArea.getScene().getRoot().setTranslateY(0);
        }
    }

    private void hideSoftwareKeyboard() {
        if (! textArea.isEditable()) {
            return;
        }
        if (! isShowingSoftwareKeyboard) {
            return;
        }
        com.sun.glass.ui.android.SoftwareKeyboard.hide();
        isShowingSoftwareKeyboard = false;
        if(textArea.getScene() != null) {
            textArea.getScene().getRoot().setTranslateY(0);
        }
    }

}

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
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.glass.ui.android.DalvikInput;

public class TextFieldSkinAndroid extends TextFieldSkin {

    public static final char MOBILEBULLET = '\u2022';
    private final TextField textField;
    boolean isShowingSoftwareKeyboard = false;


    public TextFieldSkinAndroid(final TextField textField) {
        super(textField);
        this.textField = textField;

        if (textField.isFocused() && textField.getScene() != null) {
            showSoftwareKeyboard();
        }

        textField.sceneProperty().addListener( (o, a, b) -> evaluateVisibility());

        textField.focusedProperty().addListener( (o, a, b) -> evaluateVisibility());
    }


    public TextFieldSkinAndroid(final TextField textField, final TextFieldBehavior behavior) {
        super(textField, behavior);
        this.textField = textField;
    }

    private void evaluateVisibility() {
        Scene scene = textField.getScene();
        boolean focused = textField.isFocused();
        if ((scene != null) && focused) {
            showSoftwareKeyboard();
        } else {
            hideSoftwareKeyboard();
        }
    }

    @Override 
    protected String maskText(String txt) {
        if (getSkinnable() instanceof PasswordField) {
            int n = txt.length();
            StringBuilder passwordBuilder = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                passwordBuilder.append(MOBILEBULLET);
            }

            return passwordBuilder.toString();
        } else {
            return txt;
        }
    }


    private void showSoftwareKeyboard() {
        if (! textField.isEditable()) {
            return;
        }
        // even if we think we are showing the keyboard, we need to evaluate this method as the 
        // keyboard might be hidden by the user (height will be 0)
        if (!isShowingSoftwareKeyboard) {
            com.sun.glass.ui.android.SoftwareKeyboard.show();
            isShowingSoftwareKeyboard = true;
            try {
                com.sun.glass.ui.android.DalvikInput.setActiveNode(textField);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        double kh = com.sun.glass.ui.android.DalvikInput.keyboardSize;
        adjustSize(kh);
        DalvikInput.setKeyboardHeightListener (e -> adjustSize(e));
    }

    private void adjustSize(double kh) {
        double tTot = textField.getScene().getHeight();
        double ty = textField.getLocalToSceneTransform().getTy()+ textField.getHeight();
        if (ty > (tTot - kh) ) {
            textField.getScene().getRoot().setTranslateY(tTot - ty - kh);
        } else if (kh < 1) {
            textField.getScene().getRoot().setTranslateY(0);
        }
    }


    private void hideSoftwareKeyboard() {
        if (! textField.isEditable()) {
            return;
        }
        if (! isShowingSoftwareKeyboard) {
            return;
        }
        com.sun.glass.ui.android.SoftwareKeyboard.hide();
        isShowingSoftwareKeyboard = false;
        if(textField.getScene() != null) {
            textField.getScene().getRoot().setTranslateY(0);
        }
    }


}

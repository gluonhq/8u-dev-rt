package javafxports.android;

import android.text.Editable;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.*;

import java.lang.Override;

public class FXInputConnection extends BaseInputConnection {

    private Editable editable;
    private FXDalvikEntity.InternalSurfaceView surfaceView;
    private String workText = "";

    public FXInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);

        this.surfaceView = (FXDalvikEntity.InternalSurfaceView) targetView;
    }

    @Override
    public Editable getEditable() {
        if (editable == null) {
            editable = Editable.Factory.getInstance().newEditable("");
            Selection.setSelection(editable, 0);
        }
        return editable;
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
System.out.println("[JVDBG] TEXT setComposingText-Before: text = '" + text + "', workText = '" + workText + "', editable = '" + editable + "'");
        if (!text.toString().equals(workText)) {
            for (int i = 0; i < workText.length(); i++) {
                surfaceView.backSpace();
            }

            workText = text.toString();
            surfaceView.setText(text, newCursorPosition);
        }

System.out.println("[JVDBG] TEXT setComposingText-BeforeSuper: text = '" + text + "', workText = '" + workText + "', editable = '" + editable + "'");
        boolean result = super.setComposingText(text, newCursorPosition);
System.out.println("[JVDBG] TEXT setComposingText-After: text = '" + text + "', workText = '" + workText + "', editable = '" + editable + "'");
        return result;
    }

    @Override
    public boolean finishComposingText() {
System.out.println ("[JVDBG] TEXT finishComposingText-Before: workText = '" + workText + "', editable = '" + editable + "'");
        workText = "";
//        editable.clear();
System.out.println ("[JVDBG] TEXT finishComposingText-BeforeSuper: workText = '" + workText + "', editable = '" + editable + "'");
        boolean result = super.finishComposingText();
System.out.println ("[JVDBG] TEXT finishComposingText-After: workText = '" + workText + "', editable = '" + editable + "'");
        return result;
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
System.out.println ("[JVDBG] TEXT commitText-Before: '" + text + "', workText: '" + workText + "', editable = '" + editable + "'");
//        editable.clear();
        if (workText.length() > 0) {
//            editable.replace(editable.length() - workText.length(), editable.length(), text);
            for (int i = 0; i < workText.length(); i++) {
                surfaceView.backSpace();
            }
        }
        editable.append(text);
        surfaceView.setText(text, newCursorPosition);
        editable.clear();
        workText = "";
System.out.println ("[JVDBG] TEXT commitText-BeforeSuper: '" + text + "', workText: '" + workText + "', editable = '" + editable + "'");
        boolean result = super.commitText(text, newCursorPosition);
System.out.println ("[JVDBG] TEXT commitText-After: '" + text + "', workText: '" + workText + "', editable = '" + editable + "'");
        return result;
    }

    @Override public boolean commitCompletion(CompletionInfo info) {
System.out.println ("[JVDBG] TEXT commitCompletion: info = " + info);
       return super.commitCompletion(info);
    }

    @Override public boolean commitCorrection(CorrectionInfo info) {
System.out.println ("[JVDBG] TEXT commitCorrection: info = " + info);
       return super.commitCorrection(info);
    }

    @Override public boolean sendKeyEvent(KeyEvent event) {
System.out.println ("[JVDBG] TEXT sendKeyEvent: event = " + event);
        return super.sendKeyEvent(event);
    }

    @Override public boolean deleteSurroundingText(int beforeLength, int afterLength) {
System.out.println ("[JVDBG] TEXT deleteSurroundingText: beforeLength = " + beforeLength + ", afterLength = " + afterLength);
        // check for backspace being pressed
        if (beforeLength == 1 && afterLength == 0) {
            editable.clear();
            surfaceView.backSpace();
        }
        return super.deleteSurroundingText(beforeLength, afterLength);
    }

    private int commonSize (CharSequence a, CharSequence b) {
        int la = (a == null) ? 0 : a.length();
        int lb = (b == null) ? 0 : b.length();
        int l = Math.min(la, lb);
        int idx = 0;
        while ( (idx < l) && (a.charAt(idx) == b.charAt(idx))) idx++;
        return idx;
    }

}


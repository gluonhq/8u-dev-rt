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
System.out.println ("[JVDBG] getEditable asked, return "+editable);
// Thread.dumpStack();
        return editable;
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
System.out.println ("[JVDBG] TEXT setComposingtext to "+text+", ncp = "+newCursorPosition);
System.out.println ("[JVDBG] aftercursor = "+getTextAfterCursor(5, 0)+" and before = "+getTextBeforeCursor(5,0));
        int ol = workText.length();
        int nl = text.length();
        if (nl > ol ) {
            // new text is longer than work, send all new bytes
            int cs = commonSize(workText, text);
            surfaceView.setText(text.subSequence(cs, nl), newCursorPosition);
        }
        else {
            for (int i = nl; i < ol;i++) surfaceView.backSpace();
            // surfaceView.setText(text, newCursorPosition);
        }
        workText = text.toString();
        return super.setComposingText(text, newCursorPosition);
    }

    @Override
    public boolean finishComposingText() {
System.out.println ("[JVDBG] TEXT done FinishComposingText, workString = "+workText+" with length = "+workText.length());
        if (workText.length() == 0) surfaceView.backSpace();
System.out.println ("[JVDBG] selectedtext = "+getSelectedText(0)+", aftercursor = "+getTextAfterCursor(5, 0)+" and before = "+getTextBeforeCursor(5,0));
        return super.finishComposingText();
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
System.out.println ("[JVDBG] TEXT CommitText: "+text+", ncp = "+newCursorPosition+", textsize = "+text.length());
        if (text.length() > 1) {
            for (int i = 1; i < text.length(); i++) surfaceView.backSpace();
        }
        editable.append(text);
        surfaceView.setText(text, newCursorPosition);
        editable.clear();
        workText = "";
        return true;
    }

    @Override public boolean commitCompletion(CompletionInfo text) {
System.out.println ("[JVDBG] TEXT commit completion: "+text);
       return super.commitCompletion(text);
    }

    @Override public boolean commitCorrection(CorrectionInfo text) {
System.out.println ("[JVDBG] TEXT CorrectionInfo: "+text);
       return super.commitCorrection(text);
    }

    @Override public boolean sendKeyEvent (KeyEvent event) {
System.out.println ("[JVDBG] TEXT sendKeyEvent "+event);
        return super.sendKeyEvent (event);
    }

    @Override public CharSequence getSelectedText (int flags) {
System.out.println ("[JVDBG] getSelectedText");
        return super.getSelectedText(flags);
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


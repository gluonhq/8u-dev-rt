package javafxports.android;

import android.content.Intent;

public interface IntentHandler {

    void gotActivityResult (int requestCode, int resultCode, Intent intent);

}

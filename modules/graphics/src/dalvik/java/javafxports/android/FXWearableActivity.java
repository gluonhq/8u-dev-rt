/*
 * Copyright (c) 2017, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javafxports.android;

import android.support.wearable.activity.WearableActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import javafx.application.Platform;

public class FXWearableActivity extends WearableActivity {

    private static final String TAG = "FXWearableActivity";
    private static final String JFX_BUILD = "8.60.9-SNAPSHOT";
    
    private static final String ACTIVITY_LIB = "activity";
    private static final String META_DATA_DEBUG_PORT = "debug.port";

    public static String dexClassPath = new String();

    private static FXWearableActivity instance;
    private static Launcher launcher;
    private static FrameLayout mViewGroup;

    private static String appDataDir;

    private static IntentHandler intentHandler;

    private static AmbientHandler ambientHandler;

    private static final Bundle metadata = new Bundle();
    private FXDalvikEntity fxDalvikEntity;

    static {
        Log.v(TAG, "Initializing JavaFX Platform, using "+JFX_BUILD);
        System.loadLibrary(ACTIVITY_LIB);
    }
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ApplicationInfo appi = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            if (appi != null && appi.metaData != null) {
                metadata.putAll(appi.metaData);
            }

        } catch (NameNotFoundException e) {
            Log.w(TAG, "Error getting Application info.");
        }

        try {            
            ActivityInfo ai = FXWearableActivity.this.getPackageManager().getActivityInfo(
                    getIntent().getComponent(), PackageManager.GET_META_DATA);
            if (ai != null && ai.metaData != null) {
                metadata.putAll(ai.metaData);           
            }

        } catch (NameNotFoundException e) {
            Log.w(TAG, "Error getting Activity info.");
        }
        this.fxDalvikEntity = new FXDalvikEntity(metadata, this);
        Log.v(TAG, "onCreate called, using "+JFX_BUILD);
        if (launcher != null) {
            Log.v(TAG, "JavaFX application is already running");
            return;
        }
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
        getWindow().setFormat(PixelFormat.RGBA_8888);


        View myView = fxDalvikEntity.createView();
        
        mViewGroup = new FrameLayout(this);
        mViewGroup.addView(myView);
        setContentView(mViewGroup);
        instance = this;

        appDataDir = getApplicationInfo().dataDir;
        instance = this;
        _setDataDir(appDataDir);

        int dport = metadata.getInt(META_DATA_DEBUG_PORT);
        if (dport > 0) {
            android.os.Debug.waitForDebugger();
        }

    }
    
    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.v(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.v(TAG, "onActivityResult with requestCode " + requestCode+" and resultCode = "+resultCode+" and intent = "+intent);
        if (intentHandler != null) {
            intentHandler.gotActivityResult (requestCode, resultCode, intent);
        }
    }

    public void setOnActivityResultHandler (IntentHandler handler) {
        intentHandler = handler;
    }

    public static FXWearableActivity getInstance() {
        return instance;
    }

    public static ViewGroup getViewGroup() {
        return mViewGroup;
    }

    public static String getMyDataDir() {
        return appDataDir;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "Called onConfigurationChanged");
    }

    private native void _setDataDir(String dir);

    /**
     * The AmbientHandler, if set, will enable AmbientMode, otherwise it will be disabled.
     * Once enabled, it can't be disabled
     * When enabled, it will call its methods on the JavaFX thread when enter/update/exit Ambient are
     * called
     * @param handler 
     */
    public void setAmbientHandler(AmbientHandler handler) {
        ambientHandler = handler;
        if (ambientHandler != null) {
            Log.v(TAG, "WearableActivity: enable Ambient");
            // Sets that this activity should remain displayed when the system enters ambient mode.
            setAmbientEnabled();
        }
    }
    
    
    /**
     * Called when an activity is entering ambient mode.
     * @param ambientDetails 
     */
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails); 
        Log.v(TAG, "onEnterAmbient with ambientDetails ");
        if (ambientHandler != null) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    ambientHandler.enterAmbient(ambientDetails);
                }

            });
        }
    }
    
    /**
     * Called when the system is updating the display for ambient mode.
     */
    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient(); 
        Log.v(TAG, "onUpdateAmbient");
        if (ambientHandler != null) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    ambientHandler.updateAmbient();
                }

            });
        }
    }
    
    /**
     * Called when an activity should exit ambient mode.
     */
    @Override
    public void onExitAmbient() {
        if (ambientHandler != null) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    ambientHandler.exitAmbient();
                }

            });
        }
        Log.v(TAG, "onExitAmbient");
        super.onExitAmbient(); 
    }
    
    /**
     * True if activity is in ambient mode, false if it is in interaction mode
     * @return 
     */
    public final boolean isAmbientMode() {
        return isAmbient(); 
    }
}

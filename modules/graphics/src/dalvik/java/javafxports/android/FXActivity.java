/*
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates. All rights reserved.
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

import android.app.Activity;
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

public class FXActivity extends Activity {

    private static final String TAG = "FXActivity";
    private static final String JFX_BUILD = "8.60.9-SNAPSHOT";
    
    private static final String ACTIVITY_LIB = "activity";
    private static final String META_DATA_DEBUG_PORT = "debug.port";

    public static String dexClassPath = new String();

    private static FXActivity instance;
    private static Launcher launcher;
    private static FrameLayout mViewGroup;

    private static String appDataDir;

    private static IntentHandler intentHandler;

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
            ActivityInfo ai = FXActivity.this.getPackageManager().getActivityInfo(
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

    public static FXActivity getInstance() {
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
}

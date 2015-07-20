package com.afollestad.bridgesample;

import android.app.Application;

import com.afollestad.bridge.Bridge;

/**
 * @author Aidan Follestad (afollestad)
 */
public class App extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        Bridge.cleanup();
    }
}

package com.anupcowkur.reservoirsample;

import android.app.Application;

import com.anupcowkur.reservoir.Reservoir;

/**
 * The main application.
 */
public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Reservoir.init(this, 2048);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

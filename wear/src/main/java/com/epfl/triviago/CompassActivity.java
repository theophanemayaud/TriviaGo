package com.epfl.triviago;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ImageView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CompassActivity extends WearableActivity {

    private ImageView mArrow;
    public static final String ANGLE_BROADCAST = "ANGLE_BROADCAST";
    public static final String STOP_ACTIVITY_BROADCAST = "STOP_ACTIVITY_BROADCAST";
    public static final String ANGLE_VALUE = "ANGLE_VALUE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_view);

        // get components for view
        mArrow = findViewById(R.id.compas_arrow);
        mArrow.setRotation(0.f);
        // register local broadcast manager
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Float angle = intent.getFloatExtra(ANGLE_VALUE, 0);
                mArrow.setRotation(angle);
            }
        }, new IntentFilter(ANGLE_BROADCAST));
        // register local broadcast manager
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        }, new IntentFilter(STOP_ACTIVITY_BROADCAST));

        // Enables Always-on
        setAmbientEnabled();
    }
}
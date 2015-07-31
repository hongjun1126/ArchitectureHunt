package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Set;

public class MainActivity extends Activity implements SensorEventListener {

    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];

    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private float start = 0f;
    private Bitmap pic;
    private float[] loc;
    private ImageView bg_pic;
    private TextView lat;
    private TextView lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);
        Intent intent = getIntent();
        pic = intent.getParcelableExtra("pic");
        loc = intent.getFloatArrayExtra("loc");
        if (pic != null && loc != null) {
            bg_pic = (ImageView) findViewById(R.id.pic);
            bg_pic.setImageBitmap(pic);
            lat = (TextView) findViewById(R.id.lat);
            lng = (TextView) findViewById(R.id.lng);
            lat.setText(Float.toString(loc[0]));
            lng.setText(Float.toString(loc[1]));
        }
        Log.d("ADebugTag", "IN EXPLORATION MODE");


    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            mLastAccelerometer = event.values.clone();
        }
        if (event.sensor == mMagnetometer) {
            mLastMagnetometer = event.values.clone();
        }
        if (mLastAccelerometer != null && mLastMagnetometer!= null) {
            float R[] = new float[9];
            float I[] = new float[9];
            if (SensorManager.getRotationMatrix(R, I, mLastAccelerometer, mLastMagnetometer)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimuthInDegrees = (float)(Math.toDegrees(orientation[0]));
                Log.d("SensorChanged", "Curr orientation in degrees: " + Float.toString(azimuthInDegrees));
                RotateAnimation ra = new RotateAnimation(
                        -azimuthInDegrees,
                        -azimuthInDegrees,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);
                ra.setDuration(0);
                ra.setFillAfter(true);
                mPointer.startAnimation(ra);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}

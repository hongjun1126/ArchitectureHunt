package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

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

    private float dest_dist = 0;
    private float dest_rot = 0;


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
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("new_dist_rot"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (pic != null && loc!=null) {
                Log.d("ADebugTag", "received the right broadcast from messenger");
                Bundle data = intent.getBundleExtra("data");
                dest_dist = data.getFloat("distance");
                Log.d("DestDISTANCE", Float.toString(dest_dist));

                dest_rot = data.getFloat("rot");
                Log.d("DestROT", Float.toString(dest_rot));
                TextView dist = (TextView) findViewById(R.id.dist);
                dist.setText(Integer.toString(Math.round(dest_dist)) + "m");
                if (dest_rot < 20) {
                    atDestination();
                }
            }
        }
    };

    protected void atDestination(){
    //DO SOMETHING
        finish();
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("new_dist_loc"));
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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
//                Log.d("SensorChanged", "Curr orientation in degrees: " + Float.toString(azimuthInDegrees));
                float rot = 0;
                if (dest_rot != 0) {
                    rot = dest_rot;
                }
                RotateAnimation ra = new RotateAnimation(
                        rot - azimuthInDegrees,
                        rot - azimuthInDegrees,
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

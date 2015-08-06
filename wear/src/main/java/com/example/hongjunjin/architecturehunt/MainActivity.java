package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.app.PendingIntent;
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

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    private String photoId;

    protected static NotificationManagerCompat notificationManager;

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
        photoId = intent.getStringExtra("photoId");

        Log.d("ADebugTag", "onCreate photoId: " + photoId);



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


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        notification();


    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("finish", false)) {
                Log.d("ADebugTag", "received the finish broadcast from messenger");
                finish();
            } else if (pic != null && loc!=null) {
                Log.d("ADebugTag", "received the right broadcast from messenger");
                Bundle data = intent.getBundleExtra("data");
                dest_dist = data.getFloat("distance");
                Log.d("DestDISTANCE", Float.toString(dest_dist));

                dest_rot = data.getFloat("rot");
                Log.d("DestROT", Float.toString(dest_rot));
                TextView dist = (TextView) findViewById(R.id.dist);
                dist.setText(Integer.toString(Math.round(dest_dist)) + "m");
                if (dest_dist < 30) {
                    atDestination();
                }
            }
        }
    };

    protected void atDestination(){
    //DO SOMETHING
        notification();
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

    public void notification(){

        // create Intents for taking pictures

        Intent cameraIntent = new Intent(this, sendMessageCamera.class);

        cameraIntent.putExtra("key", "camera");
        PendingIntent cameraPendingIntent = PendingIntent.getService(this, 0, cameraIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent favoriteIntent = new Intent(this, sendMessage.class);

        Log.d("ADebugTag", "notification photoId: " + photoId);
        favoriteIntent.putExtra("key", photoId);


        PendingIntent favoritePendingIntent = PendingIntent.getService(this, 0, favoriteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("ADebugTag", "notification photoId in intent: " + favoriteIntent.getStringExtra("key"));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("ArchitectureHunt")
                .setContentText("You've arrived.")
                .setContentIntent(cameraPendingIntent)
                .setContentIntent(favoritePendingIntent)
                .addAction(R.drawable.flickrccc, "Favorite", favoritePendingIntent)
                .addAction(R.drawable.camera, "Take a picture", cameraPendingIntent);



        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(7, notificationBuilder.build());

    }
}
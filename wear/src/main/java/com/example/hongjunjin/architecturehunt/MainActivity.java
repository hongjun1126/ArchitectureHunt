package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends Activity implements SensorEventListener {

    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private Bitmap pic;
    private float[] loc;
    private ImageView bg_pic;
    private String photoId;

    protected static NotificationManagerCompat notificationManager;

    private float dest_dist = 0;
    private float dest_rot = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        Log.d("ADebugTag", "onCreate photoId: " + "in mainActivity");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);

        pic = messengerService.bm;
        loc = messengerService.float_loc;
        photoId = messengerService.pictureId;

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        doWork();
                        Thread.sleep(1000); // Pause of 1 Second
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }catch(Exception e){
                    }
                }
            }
        }).start();


        if (pic != null && loc != null) {
            bg_pic = (ImageView) findViewById(R.id.pic);
            bg_pic.setImageBitmap(pic);
            //lat = (TextView) findViewById(R.id.lat);
            //lng = (TextView) findViewById(R.id.lng);
            //lat.setText(Float.toString(loc[0]));
            //lng.setText(Float.toString(loc[1]));
        }
        Log.d("Wear_MAINACTIVITY", "IN EXPLORATION MODE");
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("new_dist_rot"));




        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notification();


    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("finish", false)) {
                Log.d("MAINACTIVITY", "received the finish broadcast from LOCALSERVICE");
                finish();
            } else if (pic != null && loc!=null) {
                Log.d("MainActivity", "received the right broadcast from LOCALSERVICE");
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

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{

                    TextView txtCurrentTime= (TextView)findViewById(R.id.timeText);

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("K:mm aa");
                    sdf.setTimeZone(TimeZone.getTimeZone("America/Vancouver"));

                    txtCurrentTime.setText(sdf.format(cal.getTime()));

                }catch (Exception e) {}
            }
        });
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
                .setSmallIcon(R.drawable.build_logo)
                .setContentTitle("The Building Scavenger")
                .setContentText("You've arrived.")
                .setLargeIcon(pic)
                .setContentIntent(cameraPendingIntent)
                .setContentIntent(favoritePendingIntent)
                .addAction(R.drawable.flickrccc, "Favorite", favoritePendingIntent)
                .addAction(R.drawable.camera, "Take a picture", cameraPendingIntent);



        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(7, notificationBuilder.build());

    }
}
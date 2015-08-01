package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class sendMessage extends Service {

    private GoogleApiClient mGoogleApiClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        final String RECEIVER_SERVICE_PATH = extras.getString("key");


        if (RECEIVER_SERVICE_PATH.equals("camera")){
            receiveMessage.notificationManager.cancel(7);
        }

        Log.d("ADebugTag", "test: " + "in sendMessage");

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        // Do something
                        Log.d("ADebugTag", "test: " + "connected");
                        Log.d("ADebugTag", "test: " + RECEIVER_SERVICE_PATH);
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, receiveMessage.nodeId, RECEIVER_SERVICE_PATH,
                                null);

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // Do something

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        // Do something

                    }
                })
                .addApi(Wearable.API)
                .build();
        this.mGoogleApiClient.connect();


        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

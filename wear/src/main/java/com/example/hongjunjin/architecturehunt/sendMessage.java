package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;


public class sendMessage extends Service {

    private GoogleApiClient mGoogleApiClient;
    private String RECEIVER_SERVICE_PATH;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do


        Toast.makeText(this, "Successfully favorite it!", Toast.LENGTH_SHORT).show();
        RECEIVER_SERVICE_PATH = intent.getStringExtra("key");


        Log.d("ADebugTag", "sendMessage photoId: " + RECEIVER_SERVICE_PATH);


        Log.d("ADebugTag", "test: " + "in sendMessage");

        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        // Do something
                        Log.d("ADebugTag", "test: " + "connected");
                        Log.d("ADebugTag", "test: " + RECEIVER_SERVICE_PATH);
                        Wearable.MessageApi.sendMessage(mGoogleApiClient, messengerService.nodeId, RECEIVER_SERVICE_PATH,
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

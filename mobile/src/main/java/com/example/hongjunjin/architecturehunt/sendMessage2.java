package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Created by hongjunjin on 7/28/15.
 */
public class sendMessage2 extends Service {

    private GoogleApiClient messageAPIclient;
    protected static final String RECEIVER_SERVICE_PATH = "/compass";
    protected static final String CAPABILITY_NAME = "compass";
    private Intent start_intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do

        Log.d("ADebugTag", "test: " + "in sendMessage2");
        start_intent = intent;

        this.messageAPIclient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d("ADebugTag", "test: " + "connected");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CapabilityApi.GetCapabilityResult capResult =
                                        Wearable.CapabilityApi.getCapability(messageAPIclient, CAPABILITY_NAME,
                                                CapabilityApi.FILTER_REACHABLE).await();
                                Set<Node> nodes = capResult.getCapability().getNodes();
                                for (Node node : nodes) {
                                    if (start_intent.getFloatExtra("distance", -1) != -1) {
                                        float temp = start_intent.getFloatExtra("distance", -1);
                                        byte[] distance = ByteBuffer.allocate(4).putFloat(temp).array();
                                        Wearable.MessageApi.sendMessage(messageAPIclient, node.getId(), RECEIVER_SERVICE_PATH + "/distance",
                                                distance);
                                        temp = start_intent.getFloatExtra("rot", 0);
                                        byte[] rot = ByteBuffer.allocate(4).putFloat(temp).array();
                                        Wearable.MessageApi.sendMessage(messageAPIclient, node.getId(), RECEIVER_SERVICE_PATH + "/rot",
                                                rot);
                                        Log.d("ADebugTag", "DISTROT Message sent");
                                    }
                                }
                            }
                        }).start();

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
                        Log.d("ADebugTag", "failedListener: " + "failed");

                    }
                })
                .addApi(Wearable.API)
                .build();
        this.messageAPIclient.connect();

        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

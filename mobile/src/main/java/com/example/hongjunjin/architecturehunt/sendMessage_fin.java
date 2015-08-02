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
public class sendMessage_fin extends Service {

    private GoogleApiClient messageAPIclient;
    protected static final String RECEIVER_SERVICE_PATH = "/compass";
    protected static final String CAPABILITY_NAME = "compass";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do

        Log.d("ADebugTag", "test: " + "in sendMessage_fin");

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
                                        Wearable.MessageApi.sendMessage(messageAPIclient, node.getId(), RECEIVER_SERVICE_PATH + "/finish",
                                                null);
                                        Log.d("ADebugTag", "Finish Message sent");
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

package com.example.hongjunjin.architecturehunt;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by hongjunjin on 7/28/15.
 */
public class messengerService extends WearableListenerService {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do
        Log.d("ADebugTag", "test: " + "in messenger service");

        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("ADebugTag", "test: " + "hullllllllluuuuuu");
        String name = messageEvent.getPath();
        Log.d("ADebugTag", "test: " + name);
    }
}

package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


/**
 * Created by Gordon Lai on 8/1/2015.
 */
public class LocalService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "in LocalService!!!!!!");

        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        if (intent.getBooleanExtra("finish", false)) {
            Log.d("LocalService", "received the finish message from messengerService");
            Intent finish = new Intent("new_dist_rot");
            finish.putExtra("finish", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finish);
            Log.d("LocalService", "finish message broadcasted");

        }
        else {
            Intent broadcast = new Intent("new_dist_rot");
            broadcast.putExtra("data", intent.getBundleExtra("data"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            Log.d("LocalService", "DISTROT broadcasted");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

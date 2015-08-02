package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Gordon Lai on 8/1/2015.
 */
public class LocalService extends Service {
    @Override
    public void onCreate() {
        Log.d("LocalService", "Started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        if (intent.getBooleanExtra("finish", false)) {
            Log.d("ADebugTag", "received the finish message from messengerService");
            Intent finish = new Intent("new_dist_rot");
            finish.putExtra("finish", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finish);
        }
        else {
            Intent broadcast = new Intent("new_dist_rot");
            broadcast.putExtra("data", intent.getBundleExtra("data"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
            Log.d("LocalService", "DISTROT Sent to MainActivity");
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

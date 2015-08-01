package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by hongjunjin on 8/1/15.
 */
public class addFavorite extends Service{


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

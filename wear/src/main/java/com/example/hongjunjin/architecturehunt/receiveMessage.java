package com.example.hongjunjin.architecturehunt;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by hongjunjin on 7/30/15.
 */
public class receiveMessage extends WearableListenerService {

    protected static String nodeId;
    protected static NotificationManagerCompat notificationManager;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("ADebugTag", "test: " + "what the heck");

        nodeId = messageEvent.getSourceNodeId();
        notification();
    }

    public void notification(){

        // create Intent to take a picture and return control to the calling application

        Intent cameraIntent = new Intent(this, sendMessage.class);
        cameraIntent.putExtra("key", "camera");
        cameraIntent.setAction("opening a camera");
        PendingIntent cameraPendingIntent = PendingIntent.getService(this, 0, cameraIntent, 0);

        Intent favoriteIntent = new Intent(this, sendMessage.class);
        favoriteIntent.putExtra("key", "favorite");
        favoriteIntent.setAction("set favorite");
        PendingIntent favoritePendingIntent = PendingIntent.getService(this, 0, favoriteIntent, 0);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("ArchitectureHunt")
                .setContentText("You've arrived.")
                .setContentIntent(cameraPendingIntent)
                .setContentIntent(favoritePendingIntent)
                .addAction(R.drawable.flickr, "Favorite", favoritePendingIntent)
                .addAction(R.drawable.camera, "Take a picture", cameraPendingIntent);


        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(7, notificationBuilder.build());

    }

}

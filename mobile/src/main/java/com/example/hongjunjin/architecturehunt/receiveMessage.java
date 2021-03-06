package com.example.hongjunjin.architecturehunt;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;


public class receiveMessage extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("ADebugTag", "test: " + "what the fuck");

        if (messageEvent.getPath().equals("camera")){

            Log.d("ADebugTag", "test: " + "in camera");
            Intent cameraIntent = new Intent(this, cameraActivity.class);
            cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(cameraIntent);

        }else{
            Log.d("ADebugTag", "test: " + "in favorite");
            String photoId = messageEvent.getPath();

            Log.d("ADebugTag", "receiveMessage photoId: " + photoId);

            Intent addFavIntent = new Intent(this, addFavorite.class);
            addFavIntent.putExtra("photoId", photoId);
            startService(addFavIntent);

        }

    }

}

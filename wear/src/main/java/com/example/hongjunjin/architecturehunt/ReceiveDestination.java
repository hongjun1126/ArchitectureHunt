package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ReceiveDestination extends WearableListenerService {
    private static final String RECEIVER_SERVICE_PATH = "/from_mobile_device";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("", "wear recevied");
        String message = new String(messageEvent.getData());
        if( messageEvent.getPath().equalsIgnoreCase( RECEIVER_SERVICE_PATH ) ) {
            Intent intent = new Intent( this, MainActivity.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity( intent );
        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}

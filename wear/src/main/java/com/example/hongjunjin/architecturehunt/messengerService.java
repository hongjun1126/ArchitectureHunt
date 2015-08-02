package com.example.hongjunjin.architecturehunt;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;


/**
 * Created by hongjunjin on 7/28/15.
 */
public class messengerService extends WearableListenerService {

    private byte[] pic;
    private byte[] loc;
    private byte[] distance;
    private byte[] rot;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do
        Log.d("ADebugTag", "test: " + "in messenger service");

        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d("ADebugTag", "Message received on wear");
        String name = messageEvent.getPath();
        if (name.contentEquals("/compass/pic")) {
            Log.d("ADebugTag", "message_path: " + name);
            pic = messageEvent.getData();
        } else if (name.contentEquals("/compass/loc")) {
            Log.d("ADebugTag", "message_path: " + name);
            loc = messageEvent.getData();
            if (pic != null && loc != null) {
                byte[] the_pic = pic;
                byte[] the_loc = loc;

                Intent intent = new Intent(this, MainActivity.class);
                Bitmap bm = BitmapFactory.decodeByteArray(the_pic, 0, the_pic.length);
                intent.putExtra("pic", bm);
                ByteBuffer buffer = ByteBuffer.wrap(the_loc);
                float[] float_loc = new float[2];
                float_loc[0] = buffer.getFloat();
                float_loc[1] = buffer.getFloat();
                intent.putExtra("loc", float_loc);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                pic = null;
                loc = null;
            }
        } else if (name.contentEquals("/compass/distance")) {
            Log.d("ADebugTag", "message_path: " + name);
            distance = messageEvent.getData();
        } else if (name.contentEquals("/compass/rot")) {
            Log.d("ADebugTag", "message_path: " + name);

            rot = messageEvent.getData();
//            if (distance == null) {
//                Log.d("ADebugTag", "distance objnull");
//            }
//            if (rot == null) {
//                Log.d("ADebugTag", "rot obj null");
//            }
            if (distance != null && rot != null) {
                byte[] the_dist = distance;
                byte[] the_rot = rot;

                Bundle data = new Bundle();
                data.putFloat("distance", ByteBuffer.wrap(the_dist).getFloat());
                data.putFloat("rot", ByteBuffer.wrap(the_rot).getFloat());
                Intent intent = new Intent(this, LocalService.class);
                intent.putExtra("data", data);
                Log.e("ADebugTag", "ready for rot launch");

                startService(intent);

            }
        } else if (name.contentEquals("/compass/finish")) {
            Log.d("ADebugTag", "message_path: " + name);
            Intent intent = new Intent(this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}

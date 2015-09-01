package com.example.hongjunjin.architecturehunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;


/**
 * Created by hongjunjin on 7/28/15.
 */
public class messengerService extends WearableListenerService {

    private byte[] pic;
    private byte[] loc;
    private byte[] photoId;
    protected static String nodeId;
    private Intent intent;
    private byte[] distance;
    private byte[] rot;
    protected static Bitmap bm;
    protected static float[] float_loc;
    protected static String pictureId;


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        nodeId = messageEvent.getSourceNodeId();


        Log.d("ADebugTag", "Message received on wear");


        String name = messageEvent.getPath();

        if (name.contentEquals("/compass/pic")) {
            Log.d("ADebugTag", "message_path: " + name);
            pic = messageEvent.getData();


        }else if(name.contentEquals("/compass/photoId")){
            Log.d("ADebugTag", "message_path: " + name);
            photoId = messageEvent.getData();

        }else if (name.contentEquals("/compass/loc")) {
            Log.d("ADebugTag", "message_path: " + name);

            loc = messageEvent.getData();

        }else if (name.contentEquals("/compass/distance")) {
            Log.d("ADebugTag", "message_path: " + name);
            distance = messageEvent.getData();
        }else if (name.contentEquals("/compass/rot")) {
            Log.d("ADebugTag", "message_path: " + name);
            rot = messageEvent.getData();

            if (distance != null && rot != null) {
                byte[] the_dist = distance;
                byte[] the_rot = rot;

                Bundle data = new Bundle();
                data.putFloat("distance", ByteBuffer.wrap(the_dist).getFloat());
                data.putFloat("rot", ByteBuffer.wrap(the_rot).getFloat());
                Intent intentLocal = new Intent(this, LocalService.class);
                intentLocal.putExtra("data", data);
                Log.e("ADebugTag", "ready for rot launch");

                startService(intentLocal);

            }
        }else {
            Log.d("ADebugTag", "message_path: " + name);
            Intent intentA = new Intent(this, LocalService.class);
            intentA.putExtra("finish", true);
            startService(intentA);
        }

        loadData();

    }

    public void loadData(){
        if (pic != null && loc != null && photoId != null) {
            byte[] the_pic = pic;
            byte[] the_loc = loc;
            byte[] id = photoId;


            Log.d("ADebugTag", "ready for compass launch");


            intent = new Intent(this, MainActivity.class);
            bm = BitmapFactory.decodeByteArray(the_pic, 0, the_pic.length);

            ByteBuffer buffer = ByteBuffer.wrap(the_loc);
            float_loc = new float[2];
            float_loc[0] = buffer.getFloat();
            float_loc[1] = buffer.getFloat();

            pictureId =new String(id, StandardCharsets.US_ASCII);

            pic = null;
            loc = null;
            photoId = null;

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

    }
}

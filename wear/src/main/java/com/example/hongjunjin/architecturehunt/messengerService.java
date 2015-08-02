package com.example.hongjunjin.architecturehunt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int counter = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Kick off new work to do
        Log.d("ADebugTag", "test: " + "in messenger service");

        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        nodeId = messageEvent.getSourceNodeId();

        Log.d("ADebugTag", "Message received on wear");
        String name = messageEvent.getPath();
        if (name.contentEquals("/compass/pic")) {
            Log.d("ADebugTag", "message_path: " + name);
            pic = messageEvent.getData();
            counter += 1;
        }else if(name.contentEquals("/compass/photoId")){
            Log.d("ADebugTag", "message_path: " + name);
            photoId = messageEvent.getData();

            Log.d("ADebugTag", "photoId : " + photoId);
            counter += 1;
        }
        else if (name.contentEquals("/compass/loc")) {
            Log.d("ADebugTag", "message_path: " + name);
            Log.d("ADebugTag", "pic : " + pic);
            Log.d("ADebugTag", "loc : " + loc);
            Log.d("ADebugTag", "photoId : " + photoId);

            counter += 1;

        }

        if (counter == 3){
            add(messageEvent);
            counter = 0;
        }
    }

    public void add(MessageEvent messageEvent){

        loc = messageEvent.getData();
        if (pic != null && loc != null && photoId != null) {
            byte[] the_pic = pic;
            byte[] the_loc = loc;
            byte[] id = photoId;

            Log.d("ADebugTag", "ready for compass launch");
            intent = new Intent(this, MainActivity.class);
            Bitmap bm = BitmapFactory.decodeByteArray(the_pic, 0, the_pic.length);
            intent.putExtra("pic", bm);
            ByteBuffer buffer = ByteBuffer.wrap(the_loc);
            float[] float_loc = new float[2];
            float_loc[0] = buffer.getFloat();
            float_loc[1] = buffer.getFloat();
            intent.putExtra("loc", float_loc);

            String pictureId =new String(id, StandardCharsets.US_ASCII);
            intent.putExtra("photoId", pictureId);

            Log.d("ADebugTag", "messenger SErvice: " + pictureId);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            pic = null;
            loc = null;
            photoId = null;


        }

    }
}

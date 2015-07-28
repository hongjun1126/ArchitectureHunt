package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hongjunjin on 7/27/15.
 */
public class selectedActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Log.d("ADebugTag", "in selectedActivity: " + "in onCreate");



    }
}

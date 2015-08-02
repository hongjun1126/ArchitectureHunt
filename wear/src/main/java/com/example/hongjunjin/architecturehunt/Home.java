package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Gordon Lai on 7/30/2015.
 */
public class Home extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        Log.d("INHOME", "CREATED");
        if (!isTaskRoot()){
            finish();
        }
        else {
            Log.d("INHOME", "CREATED_ISTASKROOT");
        }
    }
}

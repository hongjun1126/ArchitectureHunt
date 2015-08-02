package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class postUpload extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_upload);

        Button btn = (Button) findViewById(R.id.backButton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startNewActivity();
            }
        });


    }

    public void startNewActivity(){

        Intent searchIntent = new Intent(this, locationActivity.class);
        startActivity(searchIntent);

    }

}

package com.example.hongjunjin.architecturehunt;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

/**
 * Created by hongjunjin on 7/28/15.
 */
public class MyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RowItem item = locationActivity.getItem();
        Bitmap img = item.getBmp();
        float height = locationActivity.getFrag_img().getMeasuredHeight();
        float scale = height / img.getHeight();

        img = Bitmap.createScaledBitmap(img, Math.round(img.getWidth() * scale), Math.round(height), true);
        locationActivity.getFrag_img().setImageBitmap(img);
        locationActivity.getFrag_name().setText("\"" + item.getTitle() + "\"");
        locationActivity.getFlayout().setVisibility(View.VISIBLE);

    }

}

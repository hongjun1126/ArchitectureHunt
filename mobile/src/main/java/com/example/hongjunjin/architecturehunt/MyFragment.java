package com.example.hongjunjin.architecturehunt;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by hongjunjin on 7/28/15.
 */
public class MyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RowItem item = locationActivity.item;
        Bitmap img = item.getBmp();
        float height = locationActivity.frag_img.getMeasuredHeight();
        float scale = height / img.getHeight();

        img = Bitmap.createScaledBitmap(img, Math.round(img.getWidth() * scale), Math.round(height), true);
        locationActivity.frag_img.setImageBitmap(img);
        locationActivity.frag_name.setText(item.getTitle());
        locationActivity.flayout.setVisibility(View.VISIBLE);

    }

}

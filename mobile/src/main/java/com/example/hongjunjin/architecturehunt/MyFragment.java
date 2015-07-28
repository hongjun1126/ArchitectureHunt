package com.example.hongjunjin.architecturehunt;

import android.app.Fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RowItem item = locationActivity.item;

        ImageView img = new ImageView(getActivity());
        img.setImageBitmap(item.getBmp());


        locationActivity.GPSbutton.setVisibility(View.VISIBLE);
        locationActivity.compassButton.setVisibility(View.VISIBLE);
        locationActivity.backButton.setVisibility(View.VISIBLE);
        locationActivity.ll.setAlpha(0.3f);

        return img;
    }
}

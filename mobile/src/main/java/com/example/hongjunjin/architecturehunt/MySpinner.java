package com.example.hongjunjin.architecturehunt;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by hongjunjin on 8/11/15.
 */

public class MySpinner extends Spinner {
    OnItemSelectedListener listener;

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setSelection(int position) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position);
        if (sameSelected) {
            // Spinner does not call the OnItemSelectedListener if the same item is selected, so do it manually now
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }

    }

    public void setOnItemSelectedEvenIfUnchangedListener(
            OnItemSelectedListener listener) {
        this.listener = listener;
    }
}
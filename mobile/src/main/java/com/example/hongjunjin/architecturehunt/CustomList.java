package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

/**
 * Created by hongjunjin on 7/22/15.
 */
public class CustomList extends ArrayAdapter<RowItem> {

    Context context;
    protected static final int IMAGE_PIXEL = 30;

    public CustomList(Context context, int resourceId, List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtDist;
        TextView favNumber;
        ImageView imageStar;
        LinearLayout background;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem, null);
            holder = new ViewHolder();

            holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img);
            holder.txtDist = (TextView) convertView.findViewById(R.id.distance);
            holder.favNumber = (TextView) convertView.findViewById(R.id.favorite);
            holder.imageStar = (ImageView) convertView.findViewById(R.id.star);
            holder.background = (LinearLayout) convertView.findViewById(R.id.item_background);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (rowItem.navigating) {
            holder.txtTitle.setText(rowItem.getTitle() + " (NAVIGATING)");
            holder.txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else if (rowItem.navigated) {
            holder.txtTitle.setText(rowItem.getTitle() + " (SEEN)");
            holder.txtTitle.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            holder.txtTitle.setText(rowItem.getTitle());
            holder.txtTitle.setTypeface(Typeface.DEFAULT);
        }

        Bitmap bmpSquare = ImageHelper.cropToSquare(rowItem.getBmp());
        Bitmap squareRoundBmp = ImageHelper.getRoundedCornerBitmap(bmpSquare, IMAGE_PIXEL);

        holder.imageView.setImageBitmap(squareRoundBmp);
        Bitmap img = rowItem.getBmp();
        float width = parent.getWidth();
        float scale = width / img.getWidth();

        img = Bitmap.createScaledBitmap(img, Math.round(width), Math.round(img.getHeight() * scale), true);
        float density = getContext().getResources().getDisplayMetrics().density;
        int px = Math.round(80 * density);
        img = Bitmap.createBitmap(img, 0, img.getHeight() / 3, img.getWidth(), px);

        BitmapDrawable ob = new BitmapDrawable(getContext().getResources(), img);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        ob.setColorFilter(filter);
        holder.background.setBackground(ob);
        holder.background.getBackground().setAlpha(30);
        holder.txtDist.setText(rowItem.getDistInString());
        holder.favNumber.setText(rowItem.getFavInString());
        holder.imageStar.setImageResource(R.drawable.flickrccc);

        return convertView;
    }


}

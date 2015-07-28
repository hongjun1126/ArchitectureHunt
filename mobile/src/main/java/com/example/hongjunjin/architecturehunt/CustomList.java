package com.example.hongjunjin.architecturehunt;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by hongjunjin on 7/22/15.
 */
public class CustomList extends ArrayAdapter<RowItem> {

    Context context;

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

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

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
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtTitle.setText(rowItem.getTitle());
        holder.imageView.setImageBitmap(rowItem.getBmp());
        holder.txtDist.setText(rowItem.getDistInString());
        holder.favNumber.setText(rowItem.getFavInString());
        holder.imageStar.setImageResource(R.drawable.flickr_star);

        return convertView;
    }

}

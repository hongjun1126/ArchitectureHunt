package com.example.hongjunjin.architecturehunt;

import android.graphics.Bitmap;

/**
 * Created by hongjunjin on 7/22/15.
 */
public class RowItem {

    private Bitmap bmp;
    private String title;
    private float dist;
    private int favoriteNum;
    private float[] loc;

    public RowItem(Bitmap bmp, String title, float distance, int favoriteNum, float[] loc){
        this.bmp = bmp;
        this.title = title;
        this.dist = distance;
        this.favoriteNum = favoriteNum;
        this.loc = loc;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public String getTitle() { return title; }

    public String getDistInString() {
        return Double.toString(roundOff(dist)) + " mi";
    }

    public float getDist(){
        return dist;
    }

    public float[] getLoc() { return loc; }

    public double roundOff(float dist){
        return Math.round(dist * 10.0) / 10.0;
    }

    public int getFavoriteNum() {
        return favoriteNum;
    }

    public String getFavInString(){
        return Integer.toString(favoriteNum);
    }
}

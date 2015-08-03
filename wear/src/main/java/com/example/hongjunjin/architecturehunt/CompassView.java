package com.example.hongjunjin.architecturehunt;

import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by winni on 7/28/2015.
 */
public class CompassView extends View
{
    private float direction = 0;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean firstDraw;

    public CompassView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
        init();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
        init();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
// TODO Auto-generated constructor stub
        init();
    }

    private void init(){

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);

        firstDraw = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
// TODO Auto-generated method stub
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
// TODO Auto-generated method stub

        int cxCompass = getMeasuredWidth()/2;
        int cyCompass = getMeasuredHeight()/2;
        float radiusCompass;

        if(cxCompass > cyCompass){
            radiusCompass = (float) (cyCompass * 0.9);
        }
        else{
            radiusCompass = (float) (cxCompass * 0.9);
        }

        Log.d("", "cxCompass" + cxCompass);
        Log.d("", "cyCompass" + cyCompass);
        Log.d("", "radiusCompass" + radiusCompass);
        canvas.drawCircle(cxCompass, cyCompass, radiusCompass, paint);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        if(!firstDraw){

            canvas.drawLine(cxCompass, cyCompass,
                    (float)(cxCompass + radiusCompass * Math.sin((double)(-direction) * 3.14/180)),
                    (float)(cyCompass - radiusCompass * Math.cos((double)(-direction) * 3.14/180)),
                    paint);

            canvas.drawText(String.valueOf(direction), cxCompass, cyCompass, paint);
        }

    }

    public void updateDirection(float dir)
    {
        firstDraw = false;
        direction = dir;
        invalidate();
    }
}

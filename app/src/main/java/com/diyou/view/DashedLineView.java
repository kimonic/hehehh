package com.diyou.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedLineView extends View
{
    public DashedLineView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public DashedLineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DashedLineView(Context context)
    {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.DKGRAY);
        Path path = new Path();
        path.moveTo(0, 10);
        path.lineTo(2480, 10);
        PathEffect effects = new DashPathEffect(new float[]
        { 5, 5, 5, 5 }, 1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }
}

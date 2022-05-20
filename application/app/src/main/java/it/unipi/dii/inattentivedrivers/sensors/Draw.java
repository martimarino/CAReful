package it.unipi.dii.inattentivedrivers.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Draw extends View {

    Paint boundryPaint, textPaint;
    Rect rect;
    String text;
    String text1;
    String text2;
    String text3;
    String text4;

    public Draw(Context context, Rect rect, String text, String text1, String text2, String text3, String text4) {
        super(context);
        this.rect = rect;
        this.text = text;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
        this.text4 = text4;


        boundryPaint = new Paint();
        boundryPaint.setColor(Color.YELLOW);
        boundryPaint.setStrokeWidth(7f);
        boundryPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setTextSize(42f);
        textPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText("Rot x: " + text, rect.centerX(), rect.centerY() + 42f, textPaint);
        canvas.drawText("Rot y: " + text1, rect.centerX(), rect.centerY() + 84f, textPaint);
        canvas.drawText("Rot z: " + text2, rect.centerX(), rect.centerY() + 126f, textPaint);
        canvas.drawText("Left eye: " + text3, rect.centerX(), rect.centerY() + 168f, textPaint);
        canvas.drawText("Right eye: " + text4, rect.centerX(), rect.centerY() + 210f, textPaint);
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, boundryPaint);
    }
}

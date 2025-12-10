package com.example.firstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {

    private Paint carPaint;

    private float carX, carY;
    private final float carWidth = 120f, carHeight = 200f;

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            update();
            invalidate();
            postDelayed(this, 16);
        }
    };

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        carPaint = new Paint();
        carPaint.setColor(Color.RED);

        post(updateRunnable);
    }
    private void update() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(50, 50, 50));

        canvas.drawRect(
                carX,
                carY,
                carX + carWidth,
                carY + carHeight,
                carPaint
        );
    }
}

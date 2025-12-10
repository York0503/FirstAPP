package com.example.firstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {

    private Paint carPaint; // 車子畫筆
    private Paint roadPaint; // 車道畫筆
    private Paint linePaint; // 車道中線畫筆(黃色的虛線)

    private float carX, carY; // 車子的位置
    private final float carWidth = 120f, carHeight = 200f; // 車子本身的寬度跟高度
    private float lineOffset = 0f; // 車道中線的偏移量

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
        // 車子畫筆
        carPaint = new Paint();
        carPaint.setColor(Color.RED);

        // 道路畫筆
        roadPaint = new Paint();
        roadPaint.setColor(Color.GRAY);

        // 中線畫筆
        linePaint = new Paint();
        linePaint.setColor(Color.YELLOW);
        linePaint.setStrokeWidth(10f);

        // 車子的初始位置
        carX = 300f;
        carY = 100f;

        post(updateRunnable);
    }
    private void update() {
        // 車子向下移動5pixel
        carY += 5;

        // 如果車子超過底部邊界，則回到最上方
        if(carY > getHeight()){
            carY = -carHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.GREEN);

        float roadLeft = getWidth() * 0.2f;
        float roadRight = getWidth() * 0.8f;

        canvas.drawRect(
                roadLeft,
                0,
                roadRight,
                getHeight(),
                roadPaint
        );

        float centerX = (roadLeft + roadRight) / 2;
        float dashLength = 60f;
        float gapLength = 40f;

        for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength){
            canvas.drawLine(
                    centerX,
                    y,
                    centerX,
                    y + dashLength,
                    linePaint);
        }

        float roadCenter = (roadLeft + roadRight) / 2;
        carX = roadCenter - (carWidth / 2);

        canvas.drawRect(
                carX,
                carY,
                carX + carWidth,
                carY + carHeight,
                carPaint
        );
    }
}

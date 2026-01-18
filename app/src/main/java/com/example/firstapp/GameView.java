package com.example.firstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class GameView extends View {

    private Paint carPaint; // 車子畫筆
    private Paint roadPaint; // 車道畫筆
    private Paint linePaint; // 車道分隔線畫筆
    private Paint buttonPaint; // 按鈕畫筆
    private Paint textPaint; // 文字畫筆

    // 玩家車輛
    private float carX, carY; // 車輛當前位置
    private final float carWidth = 100f;
    private final float carHeight = 200f;

    // 車道系統
    private final float[] lanes = new float[3]; // 3 條車道的中心 X 座標
    private int currentLane = 1; // 當前車道 (0=左, 1=中, 2=右)
    private float targetX; // 目標 X 座標（用於平滑移動）

    private float lineOffset = 0f; // 車道線的偏移量（動畫效果）

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

        // 車道分隔線畫筆（白色）
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(8f);

        // 按鈕畫筆（半透明深色）
        buttonPaint = new Paint();
        buttonPaint.setColor(Color.argb(150, 50, 50, 50)); // 半透明深灰色
        buttonPaint.setStyle(Paint.Style.FILL);
        buttonPaint.setAntiAlias(true); // 抗鋸齒

        // 文字畫筆
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        // 初始化車輛位置
        carY = 100f; // 垂直位置
        currentLane = 1; // 從中間車道開始

        post(updateRunnable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 計算道路和車道位置
        float roadLeft = w * 0.2f;
        float roadRight = w * 0.8f;
        float roadWidth = roadRight - roadLeft;

        // 計算 3 條車道的中心 X 座標（每個車道寬度相等）
        lanes[0] = roadLeft + roadWidth / 6f;      // 左車道中心（在 1/6 位置）
        lanes[1] = roadLeft + roadWidth / 2f;      // 中車道中心（在 1/2 位置）
        lanes[2] = roadLeft + roadWidth * 5f / 6f; // 右車道中心（在 5/6 位置）

        // 設定初始目標位置為中間車道
        targetX = lanes[currentLane];
        carX = targetX;
    }

    private void update() {
        // 車輛向下移動
        carY += 5f;
        if (carY > getHeight()) {
            carY = -carHeight; // 回到頂部
        }

        // 平滑移動到目標車道（線性固定，還沒有加速度）
        carX += (targetX - carX) * 0.1f;

        // 車道線向下移動（動畫效果）
        lineOffset += 5f;
        if (lineOffset > 100f) {
            lineOffset = 0f;
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // 1. 繪製背景（草地綠）
        canvas.drawColor(Color.GREEN);

        // 2. 計算道路邊界
        float roadLeft = getWidth() * 0.2f;
        float roadRight = getWidth() * 0.8f;

        // 3. 繪製道路主體（灰色）
        canvas.drawRect(roadLeft, 0, roadRight, getHeight(), roadPaint);

        // 4. 繪製車道分隔線（2 條白線，分隔成 3 個車道）
        float roadWidth = roadRight - roadLeft;
        float dashLength = 60f;
        float gapLength = 40f;

        // 左分隔線（在 1/3 位置）
        float leftLineX = roadLeft + roadWidth / 3f;
        for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength) {
            canvas.drawLine(leftLineX, y, leftLineX, y + dashLength, linePaint);
        }

        // 右分隔線（在 2/3 位置）
        float rightLineX = roadLeft + roadWidth * 2f / 3f;
        for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength) {
            canvas.drawLine(rightLineX, y, rightLineX, y + dashLength, linePaint);
        }

        // 5. 繪製車輛（以中心座標繪製）
        float carLeft = carX - carWidth / 2f;
        canvas.drawRect(carLeft, carY, carLeft + carWidth, carY + carHeight, carPaint);

        // 6. 繪製底部控制按鈕
        float screenHeight = getHeight();
        float screenWidth = getWidth();

        float buttonTop = screenHeight * 0.80f; // 按鈕從 80% 高度開始
        float buttonBottom = screenHeight * 0.95f; // 到 95% 高度
        float buttonMargin = 20f; // 按鈕邊距
        float buttonGap = screenWidth * 0.1f; // 按鈕間隙
        float cornerRadius = 30f; // 圓角半徑

        // 向左切換車按鈕
        if (currentLane > 0) { // 只有不在最左車道時顯示
            canvas.drawRoundRect(
                    buttonMargin,
                    buttonTop,
                    screenWidth * 0.45f - buttonGap / 2f,
                    buttonBottom,
                    cornerRadius, cornerRadius,
                    buttonPaint
            );
            canvas.drawText("◀", screenWidth * 0.225f, buttonTop + (buttonBottom - buttonTop) / 2f + 30, textPaint);
        }

        // 向右切換車道按鈕
        if (currentLane < 2) { // 只有不在最右車道時顯示
            canvas.drawRoundRect(
                    screenWidth * 0.55f + buttonGap / 2f,
                    buttonTop,
                    screenWidth - buttonMargin,
                    buttonBottom,
                    cornerRadius, cornerRadius,
                    buttonPaint
            );
            canvas.drawText("▶", screenWidth * 0.775f, buttonTop + (buttonBottom - buttonTop) / 2f + 30, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();
            float screenHeight = getHeight();
            float screenWidth = getWidth();

            // 檢測底部按鈕區域（80%-95% 高度）
            if (touchY > screenHeight * 0.80f && touchY < screenHeight * 0.95f) {
            // 左按鈕區域（0% - 45% 寬度）
                if (touchX < screenWidth * 0.45f) {
                    if (currentLane > 0) {
                        currentLane--;
                        targetX = lanes[currentLane];
                        performClick(); // 修復診斷警告
                        return true;
                    }
                }
                // 右按鈕區域（55% - 100% 寬度）
                else if (touchX > screenWidth * 0.55f) {
                    if (currentLane < 2) {
                        currentLane++;
                        targetX = lanes[currentLane];
                        performClick(); // 修復診斷警告
                        return true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}

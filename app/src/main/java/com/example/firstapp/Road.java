package com.example.firstapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * 道路實體（車道/虛線動畫/邊界）
 *
 * 職責：
 * 1. 根據螢幕尺寸計算道路邊界和車道位置
 * 2. 更新虛線動畫偏移量
 * 3. 繪製三層畫面：綠色草地 → 灰色道路 → 白色虛線
 * 4. 提供邊界座標給 CollisionDetector 使用
 */
public class Road {

    // ===== 道路幾何（由 init() 計算） =====
    private float roadLeft;         // 道路左邊界 X 座標
    private float roadRight;        // 道路右邊界 X 座標
    private float roadWidth;        // 道路寬度

    // ===== 車道系統 =====
    private final float[] lanes = new float[GameConfig.LANE_COUNT]; // 車道中心 X 座標陣列

    // ===== 虛線動畫 =====
    private float lineOffset = 0f;  // 虛線偏移量（每幀增加，產生道路流動效果）

    // ===== 螢幕資訊 =====
    private int screenHeight;       // 畫面高度（虛線繪製終點）

    // ===== 畫筆 =====
    private final Paint roadPaint;  // 道路畫筆（灰色）
    private final Paint linePaint;  // 分隔線畫筆（白色虛線）

    /**
     * 建構子：初始化畫筆
     * 幾何計算延遲到 init()，因為建構時還不知道螢幕尺寸
     */
    public Road() {
        // 道路畫筆：灰色填滿
        roadPaint = new Paint();
        roadPaint.setColor(Color.GRAY);

        // 分隔線畫筆：白色線條
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(GameConfig.LINE_STROKE_WIDTH); // 線寬 8px
    }

    /**
     * 根據螢幕尺寸計算道路/車道位置
     * 在 GameView.onSizeChanged() 中呼叫（螢幕尺寸確定後）
     *
     * @param screenWidth  螢幕寬度
     * @param screenHeight 螢幕高度
     */
    public void init(int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;

        // 道路佔螢幕中間 60%（左 20% ~ 右 80%）
        roadLeft = screenWidth * GameConfig.ROAD_LEFT_RATIO;   // 左邊界
        roadRight = screenWidth * GameConfig.ROAD_RIGHT_RATIO; // 右邊界
        roadWidth = roadRight - roadLeft;                       // 道路總寬

        // 計算 3 條車道的中心 X 座標
        // 將道路等分為 3 份，每份中心分別在 1/6、3/6、5/6 位置
        lanes[0] = roadLeft + roadWidth / 6f;       // 左車道中心
        lanes[1] = roadLeft + roadWidth / 2f;       // 中車道中心
        lanes[2] = roadLeft + roadWidth * 5f / 6f;  // 右車道中心
    }

    /**
     * 每幀更新虛線偏移量（產生道路向下流動的視覺效果）
     * 偏移量達到一個完整週期（虛線段 + 間隔）後歸零
     */
    public void update() {
        lineOffset += GameConfig.LINE_SCROLL_SPEED;  // 每幀偏移 5 像素
        if (lineOffset > GameConfig.DASH_LENGTH + GameConfig.GAP_LENGTH) {
            lineOffset = 0f;  // 重置偏移（一個週期 = 60 + 40 = 100 像素）
        }
    }

    /**
     * 繪製道路（三層結構）
     *
     * Layer 1: 綠色草地背景（canvas.drawColor）
     * Layer 2: 灰色道路主體（drawRect）
     * Layer 3: 白色虛線分隔線（drawLine 迴圈）
     */
    public void draw(Canvas canvas) {
        // Layer 1: 綠色草地背景（覆蓋整個畫布）
        canvas.drawColor(Color.GREEN);

        // Layer 2: 灰色道路主體
        canvas.drawRect(roadLeft, 0, roadRight, screenHeight, roadPaint);

        // Layer 3: 白色虛線分隔線（2 條線分隔 3 個車道）
        // 分隔線位置：道路寬度的 1/3 和 2/3 處
        float leftLineX = roadLeft + roadWidth / 3f;    // 左分隔線 X 座標
        float rightLineX = roadLeft + roadWidth * 2f / 3f; // 右分隔線 X 座標

        // 繪製虛線：從 lineOffset 開始，每段長 DASH_LENGTH，間隔 GAP_LENGTH
        float period = GameConfig.DASH_LENGTH + GameConfig.GAP_LENGTH; // 一個週期 = 100px
        for (float y = lineOffset; y < screenHeight; y += period) {
            // 左分隔線的一段虛線
            canvas.drawLine(leftLineX, y, leftLineX, y + GameConfig.DASH_LENGTH, linePaint);
            // 右分隔線的一段虛線
            canvas.drawLine(rightLineX, y, rightLineX, y + GameConfig.DASH_LENGTH, linePaint);
        }
    }

    // ===== Getter（碰撞偵測和車輛初始定位用） =====

    /** 取得車道中心座標陣列 */
    public float[] getLanes() { return lanes; }

    /** 取得道路左邊界 X 座標 */
    public float getRoadLeft() { return roadLeft; }

    /** 取得道路右邊界 X 座標 */
    public float getRoadRight() { return roadRight; }
}

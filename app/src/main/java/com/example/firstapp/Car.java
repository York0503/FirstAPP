package com.example.firstapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 車輛實體（含物理引擎：位置/速度/角度）
 *
 * 物理模型：
 * - 按鈕控制的是「轉向角度」（steeringAngle），不是直接切車道
 * - 每幀透過 sin/cos 將角度分解為 X/Y 速度分量
 * - Lerp 平滑轉向，讓方向盤有「轉動」手感
 *
 * 座標系：
 *   0° = 正下方（Y+ 方向）
 *   正值 = 右轉（順時針）
 *   負值 = 左轉（逆時針）
 */
public class Car {

    // ===== 位置狀態 =====
    private float x;                // 車輛中心 X 座標
    private float y;                // 車輛頂部 Y 座標

    // ===== 物理狀態 =====
    private float angle = 0f;       // 當前車頭角度（度數，0°=正下方）
    private float steeringAngle = 0f; // 目標轉向角度（按按鈕時設定，放開時為 0）
    private final float speed;      // 前進速度（固定值，從 GameConfig 讀取）

    // ===== 尺寸 =====
    private final float width;      // 車寬（從 GameConfig 讀取）
    private final float height;     // 車高（從 GameConfig 讀取）

    // ===== 繪製 =====
    private final Paint paint;      // 車輛畫筆（紅色）

    /**
     * 建構子：初始化車輛畫筆和物理參數
     * 位置稍後由 GameView.onSizeChanged() 設定
     */
    public Car() {
        // 從 GameConfig 讀取常數（消除魔術數字）
        this.width = GameConfig.CAR_WIDTH;
        this.height = GameConfig.CAR_HEIGHT;
        this.speed = GameConfig.CAR_SPEED;

        // 初始化畫筆
        paint = new Paint();
        paint.setColor(Color.RED);   // 紅色車輛
    }

    /**
     * 每幀物理計算（核心物理引擎）
     *
     * 執行順序：
     * 1. 轉向平滑（Lerp）
     * 2. 角度 → X 方向速度分量（sin）
     * 3. 更新 X 位置（左右移動）
     *
     * 注意：Y 軸不移動 — 前進感由 Road 虛線滾動提供
     */
    public void update() {
        // ===== Step 1: 轉向平滑過渡（Lerp 線性內插） =====
        // steeringAngle 是「目標」角度（按按鈕時為 ±30°，放開時為 0°）
        // angle 是「當前」角度，每幀只移動 15% 的差距
        // 效果：按下按鈕後角度漸變，不會瞬間轉向
        angle += (steeringAngle - angle) * GameConfig.CAR_STEER_LERP;

        // ===== Step 2: 角度 → X 方向速度分量（三角函數） =====
        float radians = (float) Math.toRadians(angle);

        // sin(angle) = X 方向的分量比例
        //   angle=0°  → sin=0    → 直行時 X 不動（正確！）
        //   angle=30° → sin=0.5  → 一半速度往右
        //   angle=-30°→ sin=-0.5 → 一半速度往左
        float velocityX = speed * (float) Math.sin(radians);

        // ===== Step 3: 只更新 X 位置（左右移動） =====
        x += velocityX;
        // Y 軸不動 — 前進感由 Road 虛線滾動提供
    }

    /**
     * 繪製車輛（含旋轉）
     *
     * canvas.save/restore 機制：
     * - save() 儲存當前畫布矩陣（類似 git stash）
     * - rotate() 旋轉畫布座標系
     * - drawRect() 在旋轉後的座標系上繪製
     * - restore() 還原畫布矩陣（不影響其他物件繪製）
     */
    public void draw(Canvas canvas) {
        canvas.save();                                    // 儲存畫布狀態

        // 以車輛中心為軸心旋轉 angle 度
        // 軸心 = (x, y + height/2)，即車輛的幾何中心
        canvas.rotate(angle, x, y + height / 2f);

        // 繪製車輛矩形（以中心座標計算左上角）
        float left = x - width / 2f;                     // 左邊界 = 中心 - 半寬
        canvas.drawRect(left, y, left + width, y + height, paint);

        canvas.restore();                                 // 還原畫布狀態
    }

    // ===== 轉向控制（由 GameView 呼叫，GameView 由 MainActivity 按鈕觸發） =====

    /** 按下左按鈕 → 設定目標轉向角為負值（左轉） */
    public void moveLeft() {
        steeringAngle = -GameConfig.CAR_MAX_STEER_ANGLE;
    }

    /** 按下右按鈕 → 設定目標轉向角為正值（右轉） */
    public void moveRight() {
        steeringAngle = GameConfig.CAR_MAX_STEER_ANGLE;
    }

    /** 放開按鈕 → 方向盤回正（目標角度歸零） */
    public void stopSteering() {
        steeringAngle = 0f;
    }

    // ===== Getter/Setter（碰撞偵測和初始化用） =====

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    /** 設定 X 座標（碰撞偵測修正用） */
    public void setX(float x) { this.x = x; }

    /** 設定初始位置（由 GameView.onSizeChanged 呼叫） */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 取得碰撞框（AABB — 軸對齊包圍盒）
     * 未來可用 RectF.intersects(a, b) 判斷與障礙物碰撞
     */
    public RectF getBounds() {
        return new RectF(x - width / 2f, y, x + width / 2f, y + height);
    }
}

package com.example.firstapp;

/**
 * 遊戲常數集中管理（消除所有魔術數字）
 * 類比 Spring 的 application.properties — 所有設定值在這裡定義
 */
public final class GameConfig {

    // ===== 畫面更新 =====
    public static final int FRAME_DELAY_MS = 16;          // ≈60 FPS（每幀間隔 16 毫秒）

    // ===== 車輛尺寸 =====
    public static final float CAR_WIDTH = 100f;            // 車寬（像素）
    public static final float CAR_HEIGHT = 200f;           // 車高（像素）

    // ===== 車輛物理 =====
    public static final float CAR_SPEED = 5f;              // 每幀前進速度（像素/幀）
    public static final float CAR_STEER_LERP = 0.15f;      // 轉向平滑因子（0~1，越大越靈敏）
    public static final float CAR_MAX_STEER_ANGLE = 30f;   // 最大轉向角度（度）
    public static final float CAR_FRICTION = 0.98f;        // 摩擦力衰減係數（每幀角度乘以此值）

    // ===== 道路比例 =====
    public static final float ROAD_LEFT_RATIO = 0.2f;      // 道路左邊界 = 螢幕寬度 × 0.2
    public static final float ROAD_RIGHT_RATIO = 0.8f;     // 道路右邊界 = 螢幕寬度 × 0.8
    public static final int LANE_COUNT = 3;                 // 車道數量

    // ===== 虛線參數 =====
    public static final float DASH_LENGTH = 60f;           // 虛線段長度（像素）
    public static final float GAP_LENGTH = 40f;            // 虛線間隔長度（像素）
    public static final float LINE_STROKE_WIDTH = 8f;      // 分隔線寬度（像素）
    public static final float LINE_SCROLL_SPEED = 5f;      // 虛線每幀滾動速度（像素/幀）

    private GameConfig() {} // 防止實例化（工具類模式）
}

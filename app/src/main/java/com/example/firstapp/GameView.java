package com.example.firstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * 遊戲主畫面（純協調者 — 類比 Spring Controller）
 *
 * 重構前：167 行 God Object（畫筆/位置/車道/動畫/繪製全塞在一起）
 * 重構後：≈80 行，只做「接線」工作
 *
 * 職責：
 * 1. 持有 Car、Road、GameLoop 物件（依賴注入的 Field Injection）
 * 2. 實作 GameLoop.Callback → 每幀協調更新順序
 * 3. 轉發 moveLeft/moveRight 給 Car
 * 4. 管理生命週期（pause/resume）
 *
 * 執行流程：
 * XML Inflater → new GameView() → init()（建立物件，不啟動迴圈）
 * MainActivity.onResume() → gameView.resume() → gameLoop.start()
 *            → 每 16ms: onUpdate() → road.update + car.update + collision
 *            → VSYNC: onDraw() → road.draw + car.draw
 */
public class GameView extends View implements GameLoop.Callback {

    private GameLoop gameLoop;  // 遊戲迴圈管理器
    private Car car;            // 車輛實體
    private Road road;          // 道路實體

    // ===== 建構子（Android XML Inflater 會依序嘗試這三個） =====

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();  // XML 佈局 inflate 時呼叫這個建構子
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化：建立所有子物件（不啟動迴圈）
     *
     * 執行順序：
     * 1. new Road()     → 建立道路物件（畫筆初始化，幾何延遲到 onSizeChanged）
     * 2. new Car()      → 建立車輛物件（畫筆 + 物理狀態初始化）
     * 3. new GameLoop() → 建立遊戲迴圈（持有 this 的引用）
     *
     * 遊戲迴圈啟動時機：由 MainActivity.onResume() → gameView.resume() → gameLoop.start()
     */
    private void init() {
        road = new Road();
        car = new Car();
        gameLoop = new GameLoop(this, this); // 參數1=View, 參數2=Callback
        // 不在此處啟動迴圈 — 統一由 MainActivity.onResume() → resume() → start() 管理
    }

    /**
     * 螢幕尺寸確定後的回呼（Android Framework 呼叫）
     *
     * 時機：佈局計算完成後，此時 getWidth()/getHeight() 才有正確值
     * 執行順序：
     * 1. road.init() → 根據螢幕尺寸計算道路邊界 + 車道位置
     * 2. car.setPosition() → 車輛定位到道路中央車道、畫面下方
     */
    @Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        road.init(w, h);  // 計算道路幾何（需要螢幕寬高）

        // 車輛初始位置：中間車道中心、畫面下方 1/3 處
        float[] lanes = road.getLanes();
        car.setPosition(lanes[1], h * 2f / 3f);  // lanes[1] = 中間車道
    }

    /**
     * GameLoop.Callback 實作 — 每幀更新邏輯（每 16ms 執行一次）
     *
     * 更新順序很重要：
     * 1. road.update()  → 虛線動畫偏移
     * 2. car.update()   → 物理引擎計算（angle → sin → 左右移動，Y 軸固定）
     * 3. clampToRoad()  → 碰撞偵測（確保車輛在道路範圍內）
     */
    @Override
    public void onUpdate() {
        road.update();                              // 虛線動畫
        car.update();                               // 物理計算（左右轉向移動，Y 軸固定）
        CollisionDetector.clampToRoad(car, road);   // 邊界碰撞修正
    }

    /**
     * 繪製回呼（Android VSYNC 觸發後 Framework 呼叫）
     *
     * 繪製順序 = 圖層順序（後畫的覆蓋先畫的）：
     * 1. road.draw() → 綠色草地 → 灰色道路 → 白色虛線
     * 2. car.draw()  → 紅色車輛（含 canvas 旋轉）
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        road.draw(canvas);  // Layer 1-3: 背景 + 道路 + 虛線
        car.draw(canvas);   // Layer 4: 車輛（旋轉繪製）
    }

    // ===== 外部輸入轉發（MainActivity 按鈕 → GameView → Car） =====

    /** 左轉：設定車輛轉向角度為 -30° */
    public void moveLeft() {
        car.moveLeft();
    }

    /** 右轉：設定車輛轉向角度為 +30° */
    public void moveRight() {
        car.moveRight();
    }

    // ===== 生命週期管理（由 MainActivity 呼叫） =====

    /** Activity 不可見時暫停遊戲迴圈（省電、防止背景運算） */
    public void pause() {
        gameLoop.stop();
    }

    /** Activity 回到前景時恢復遊戲迴圈 */
    public void resume() {
        gameLoop.start();
    }
}

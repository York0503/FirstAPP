package com.example.firstapp;

import android.view.View;

/**
 * 遊戲迴圈管理（start/stop + Callback 回呼介面）
 *
 * 類比後端：
 * - start()          = 啟動 ScheduledExecutorService
 * - stop()           = executorService.shutdown()
 * - isRunning        = AtomicBoolean 控制旗標
 * - removeCallbacks  = 從 MessageQueue 移除已排入但尚未執行的任務
 *
 * 運作原理：
 * Android UI 執行緒有一個 MessageQueue（消息佇列），
 * postDelayed(runnable, 16) 就是把 runnable 排入 16ms 後執行。
 * runnable 執行完後再 postDelayed 自己 → 形成遞迴排程（Game Loop）。
 */
public class GameLoop {

    /**
     * 回呼介面：每幀通知 GameView 執行更新邏輯
     * 類比 Spring 的 @EventListener — 事件驅動模式
     */
    public interface Callback {
        void onUpdate();  // 每幀回呼（由 GameView 實作）
    }

    private final View view;          // 持有 View 引用（用於 invalidate + postDelayed）
    private final Callback callback;  // 回呼對象（GameView）
    private boolean isRunning = false; // 運行狀態旗標

    /**
     * 遞迴排程的 Runnable
     *
     * 執行流程（每 16ms 一次）：
     * 1. 檢查 isRunning → 若為 false 則中斷遞迴
     * 2. callback.onUpdate() → 呼叫 GameView 的更新邏輯
     * 3. view.invalidate() → 標記需要重繪（觸發 onDraw）
     * 4. view.postDelayed(this, 16) → 16ms 後再次排入 MessageQueue
     */
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;                              // 暫停檢查（中斷遞迴鏈）
            callback.onUpdate();                                  // 通知 GameView 執行遊戲邏輯
            view.invalidate();                                    // 標記髒區域 → 排入重繪請求
            view.postDelayed(this, GameConfig.FRAME_DELAY_MS);   // 16ms 後再次執行（遞迴排程）
        }
    };

    /**
     * 建構子
     * @param view     持有的 View（用於排程和重繪）
     * @param callback 每幀回呼對象
     */
    public GameLoop(View view, Callback callback) {
        this.view = view;
        this.callback = callback;
    }

    /**
     * 啟動遊戲迴圈
     * view.post(runnable) 會在下一次 UI 執行緒空閒時執行 runnable
     */
    public void start() {
        if (isRunning) return;  // 防重複啟動：避免多個 Runnable 同時遞迴排程
        isRunning = true;
        view.post(runnable);  // 排入 MessageQueue（立即排程）
    }

    /**
     * 停止遊戲迴圈
     * removeCallbacks 會從 MessageQueue 移除已排入但尚未執行的 runnable
     * 加上 isRunning=false，即使已經在執行中的 runnable 也會在下一次檢查時中斷
     */
    public void stop() {
        isRunning = false;
        view.removeCallbacks(runnable);  // 移除待執行的排程
    }
}

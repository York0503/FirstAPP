package com.example.firstapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * 主 Activity — 負責 UI 綁定 + 生命週期管理
 *
 * 職責：
 * 1. 綁定 XML 佈局中的 View（GameView、左右按鈕）
 * 2. 設定按鈕點擊事件 → 轉發給 GameView
 * 3. 管理 Activity 生命週期 → 通知 GameView 暫停/恢復
 *
 * 輸入流程：
 * 用戶按按鈕 → onClick → gameView.moveLeft/moveRight()
 *           → car.moveLeft/moveRight()
 *           → 設定 car.steeringAngle = ±30°
 *           → 下一幀 car.update() 中 angle Lerp 向目標角度
 */
public class MainActivity extends AppCompatActivity {

    private GameView gameView;  // 遊戲畫面（協調者角色）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 處理系統列（狀態列 + 導航列）的 padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                }
        );

        // 初始化視圖：從 XML 佈局中取得 View 引用
        gameView = findViewById(R.id.gameView);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);

        // 設置按鈕點擊事件：按鈕 → GameView → Car → 設定轉向角度
        btnLeft.setOnClickListener(v -> gameView.moveLeft());
        btnRight.setOnClickListener(v -> gameView.moveRight());
    }

    /**
     * Activity 生命週期：暫停
     * 當用戶切到其他 App 或按 Home 鍵時觸發
     * → 停止遊戲迴圈（省電、防止背景運算）
     */
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();   // 通知 GameView 停止遊戲迴圈
    }

    /**
     * Activity 生命週期：恢復
     * 當用戶從背景切回此 App 時觸發
     * → 恢復遊戲迴圈
     */
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();  // 通知 GameView 恢復遊戲迴圈
    }
}

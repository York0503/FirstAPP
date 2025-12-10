
# Homework Note

> **目標**：實作一個簡單的移動的車子和道路

---

## 📚 前置知識：Android 繪圖系統

### Android 與後端技術對照表

| Android 概念 | C# WPF | Python | 用途 |
|-------------|--------|--------|------|
| `View` | `UserControl` | `tkinter.Canvas` | 可繪製的 UI 元件 |
| `Canvas` | `DrawingContext` | `canvas` | 畫布，所有圖形繪製的載體 |
| `Paint` | `Brush/Pen` | `pen` | 畫筆，定義顏色、線條粗細 |
| `onDraw()` | `OnRender()` | `draw()` | 繪圖函數，每幀被調用 |
| `Runnable` | `DispatcherTimer` | `while True` 遊戲循環 | 週期性執行邏輯 |
| `invalidate()` | `InvalidateVisual()` | `update()` | 強制重繪 UI |

---

### 核心概念：遊戲循環

**Android 實作**：
```java
private final Runnable updateRunnable = new Runnable() {
    @Override
    public void run() {
        update();              // 1️⃣ 更新遊戲邏輯（位置、碰撞）
        invalidate();          // 2️⃣ 通知系統重繪
        postDelayed(this, 16); // 3️⃣ 16ms 後再執行（≈ 60 FPS）
    }
};
```

**等價的 Python 遊戲循環**：
```python
while True:
    update()           # 更新邏輯
    render()           # 重繪畫面
    time.sleep(0.016)  # 16ms ≈ 60 FPS
```

**等價的 C# WPF**：
```csharp
var timer = new DispatcherTimer { Interval = TimeSpan.FromMilliseconds(16) };
timer.Tick += (s, e) => {
    Update();
    InvalidateVisual();
};
timer.Start();
```

---

### Android 座標系統

```
(0, 0) ────────────► X
  │
  │
  ▼
  Y

原點在左上角
Y 軸向下增長（與數學座標系相反）
```

---

## 📋 完整任務清單

### Option 2: Make the Car Go（讓車子移動）
1. ✅ **Task 1**: 在 `init()` 初始化車子位置 (carX, carY)
2. ✅ **Task 2**: 在 `update()` 實作向下移動邏輯
3. ✅ **Task 3**: 處理邊界循環（車子移出螢幕後回到頂部）

### Option 1: Road（繪製道路）
4. ✅ **Task 4**: 新增道路相關變數（roadPaint, linePaint, lineOffset）
5. ✅ **Task 5**: 在 `init()` 初始化道路畫筆
6. ✅ **Task 6**: 在 `onDraw()` 繪製背景（草地）
7. ✅ **Task 7**: 在 `onDraw()` 繪製道路主體（灰色矩形）
8. ✅ **Task 8**: 在 `onDraw()` 繪製道路中線（黃色虛線）
9. ✅ **Task 9**: 在 `update()` 加入中線動畫效果
10. ✅ **Task 10**: 調整車子位置使其在道路中央

---

## 🚗 Option 2: 讓車子移動

---

### Task 1: 初始化車子位置

#### 🎯 目的
設定車子的起始座標，否則車子會在 (0, 0) 位置（左上角）

#### 📍 要修改的位置
`GameView.java` 的 `init()` 函數

#### 📝 原始程式碼
```java
private void init() {
    carPaint = new Paint();
    carPaint.setColor(Color.RED);

    post(updateRunnable);
}
```

#### ✅ 修改後
```java
private void init() {
    carPaint = new Paint();
    carPaint.setColor(Color.RED);

    // 初始化車子位置
    carX = 300f;  // 水平位置（靠近螢幕中央偏左）
    carY = 100f;  // 垂直位置（靠近螢幕頂部）

    post(updateRunnable);
}
```

#### 💡 概念解釋

**carX 和 carY 的意義**：
- `carX = 300f`：設定水平位置（X 座標）
    - `f` 表示 `float` 型別（浮點數）
    - 300 像素約在一般手機螢幕的中央偏左位置

- `carY = 100f`：設定垂直位置（Y 座標）
    - Android 的 Y 軸**向下增長**（與數學座標相反）
    - 100 像素表示距離螢幕頂部 100px

---

### Task 2: 實作向下移動邏輯

#### 🎯 目的
讓車子每幀向下移動一點，產生動畫效果

#### 📍 要修改的位置
`GameView.java` 的 `update()` 函數

#### 📝 原始程式碼
```java
private void update() {
    // 空的
}
```

#### ✅ 修改後
```java
private void update() {
    // 每次更新讓車子向下移動 5 像素
    carY += 5f;
}
```

#### 💡 概念解釋

**移動邏輯**：
- `carY += 5f`：等同於 `carY = carY + 5`
- 每次 `update()` 被調用（每 16ms），carY 增加 5
- 因為 Y 軸向下，所以 carY 增加 = 車子向下移動

**速度計算**：
```
每幀移動 5px × 60 FPS = 300px/秒
```

#### 📊 執行流程

```
第 0 幀: carY = 100
第 1 幀: carY = 105 (100 + 5)
第 2 幀: carY = 110 (105 + 5)
第 3 幀: carY = 115 (110 + 5)
...
第 N 幀: carY = 100 + (5 × N)
```

---

### Task 3: 處理邊界循環

#### 🎯 目的
當車子移出螢幕下方時，讓它回到頂部，產生無限循環效果

#### 📍 要修改的位置
`GameView.java` 的 `update()` 函數

#### ✅ 修改後
```java
private void update() {
    // 每次更新讓車子向下移動 5 像素
    carY += 5f;

    // 如果車子移出螢幕下方，讓它回到頂部
    if (carY > getHeight()) {
        carY = -carHeight;
    }
}
```

#### 💡 概念解釋

**關鍵方法與變數**：
- `getHeight()`：取得 View 的高度（螢幕可見區域的高度）
    - 類似 C# 的 `ActualHeight`
    - 類似 Python tkinter 的 `winfo_height()`

- `carY > getHeight()`：車子的頂部超過螢幕底部
    - 此時車子已經完全移出螢幕

- `carY = -carHeight`：重置到螢幕上方
    - `carHeight = 200f`（車子高度）
    - `-200` 表示車子頂部在螢幕上方 200px 的位置
    - 這樣車子會完全消失後才從頂部重新出現

#### 📊 視覺效果

```
螢幕頂部 (Y=0)
     ↓
   [🚗] carY = 100
     ↓
     ↓ (車子向下移動)
     ↓
   [🚗] carY = 800
     ↓
螢幕底部 (Y=1000)
     ↓
   [🚗] carY = 1200 (超出螢幕)
     ↓
   重置 carY = -200
     ↓
螢幕頂部 (Y=0)
   [🚗] carY = -200 (在螢幕上方)
     ↓ (繼續向下移動)
   [🚗] carY = 0 (開始進入螢幕)
```

---

## 🛣️ Option 1: 繪製道路

---

### Task 4: 新增道路相關變數

#### 🎯 目的
準備繪製道路所需的畫筆和狀態變數

#### 📍 要修改的位置
`GameView.java` 的類別變數區域（頂部）

#### 📝 原始程式碼
```java
public class GameView extends View {

    private Paint carPaint;

    private float carX, carY;
    private final float carWidth = 120f, carHeight = 200f;
```

#### ✅ 修改後
```java
public class GameView extends View {

    private Paint carPaint;       // 車子畫筆
    private Paint roadPaint;      // 道路畫筆
    private Paint linePaint;      // 道路中線畫筆

    private float carX, carY;
    private final float carWidth = 120f, carHeight = 200f;
    private float lineOffset = 0f;  // 中線動畫偏移量
```

#### 💡 概念解釋

**Paint 是什麼？**
- `Paint` 是 Android 的「畫筆」類別
- 用於設定繪圖的**顏色、線條粗細、樣式**等
- **Paint ≠ 渲染**，Paint = 繪圖屬性配置

**類比**：
```
Paint = 調色盤/畫筆設定（配置）
Canvas.draw() = 實際刷牆（執行）
```

**變數說明**：

1. **`Paint roadPaint`**：
    - 用於繪製道路主體（深灰色矩形）

2. **`Paint linePaint`**：
    - 用於繪製道路中線（黃色虛線）
    - 與 `roadPaint` 分開，因為顏色和樣式不同

3. **`float lineOffset = 0f`**：
    - 用於製作中線的**動畫效果**
    - 這個值會不斷增加，讓虛線「向下流動」
    - 類似遊戲中的「捲軸偏移量」

---

### Task 5: 初始化道路畫筆

#### 🎯 目的
設定道路和中線的顏色、粗細等屬性

#### 📍 要修改的位置
`GameView.java` 的 `init()` 函數

#### ✅ 修改後
```java
private void init() {
    // 車子畫筆
    carPaint = new Paint();
    carPaint.setColor(Color.RED);

    // 道路畫筆（深灰色）
    roadPaint = new Paint();
    roadPaint.setColor(Color.rgb(80, 80, 80));

    // 中線畫筆（黃色虛線）
    linePaint = new Paint();
    linePaint.setColor(Color.YELLOW);
    linePaint.setStrokeWidth(10f);  // 線條粗細 10px

    // 車子初始位置
    carX = 300f;
    carY = 100f;

    post(updateRunnable);
}
```

#### 💡 概念解釋

**顏色設定**：

1. **`Color.rgb(80, 80, 80)`**：
    - RGB 顏色（紅、綠、藍各 0-255）
    - `(80, 80, 80)` = 深灰色
    - 類似 CSS 的 `rgb(80, 80, 80)`

2. **`linePaint.setStrokeWidth(10f)`**：
    - 設定**線條粗細**為 10 像素
    - 只對線條有效（`drawLine`, `drawCircle` 等）
    - 類似 CSS 的 `stroke-width: 10px`

#### 📊 顏色對照表

```
Color.RED       = rgb(255, 0, 0)     紅色
Color.YELLOW    = rgb(255, 255, 0)   黃色
Color.GRAY      = rgb(128, 128, 128) 灰色
Color.rgb(80, 80, 80)                深灰色
Color.rgb(34, 139, 34)               草地綠
```

---

### Task 6: 繪製背景（草地）

#### 🎯 目的
將背景從預設的深灰色改為綠色草地，讓道路更明顯

#### 📍 要修改的位置
`GameView.java` 的 `onDraw()` 函數

#### 📝 原始程式碼
```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawColor(Color.rgb(50, 50, 50));  // 深灰色
```

#### ✅ 修改後
```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // 背景（草地綠）
    canvas.drawColor(Color.GREEN);
```

#### 💡 概念解釋

- `canvas.drawColor()`：填充**整個畫布**為指定顏色
- `Color.GREEN` = `rgb(0, 255, 0)` 草地綠色
- 這個函數會**覆蓋整個螢幕**，所以要**第一個調用**

---

### Task 7: 繪製道路主體

#### 🎯 目的
在螢幕中央繪製一條垂直的灰色矩形作為道路

#### 📍 要修改的位置
`GameView.java` 的 `onDraw()` 函數（在繪製背景之後）

#### ✅ 新增程式碼
```java
// 道路主體（深灰色矩形）
float roadLeft = getWidth() * 0.2f;   // 道路左邊界（20% 位置）
float roadRight = getWidth() * 0.8f;  // 道路右邊界（80% 位置）

canvas.drawRect(
    roadLeft,       // 左上角 X 座標
    0,              // 左上角 Y 座標
    roadRight,      // 右下角 X 座標
    getHeight(),    // 右下角 Y 座標
    roadPaint       // 使用道路畫筆
);
```

#### 💡 概念解釋

**drawRect() 參數說明**：
```java
canvas.drawRect(
    left,    // 矩形左邊 X 座標
    top,     // 矩形頂部 Y 座標
    right,   // 矩形右邊 X 座標
    bottom,  // 矩形底部 Y 座標
    paint    // 使用哪個畫筆
);
```

**響應式設計**（為什麼用百分比）：

| 計算方式 | 小手機 (720px) | 平板 (1200px) | 折疊螢幕 (2000px) |
|---------|---------------|---------------|------------------|
| `getWidth() * 0.2` | 144px | 240px | 400px |
| `getWidth() * 0.8` | 576px | 960px | 1600px |
| **道路寬度** | **432px (60%)** | **720px (60%)** | **1200px (60%)** |

**視覺示意**：
```
螢幕寬度 (假設 1000px)
0px ──────────────────────── 1000px
│                              │
│  🌿🌿 (200, 0)────────(800, 0)  🌿🌿
│  🌿🌿  │              │  🌿🌿
│  🌿🌿  │   道路區域   │  🌿🌿
│  🌿🌿  │  (灰色)      │  🌿🌿
│  🌿🌿  │              │  🌿🌿
│  🌿🌿 (200, H)───────(800, H)  🌿🌿
│                              │

roadLeft = 1000 × 0.2 = 200px
roadRight = 1000 × 0.8 = 800px
道路寬度 = 800 - 200 = 600px
```

---

### Task 8: 繪製道路中線（黃色虛線）

#### 🎯 目的
在道路中央繪製黃色虛線，模擬真實道路

#### 📍 要修改的位置
`GameView.java` 的 `onDraw()` 函數（在繪製道路之後，繪製車子之前）

#### ✅ 新增程式碼
```java
// 道路中線（黃色虛線）
float centerX = (roadLeft + roadRight) / 2;  // 計算道路中心點
float dashLength = 60f;   // 每段虛線的長度
float gapLength = 40f;    // 虛線之間的間隔

for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength) {
    canvas.drawLine(
        centerX, y,                   // 起點 (x, y)
        centerX, y + dashLength,      // 終點 (x, y)
        linePaint                     // 使用黃色畫筆
    );
}
```

#### 💡 概念解釋

**1. 計算道路中心**：
```java
float centerX = (roadLeft + roadRight) / 2;
// 如果 roadLeft = 200, roadRight = 800
// centerX = (200 + 800) / 2 = 500
```

**2. for 迴圈繪製虛線**：
```java
for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength)
```
- `y = lineOffset`：從偏移量開始（用於動畫）
- `y < getHeight()`：繪製到螢幕底部
- `y += dashLength + gapLength`：每次跳過一段虛線 + 間隔（100px）

**視覺示意**：
```
道路中心 (X = 500)
        │
        │ ← dashLength (60px 虛線)
        │
       gap (40px 空白)
        │
        │ ← dashLength (60px 虛線)
        │
       gap (40px 空白)
        │
        │
        ⋮
```

#### 📊 迴圈執行過程

假設螢幕高度 1000px，lineOffset = 0：

```
第 1 次: y = 0,   繪製 (500, 0) 到 (500, 60)
第 2 次: y = 100, 繪製 (500, 100) 到 (500, 160)
第 3 次: y = 200, 繪製 (500, 200) 到 (500, 260)
第 4 次: y = 300, 繪製 (500, 300) 到 (500, 360)
...
第 10 次: y = 900, 繪製 (500, 900) 到 (500, 960)
```

---

### Task 9: 加入中線動畫效果

#### 🎯 目的
讓虛線向下移動，產生「車子在前進」的視覺錯覺

#### 📍 要修改的位置
`GameView.java` 的 `update()` 函數

#### ✅ 修改後
```java
private void update() {
    // 車子向下移動
    carY += 5f;
    if (carY > getHeight()) {
        carY = -carHeight;
    }

    // 中線向下移動（製造車子在開的錯覺）
    lineOffset += 5f;
    if (lineOffset > 100f) {  // 100 = dashLength + gapLength
        lineOffset = 0f;
    }
}
```

#### 💡 概念解釋

**動畫原理**：

1. **`lineOffset += 5f`**：
    - 每幀讓偏移量增加 5px
    - 這會讓虛線的起始點向下移動

2. **為什麼重置條件是 100？**：
```
完整週期 = dashLength + gapLength
         = 60 + 40
         = 100px
```
- 當偏移量超過一個完整週期，視覺上看起來和 0 一樣
- 所以重置為 0，避免數值無限增長

#### 📊 動畫過程

```
幀 0:  lineOffset = 0
       │ ← 虛線從這裡開始
       │
       gap

幀 1:  lineOffset = 5
          │ ← 虛線從這裡開始
          │
          gap

幀 2:  lineOffset = 10
             │ ← 虛線從這裡開始
             │
             gap

...視覺上虛線向下移動

幀 20: lineOffset = 100 → 重置為 0（視覺上無差異）
```

**類似概念**：
- 遊戲中的「捲軸效果」
- 影片的「時間軸偏移」
- CSS `background-position` 動畫

---

### Task 10: 調整車子位置使其在道路中央

#### 🎯 目的
確保車子在道路正中間，不會偏左或偏右

#### 📍 要修改的位置
`GameView.java` 的 `onDraw()` 函數（動態計算）

#### ✅ 在 onDraw() 加入以下程式碼

在繪製車子之前加入：

```java
// 計算道路中心並讓車子置中
float roadCenter = (roadLeft + roadRight) / 2;
carX = roadCenter - (carWidth / 2);
```

完整的 onDraw()：

```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawColor(Color.GREEN);

    float roadLeft = getWidth() * 0.2f;
    float roadRight = getWidth() * 0.8f;

    // 繪製道路
    canvas.drawRect(roadLeft, 0, roadRight, getHeight(), roadPaint);

    // 繪製中線
    float centerX = (roadLeft + roadRight) / 2;
    float dashLength = 60f;
    float gapLength = 40f;

    for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength) {
        canvas.drawLine(centerX, y, centerX, y + dashLength, linePaint);
    }

    // ⬇️⬇️⬇️ 車子置中邏輯 ⬇️⬇️⬇️
    float roadCenter = (roadLeft + roadRight) / 2;
    carX = roadCenter - (carWidth / 2);
    // ⬆️⬆️⬆️ 車子置中邏輯 ⬆️⬆️⬆️

    // 繪製車子
    canvas.drawRect(carX, carY, carX + carWidth, carY + carHeight, carPaint);
}
```

#### 💡 概念解釋

**車子置中計算**：
```
道路左邊界 = 200px
道路右邊界 = 800px
道路中心 = (200 + 800) / 2 = 500px

車子寬度 = 120px
車子左上角 X 座標 = 道路中心 - (車子寬度 / 2)
                = 500 - 60
                = 440px
```

**視覺示意**：
```
道路範圍: 200px ~ 800px
道路中心: 500px

    200         440  500  560         800
     │           │    │    │           │
     ├───────────┤────┼────┤───────────┤
     │           [🚗🚗🚗]           │
     │           │←60→│←60→│           │
     │           │ 車子 │           │
```

**為什麼要 `- (carWidth / 2)`？**
- `carX` 是車子的**左上角**座標
- 要讓車子中心對齊道路中心，需要向左偏移半個車寬

---

## 📄 完整程式碼

### GameView.java

```java
package com.example.firstapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameView extends View {

    private Paint carPaint;       // 車子畫筆
    private Paint roadPaint;      // 道路畫筆
    private Paint linePaint;      // 道路中線畫筆

    private float carX, carY;     // 車子的位置
    private final float carWidth = 120f, carHeight = 200f;  // 車子尺寸
    private float lineOffset = 0f;  // 中線動畫偏移量

    // 遊戲循環（每 16ms 執行一次）
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            update();              // 更新邏輯
            invalidate();          // 觸發重繪
            postDelayed(this, 16); // 16ms 後再執行
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
        // 車子畫筆（紅色）
        carPaint = new Paint();
        carPaint.setColor(Color.RED);

        // 道路畫筆（深灰色）
        roadPaint = new Paint();
        roadPaint.setColor(Color.rgb(80, 80, 80));

        // 中線畫筆（黃色，粗細 10px）
        linePaint = new Paint();
        linePaint.setColor(Color.YELLOW);
        linePaint.setStrokeWidth(10f);

        // 車子初始位置
        carX = 300f;
        carY = 100f;

        // 啟動遊戲循環
        post(updateRunnable);
    }

    private void update() {
        // 車子向下移動
        carY += 5f;
        if (carY > getHeight()) {
            carY = -carHeight;  // 回到頂部
        }

        // 中線向下移動（動畫效果）
        lineOffset += 5f;
        if (lineOffset > 100f) {  // 100 = dashLength + gapLength
            lineOffset = 0f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1️⃣ 繪製背景（草地綠）
        canvas.drawColor(Color.GREEN);

        // 2️⃣ 計算道路邊界（響應式）
        float roadLeft = getWidth() * 0.2f;   // 左邊界（20%）
        float roadRight = getWidth() * 0.8f;  // 右邊界（80%）

        // 3️⃣ 繪製道路（灰色矩形）
        canvas.drawRect(roadLeft, 0, roadRight, getHeight(), roadPaint);

        // 4️⃣ 繪製道路中線（黃色虛線）
        float centerX = (roadLeft + roadRight) / 2;  // 道路中心
        float dashLength = 60f;   // 虛線長度
        float gapLength = 40f;    // 間隔

        for (float y = lineOffset; y < getHeight(); y += dashLength + gapLength) {
            canvas.drawLine(
                centerX, y,
                centerX, y + dashLength,
                linePaint
            );
        }

        // 5️⃣ 調整車子位置（置中）
        float roadCenter = (roadLeft + roadRight) / 2;
        carX = roadCenter - (carWidth / 2);

        // 6️⃣ 繪製車子（紅色矩形）
        canvas.drawRect(
            carX,
            carY,
            carX + carWidth,
            carY + carHeight,
            carPaint
        );
    }
}
```

---

## 🎨 繪製順序總結

**繪製順序很重要**（後繪製的會覆蓋先繪製的）：

```
1. 背景（綠色）         ← 最底層
2. 道路（灰色）         ← 第二層
3. 中線（黃色虛線）     ← 第三層
4. 車子（紅色）         ← 最上層
```

**視覺效果**：
```
┌─────────────────────────────┐
│ 🌿🌿🌿🌿🌿🌿🌿🌿🌿🌿🌿🌿     背景
│ 🌿🌿 ┌─────────┐ 🌿🌿
│ 🌿🌿 │    │    │ 🌿🌿     道路 + 中線
│ 🌿🌿 │  🚗│    │ 🌿🌿     車子（最上層）
│ 🌿🌿 │    │    │ 🌿🌿
│ 🌿🌿 │    │    │ 🌿🌿
│ 🌿🌿 └─────────┘ 🌿🌿
│ 🌿🌿🌿🌿🌿🌿🌿🌿🌿🌿🌿🌿
└─────────────────────────────┘
```

---

## 🎯 執行流程總結

### 初始化階段（`init()`，執行一次）
1. 創建畫筆（carPaint, roadPaint, linePaint）
2. 設定畫筆屬性（顏色、粗細）
3. 設定車子初始位置（carX, carY）
4. 啟動遊戲循環（`post(updateRunnable)`）

### 遊戲循環（每 16ms 執行一次）
1. `update()`：更新車子和中線位置
2. `invalidate()`：通知系統需要重繪
3. `onDraw()`：繪製所有圖形（背景→道路→中線→車子）
4. `postDelayed(this, 16)`：16ms 後重複

---

## 🐛 常見問題與故障排除

### Q1: 車子不會動

**可能原因**：
- `update()` 函數是空的
- 忘記調用 `post(updateRunnable)`

**解決方案**：
```java
private void update() {
    carY += 5f;  // 確保有這行
}

private void init() {
    // ...
    post(updateRunnable);  // 確保有這行
}
```

---

### Q2: 畫面一片空白

**可能原因**：
- 忘記初始化 Paint
- `onDraw()` 沒有繪製任何東西

**解決方案**：
```java
private void init() {
    carPaint = new Paint();  // 確保有創建
    carPaint.setColor(Color.RED);
}
```

---

### Q3: 車子位置不正確

**可能原因**：
- 忘記設定初始位置
- carX, carY 沒有賦值

**解決方案**：
```java
private void init() {
    carX = 300f;  // 確保有設定
    carY = 100f;
}
```

---

### Q4: 中線不會動

**可能原因**：
- `update()` 中沒有更新 `lineOffset`

**解決方案**：
```java
private void update() {
    lineOffset += 5f;
    if (lineOffset > 100f) {
        lineOffset = 0f;
    }
}
```

---

### Q5: App 閃退或卡頓

**可能原因**：
- `getWidth()` 或 `getHeight()` 在 View 未初始化時返回 0
- 除以 0 錯誤

**解決方案**：
```java
@Override
protected void onDraw(Canvas canvas) {
    // 確保 View 已初始化
    if (getWidth() == 0 || getHeight() == 0) {
        return;
    }

    // 繼續繪製...
}
```

---

## 🎓 進階挑戰

完成基本任務後，可以嘗試：

1. **加入障礙物**：在道路上隨機生成障礙物
2. **碰撞檢測**：車子碰到障礙物時遊戲結束
3. **觸控控制**：用手指左右滑動控制車子
4. **計分系統**：根據行駛距離計分
5. **音效**：加入引擎聲和碰撞音效
6. **更精緻的圖形**：用圖片取代矩形

---

## 📚 參考資源

- [Android Canvas 官方文件](https://developer.android.com/reference/android/graphics/Canvas)
- [Android Paint 官方文件](https://developer.android.com/reference/android/graphics/Paint)
- [Android Custom View 教學](https://developer.android.com/develop/ui/views/layout/custom-views/create-view)

---

**完成所有 Task 後，你將會擁有一個完整的 2D 遊戲基礎！** 🎉
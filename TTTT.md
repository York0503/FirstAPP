# MainActivity.java 完整解析指南

> Documentation:Android 應用程式主活動類別完整教學
> Author:York Lai
> Last Updated:2025/12/08

<details>
<summary>CHANGELOG</summary>

> 建立文件
> [time=Sun, Dec 8, 2025 11:30 AM][name=阿諆]

</details>

---
## 📖 目錄

1. [整體架構](##整體架構)
2. [逐行詳細解析](##逐行詳細解析)
3. [完整生命週期流程](##完整生命週期流程)
4. [視窗內邊距系統](##視窗內邊距系統)
5. [關鍵概念總結](##關鍵概念總結)

---

## 整體架構

### 檔案結構樹狀圖

```
MainActivity.java
│
├── 📦 Package 宣告
│   └── com.example.firstapp
│
├── 📥 Import 匯入
│   ├── Android 核心類別
│   ├── AndroidX 支援庫
│   └── 視窗內邊距相關類別
│
└── 🏗️ MainActivity 類別
    │
    ├── 📌 繼承 AppCompatActivity
    │
    └── 🎯 onCreate() 方法
        ├── 1️⃣ 呼叫父類別初始化
        ├── 2️⃣ 設定內容視圖
        └── 3️⃣ 處理視窗內邊距
            ├── 監聽內邊距變化
            ├── 取得系統列尺寸
            └── 設定 Padding
```

### 完整程式碼

```java
package com.example.firstapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
```
 ---
### Activity 生命週期流程

```
App 啟動
    ↓
系統建立 MainActivity 物件
    ↓
呼叫 onCreate(savedInstanceState)
    ├── savedInstanceState = null (首次啟動)
    └── savedInstanceState ≠ null (重新建立,如螢幕旋轉)
    ↓
super.onCreate(savedInstanceState)
    └── 執行基礎初始化
    ↓
setContentView(R.layout.activity_main)
    ├── 載入 activity_main.xml
    ├── 建立所有 View 物件
    └── 建構 View 樹狀結構
    ↓
設定視窗內邊距監聽器
    ├── 尋找 id="main" 的根視圖
    ├── 註冊內邊距變化監聽器
    └── 當系統列變化時自動調整
    ↓
onStart() → onResume()
    ↓
App 顯示於螢幕上 ✅
```
---

## 逐行詳細解析

### 📦 第 1 行:Package 宣告

```java
package com.example.firstapp;
```

| 元素 | 說明 |
|------|------|
| `package` | Java 套件關鍵字 |
| `com.example.firstapp` | 應用程式的命名空間 |

> **作用**:定義此類別所屬的套件,確保類別名稱的唯一性

#### 💡 Package 命名規則

```
com.公司名稱.專案名稱
 │      │        │
 │      │        └── 專案識別
 │      └─────────── 組織識別
 └────────────────── 網域反向
```

#### 實際範例

| 應用程式 | Package 名稱 |
|----------|-------------|
| Google Maps | `com.google.android.apps.maps` |
| Facebook | `com.facebook.katana` |
| LINE | `jp.naver.line.android` |
| 本專案 | `com.example.firstapp` |

---

### 📥 第 3-8 行:Import 匯入

```java
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
```

#### Import 詳細說明

| 匯入類別 | 所屬套件 | 作用 |
|---------|---------|------|
| `Bundle` | `android.os` | 儲存/恢復狀態資料 |
| `EdgeToEdge` | `androidx.activity` | 全螢幕顯示工具 |
| `AppCompatActivity` | `androidx.appcompat.app` | 向下相容的 Activity 基礎類別 |
| `Insets` | `androidx.core.graphics` | 視窗內邊距資料類別 |
| `ViewCompat` | `androidx.core.view` | View 相容性工具 |
| `WindowInsetsCompat` | `androidx.core.view` | 視窗內邊距相容性包裝 |

#### 套件來源分類

```
┌─────────────────────────────────┐
│  android.*                      │ ← Android SDK 核心
│  - android.os.Bundle            │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│  androidx.*                     │ ← AndroidX 支援庫
│  - androidx.appcompat.*         │   (向下相容)
│  - androidx.core.*              │
│  - androidx.activity.*          │
└─────────────────────────────────┘
```

> ⚠️ **重要**:`androidx.*` 是 Android 支援庫的新版本,取代舊的 `android.support.*`

---

### 🏗️ 第 10 行:類別定義

```java
public class MainActivity extends AppCompatActivity {
```

#### 語法結構

| 元素 | 說明 |
|------|------|
| `public` | 公開的,任何地方都可存取 |
| `class` | 類別定義關鍵字 |
| `MainActivity` | 類別名稱(必須與檔案名稱相同) |
| `extends` | 繼承關鍵字 |
| `AppCompatActivity` | 父類別 |

#### 繼承鏈結構

```
java.lang.Object
    └── android.content.Context
        └── android.content.ContextWrapper
            └── android.app.Activity
                └── androidx.fragment.app.FragmentActivity
                    └── androidx.appcompat.app.AppCompatActivity ← 我們繼承這個
                        └── MainActivity (本類別)
```

#### ❓ 為什麼繼承 AppCompatActivity 而非 Activity?

| 原因 | 說明 |
|------|------|
| 🔄 **向下相容** | 新功能可在舊版 Android 上運作 |
| 🎨 **Material Design** | 內建 Material 主題支援 |
| 🛠️ **ActionBar/Toolbar** | 提供現代化的標題列 |
| 📱 **Fragment 支援** | 完整的 Fragment 管理功能 |

---

### 🎯 第 12-13 行:onCreate 方法宣告

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
```

#### `@Override` 註解

> **作用**:明確標示此方法覆寫父類別的方法

```java
@Override  ← 編譯器會檢查
protected void onCreate(Bundle savedInstanceState) {
    // 如果父類別沒有此方法,會編譯錯誤 ❌
}
```

#### 方法簽章說明

| 元素 | 說明 |
|------|------|
| `protected` | 保護的,子類別可存取 |
| `void` | 無回傳值 |
| `onCreate` | 方法名稱(生命週期方法) |
| `Bundle savedInstanceState` | 參數:儲存的狀態資料 |

#### Bundle savedInstanceState 的用途

```
首次啟動 App:
    savedInstanceState = null
    └── 正常初始化

螢幕旋轉/記憶體不足後重建:
    savedInstanceState ≠ null
    └── 包含之前儲存的資料
        ├── 文字輸入內容
        ├── 捲動位置
        └── 自訂狀態
```

#### 📊 savedInstanceState 範例

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null) {
        // 恢復之前的狀態
        String text = savedInstanceState.getString("user_input");
        int score = savedInstanceState.getInt("game_score");
    }
}

@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    // 儲存狀態
    outState.putString("user_input", editText.getText().toString());
    outState.putInt("game_score", currentScore);
}
```

---

### 🔧 第 14 行:呼叫父類別初始化

```java
super.onCreate(savedInstanceState);
```

#### 執行順序

```
MainActivity.onCreate()
    ↓
super.onCreate(savedInstanceState)
    ↓
AppCompatActivity.onCreate()
    ├── 初始化主題
    ├── 設定視窗屬性
    └── 準備 Fragment 管理器
    ↓
回到 MainActivity.onCreate()
    └── 繼續執行後續程式碼
```

> ⚠️ **重要**:`super.onCreate()` 必須在 `onCreate()` 的第一行執行,否則會發生錯誤!

#### ❌ 錯誤範例

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    setContentView(R.layout.activity_main);  // ❌ 錯誤!
    super.onCreate(savedInstanceState);       // ❌ 太晚呼叫!
}
```

#### ✅ 正確範例

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);       // ✅ 第一行
    setContentView(R.layout.activity_main);  // ✅ 正確!
}
```

---

### 🖼️ 第 16 行:設定內容視圖

```java
setContentView(R.layout.activity_main);
```

#### 語法拆解

| 元素 | 說明 |
|------|------|
| `setContentView()` | Activity 的方法,設定畫面佈局 |
| `R` | 自動產生的資源類別 |
| `layout` | 佈局資源類型 |
| `activity_main` | 佈局檔案名稱(不含 .xml) |

#### 路徑對應

```
setContentView(R.layout.activity_main)
        ↓
res/layout/activity_main.xml
```

#### 執行流程

```
1. 讀取 res/layout/activity_main.xml
    ↓
2. 解析 XML 內容
    ↓
3. 建立 View 物件
    ├── ConstraintLayout (根視圖)
    └── 其他子 View
    ↓
4. 設定為 Activity 的內容視圖
    ↓
5. 畫面準備完成
```

#### 🌳 View 樹狀結構範例

```xml
<!-- activity_main.xml -->
<ConstraintLayout id="@+id/main">  ← 根視圖
    ├── <TextView />               ← 子視圖 1
    ├── <Button />                 ← 子視圖 2
    └── <ImageView />              ← 子視圖 3
</ConstraintLayout>
```

轉換成 Java 物件:

```
ConstraintLayout (R.id.main)
    │
    ├── TextView 物件
    ├── Button 物件
    └── ImageView 物件
```

---

### 🪟 第 18-21 行:視窗內邊距監聽器

```java
ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
    return insets;
});
```

#### 完整語法拆解

##### 第 18 行

```java
ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
```

| 元素 | 說明 |
|------|------|
| `ViewCompat` | 向下相容的 View 工具類別 |
| `setOnApplyWindowInsetsListener()` | 設定內邊距監聽器的方法 |
| `findViewById(R.id.main)` | 尋找 id 為 "main" 的視圖 |
| `(v, insets) -> { }` | Lambda 表達式(監聽器的實作) |

##### findViewById 詳解

```java
findViewById(R.id.main)
    ↓
在剛才設定的 activity_main.xml 中尋找
    ↓
尋找 android:id="@+id/main" 的元素
    ↓
回傳該 View 物件的參照
```

##### Lambda 表達式說明

```java
(v, insets) -> {
    // 方法實作
}
```

**等同於傳統寫法**:

```java
new OnApplyWindowInsetsListener() {
    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        // 方法實作
        return insets;
    }
}
```

| Lambda 參數 | 型別 | 說明 |
|------------|------|------|
| `v` | `View` | 套用內邊距的視圖(即 R.id.main) |
| `insets` | `WindowInsetsCompat` | 視窗內邊距資料 |

##### 第 19 行

```java
Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
```

| 元素 | 說明 |
|------|------|
| `Insets` | 內邊距資料類別 |
| `systemBars` | 變數名稱 |
| `insets.getInsets()` | 取得特定類型的內邊距 |
| `WindowInsetsCompat.Type.systemBars()` | 系統列類型(狀態列+導航列) |

#### 系統列包含內容

```
┌────────────────────────────┐
│  🔋 12:34  📶 WiFi  🔋 80%│ ← 狀態列 (Status Bar)
├────────────────────────────┤
│                            │
│                            │
│      App 內容區域         │
│                            │
│                            │
├────────────────────────────┤
│    ◀    ⚫    ▢          │ ← 導航列 (Navigation Bar)
└────────────────────────────┘
```

#### Insets 資料結構

```java
Insets systemBars = {
    left: 0,      // 左側內邊距 (直立模式通常為 0)
    top: 48,      // 上方內邊距 (狀態列高度,單位:px)
    right: 0,     // 右側內邊距 (直立模式通常為 0)
    bottom: 126   // 下方內邊距 (導航列高度,單位:px)
}
```

> 📏 **單位說明**:數值單位為像素(px),不同裝置數值不同

##### 第 20 行

```java
v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
```

| 參數 | 說明 | 典型值 |
|------|------|--------|
| `systemBars.left` | 左側內距 | 0 px |
| `systemBars.top` | 上方內距 | 48 px (狀態列) |
| `systemBars.right` | 右側內距 | 0 px |
| `systemBars.bottom` | 下方內距 | 126 px (導航列) |

#### 視覺效果對比

```
沒有設定 Padding:
┌────────────────────────────┐
│  🔋 12:34  📶           │ ← 狀態列
├────────────────────────────┤
│ Hello World (被遮住一部分) │ ← 內容延伸到狀態列下
│                            │
│                            │
├────────────────────────────┤
│    ◀    ⚫    ▢          │ ← 導航列
│ Button (被遮住)           │ ← 內容延伸到導航列下
└────────────────────────────┘

設定 Padding 後:
┌────────────────────────────┐
│  🔋 12:34  📶           │ ← 狀態列
├────────────────────────────┤
│                            │ ← top padding
│ Hello World (完整顯示)    │
│                            │
│                            │
│                            │ ← bottom padding
├────────────────────────────┤
│    ◀    ⚫    ▢          │ ← 導航列
└────────────────────────────┘
```

##### 第 21 行

```java
return insets;
```

> **作用**:回傳原始的 insets,允許其他監聽器繼續處理

#### 回傳值的重要性

```
return insets  → 其他視圖仍可接收到內邊距資訊
return null    → 中斷傳遞鏈,其他視圖收不到 ❌
```

---

## 完整生命週期流程

### 🚀 從 App 啟動到顯示(15 個步驟)

#### 階段 1️⃣:系統準備

```
1. 使用者點擊桌面 App 圖示
    ↓
2. Android Launcher 發送 Intent
    {
      action: "android.intent.action.MAIN",
      category: "android.intent.category.LAUNCHER"
    }
    ↓
3. 系統查詢 AndroidManifest.xml
    ↓
4. 找到 MainActivity 且 exported="true"
    ↓
5. 系統建立 MainActivity 物件
```

#### 階段 2️⃣:onCreate 初始化

```
6. 呼叫 onCreate(null)
    ↓
7. super.onCreate(savedInstanceState)
    ├── AppCompatActivity 初始化
    ├── 設定主題
    └── 準備視窗
    ↓
8. setContentView(R.layout.activity_main)
    ├── 讀取 activity_main.xml
    ├── 解析 XML
    ├── 建立 View 物件
    └── 建構 View 樹狀結構
```

#### 階段 3️⃣:處理視窗內邊距

```
9. findViewById(R.id.main)
    └── 取得根視圖的參照
    ↓
10. 設定 OnApplyWindowInsetsListener
    ↓
11. 系統觸發內邊距計算
    ↓
12. Lambda 函式執行
    ├── 取得系統列尺寸
    ├── 計算內邊距
    └── 套用 Padding
```

#### 階段 4️⃣:畫面顯示

```
13. onStart() 被呼叫
    └── Activity 變為可見
    ↓
14. onResume() 被呼叫
    └── Activity 獲得焦點
    ↓
15. 畫面顯示於螢幕上 🎉
```

> ⏱️ **總耗時**:通常 < 500 毫秒

---

### 📱 完整 Activity 生命週期圖

```
App 啟動
    ↓
[onCreate()]  ← 我們的程式碼在這裡
    ↓
[onStart()]
    ↓
[onResume()]  ← 畫面顯示,可互動
    ↓
    ├──→ 使用者按 Home 鍵
    │       ↓
    │   [onPause()]
    │       ↓
    │   [onStop()]  ← App 在背景
    │       ↓
    │   使用者返回 App
    │       ↓
    │   [onRestart()]
    │       ↓
    └───[onStart()] → [onResume()]

    ├──→ 螢幕旋轉
    │       ↓
    │   [onPause()]
    │       ↓
    │   [onStop()]
    │       ↓
    │   [onDestroy()]
    │       ↓
    │   重新建立 Activity
    │       ↓
    └───[onCreate(savedInstanceState ≠ null)]

    └──→ 使用者按返回鍵
            ↓
        [onPause()]
            ↓
        [onStop()]
            ↓
        [onDestroy()]  ← Activity 銷毀
            ↓
        App 結束
```

---

## 視窗內邊距系統

### 🪟 什麼是 Window Insets?

> **定義**:系統 UI(狀態列、導航列等)佔據的螢幕空間

```
實體螢幕
┌────────────────────────────┐
│ ┌ Insets.top = 48px        │ ← 狀態列區域
│ ├──────────────────────────┤
│ │                          │
│ │   可用內容區域           │
│ │   (App 應該繪製的範圍)   │
│ │                          │
│ ├──────────────────────────┤
│ └ Insets.bottom = 126px    │ ← 導航列區域
└────────────────────────────┘
```

### 📊 不同 Insets 類型

| 類型 | 說明 | 包含內容 |
|------|------|---------|
| `systemBars()` | 系統列 | 狀態列 + 導航列 |
| `statusBars()` | 狀態列 | 僅頂部狀態列 |
| `navigationBars()` | 導航列 | 僅底部導航列 |
| `ime()` | 輸入法 | 軟鍵盤 |
| `systemGestures()` | 手勢區域 | 手勢導航區 |

### 🎯 處理方式比較

#### 方式 1:設定 Padding(本程式碼使用)

```java
v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
```

**優點**:
- ✅ 內容完整顯示
- ✅ 不會被系統列遮擋

**缺點**:
- ❌ 無法使用系統列下方空間
- ❌ 畫面較小

**適用場景**:大部分普通 App

#### 方式 2:使用 Margin

```java
ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
params.topMargin = systemBars.top;
params.bottomMargin = systemBars.bottom;
v.setLayoutParams(params);
```

**優點**:
- ✅ 不影響內容大小
- ✅ 佈局更靈活

**缺點**:
- ⚠️ 需要額外程式碼

**適用場景**:複雜佈局

#### 方式 3:全螢幕顯示(不處理)

```java
// 不設定任何 Padding/Margin
```

**優點**:
- ✅ 使用全螢幕空間
- ✅ 視覺效果更震撼

**缺點**:
- ❌ 內容會被遮擋
- ❌ 需要小心設計

**適用場景**:遊戲、影片播放器、圖片瀏覽器

---

### 🔄 Edge-to-Edge 模式

#### 什麼是 Edge-to-Edge?

> **定義**:內容延伸到螢幕邊緣,包括系統列下方

```
傳統模式:
┌────────────────────────────┐
│     狀態列 (系統管理)      │
├────────────────────────────┤
│                            │
│    App 內容區域           │ ← 系統自動留空間
│                            │
├────────────────────────────┤
│     導航列 (系統管理)      │
└────────────────────────────┘

Edge-to-Edge 模式:
┌────────────────────────────┐
│╔════════════════════════╗│
││    App 內容延伸到這裡  ││ ← 內容在狀態列下方
│╠════════════════════════╣│
││                        ││
││      主要內容區域      ││
││                        ││
│╠════════════════════════╣│
││    App 內容延伸到這裡  ││ ← 內容在導航列下方
│╚════════════════════════╝│
└────────────────────────────┘
```

#### 啟用 Edge-to-Edge

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    EdgeToEdge.enable(this);  // ← 啟用全螢幕模式

    setContentView(R.layout.activity_main);

    // 必須處理 Insets,否則內容會被遮擋
    ViewCompat.setOnApplyWindowInsetsListener(...);
}
```

> ⚠️ **重要**:本程式碼匯入了 `EdgeToEdge`,但沒有呼叫 `enable()`,所以未啟用全螢幕模式

---

## 關鍵概念總結

### 1. 🏗️ Activity 基礎

```
Activity = 一個畫面
MainActivity = 主畫面
extends AppCompatActivity = 繼承向下相容的基礎類別
```

### 2. 🔄 onCreate 生命週期

```java
onCreate(Bundle savedInstanceState) {
    1. super.onCreate()      // 必須第一行
    2. setContentView()      // 設定畫面
    3. findViewById()        // 尋找元件
    4. 初始化邏輯            // 設定監聽器等
}
```

### 3. 📦 資源系統

| Java 程式碼 | XML 資源檔案 |
|------------|-------------|
| `R.layout.activity_main` | `res/layout/activity_main.xml` |
| `R.id.main` | `android:id="@+id/main"` |
| `R.string.app_name` | `res/values/strings.xml` |

### 4. 🪟 視窗內邊距

```
Window Insets = 系統 UI 佔據的空間
處理方式:設定 Padding/Margin
目的:避免內容被系統列遮擋
```

### 5. 🎯 Lambda 表達式

```java
// 傳統寫法
new OnClickListener() {
    @Override
    public void onClick(View v) {
        // ...
    }
}

// Lambda 寫法
(v) -> {
    // ...
}
```

---

## 🎓 類比總結

### MainActivity = 房屋建造過程

```java
package com.example.firstapp;
// 📍 地址:這棟房子位於「範例街第一號」

import ...;
// 📦 建材:從倉庫運來需要的材料

public class MainActivity extends AppCompatActivity {
// 🏠 藍圖:這是一棟「現代化住宅」類型的房子

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    // 🔨 施工方法:建造房屋的步驟

        super.onCreate(savedInstanceState);
        // 🏗️ 打地基:先完成基礎建設

        setContentView(R.layout.activity_main);
        // 🪟 裝潢:依照設計圖安裝門窗、隔間

        ViewCompat.setOnApplyWindowInsetsListener(...);
        // 🛡️ 防護措施:確保傢俱不會被門窗遮擋

        findViewById(R.id.main)
        // 🔍 找到「主客廳」

        (v, insets) -> {
            // 📏 測量窗戶佔據的空間
            Insets systemBars = insets.getInsets(...);

            // 🪑 擺放傢俱:留出安全距離
            v.setPadding(...);

            return insets;
        }
    }
}
```

---

## 📚 程式碼變化版本

### 版本 1:最簡化版本(無 Insets 處理)

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
```

**適用場景**:
- ✅ 簡單 App
- ✅ 不需要 Edge-to-Edge 效果
- ✅ 系統自動處理內邊距

---

### 版本 2:標準版本(本程式碼)

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
```

**適用場景**:
- ✅ 需要精確控制內邊距
- ✅ 準備支援 Edge-to-Edge
- ✅ 現代化 App 標準做法

---

### 版本 3:Edge-to-Edge 完整版

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);  // 啟用全螢幕

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
```

**適用場景**:
- ✅ 現代化 UI 設計
- ✅ 全螢幕沉浸式體驗
- ✅ Android 15+ 推薦做法

---

### 版本 4:進階版本(分離處理狀態列與導航列)

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // 分別取得狀態列和導航列
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            Insets navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            // 只在頂部和底部設定 Padding
            v.setPadding(0, statusBars.top, 0, navBars.bottom);

            return insets;
        });
    }
}
```

**適用場景**:
- ✅ 需要左右延伸到螢幕邊緣
- ✅ 複雜佈局設計
- ✅ 特殊 UI 需求

---

## ✅ 學習檢查清單

完成本教學後,你應該能夠:

- [ ] 理解 `package` 宣告的作用
- [ ] 知道為什麼要 `import` 類別
- [ ] 明白 `extends AppCompatActivity` 的意義
- [ ] 了解 `@Override` 註解的用途
- [ ] 知道 `onCreate()` 的執行時機
- [ ] 理解 `super.onCreate()` 必須在第一行
- [ ] 明白 `setContentView()` 的作用
- [ ] 知道 `findViewById()` 如何尋找元件
- [ ] 理解 Lambda 表達式的語法
- [ ] 了解 Window Insets 的概念
- [ ] 知道如何處理系統列遮擋問題
- [ ] 明白 `setPadding()` 的四個參數意義

---

## 🔗 相關檔案

| 檔案 | 說明 |
|------|------|
| `MainActivity.java` | 主程式進入點(本檔案) |
| `AndroidManifest.xml` | App 設定檔,註冊 MainActivity |
| `res/layout/activity_main.xml` | 主畫面佈局 |
| `res/values/themes.xml` | 主題樣式定義 |
| `res/values/strings.xml` | 字串資源 |

---

## 🆚 與其他檔案的關係

```
AndroidManifest.xml
    ├── 宣告 MainActivity 為主入口
    ├── 設定 MAIN + LAUNCHER intent-filter
    └── 定義 android:theme
        ↓
MainActivity.java (本檔案)
    ├── onCreate() 被呼叫
    ├── 套用主題
    └── 載入 activity_main.xml
        ↓
activity_main.xml
    └── 定義畫面佈局
        ├── ConstraintLayout (id=main)
        └── 其他 UI 元件
```

---

## 🎯 常見問題 FAQ

### Q1:可以刪除未使用的 import 嗎?

**A**:可以!如果沒呼叫 `EdgeToEdge.enable()`,可以刪除:

```java
import androidx.activity.EdgeToEdge;  // ← 可刪除
```

但建議保留其他 import,因為它們正在被使用。

---

### Q2:為什麼要用 Lambda 而不是傳統匿名類別?

**A**:Lambda 更簡潔且易讀:

```java
// 傳統寫法:7 行
ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        // ...
        return insets;
    }
});

// Lambda:3 行
ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
    // ...
    return insets;
});
```

---

### Q3:不處理 Insets 會怎樣?

**A**:如果沒啟用 Edge-to-Edge,系統會自動處理,沒問題。

但如果啟用了 `EdgeToEdge.enable()`,不處理會導致:
- ❌ 頂部內容被狀態列遮擋
- ❌ 底部按鈕被導航列遮擋
- ❌ 使用者無法點擊部分元件

---

### Q4:savedInstanceState 什麼時候不是 null?

**A**:以下情況會有值:
- 🔄 螢幕旋轉
- 📱 App 被系統回收記憶體後重建
- 🔙 從其他 Activity 返回
- ⚙️ 系統設定變更(語言、字體大小等)

---

### Q5:可以在 onCreate 之外呼叫 setContentView 嗎?

**A**:技術上可以,但不建議:

```java
// ❌ 不建議
public void changeLayout() {
    setContentView(R.layout.another_layout);  // 會重置整個畫面
}

// ✅ 建議:使用 Fragment 或 include
```

---

## 📖 延伸學習

### 進階主題

1. **Activity 生命週期完整解析**
   - onStart、onResume、onPause、onStop、onDestroy

2. **Fragment 的使用**
   - 單一 Activity 多畫面切換
   - Fragment 生命週期

3. **View Binding 與 Data Binding**
   - 取代 findViewById 的現代做法

4. **Jetpack Compose**
   - Android 新一代 UI 框架
   - 宣告式 UI 設計

5. **ViewModel 與 LiveData**
   - 處理配置變更
   - 資料持久化

---

**📅 文件版本**:v1.0
**👤 適用對象**:Android 初學者
**💡 建議**:保存此文件作為參考資料
**🔗 搭配閱讀**:AndroidManifest.xml 完整解析指南

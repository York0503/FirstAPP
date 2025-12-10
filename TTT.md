# AndroidManifest.xml 完整解析指南

> Documentation：Android App 的設定檔案完整教學
> Author：York Lai
> Last Updated：2025/12/08

<details>
<summary>CHANGELOG</summary>

> 建立文件
> [time=Mon, Dec 8, 2025 10:55 AM][name=阿諆]

</details>

---
## 📖 目錄

1. [整體架構](##整體架構)
2. [逐行詳細解析](##逐行詳細解析)
3. [完整啟動流程](##完整啟動流程)
4. [資源參照系統](##資源參照系統)
5. [關鍵概念總結](##關鍵概念總結)

---

## 整體架構

### 檔案結構樹狀圖

```
AndroidManifest.xml
│
├── 📄 XML 宣告
│
├── 📦 <manifest> 根容器
│   │
│   └── 🎯 <application> App 容器
│       │
│       ├── ⚙️ App 全域屬性
│       │   ├── 備份設定
│       │   ├── 圖示設定
│       │   ├── 名稱設定
│       │   └── 主題設定
│       │
│       └── 🪟 <activity> 主 Activity
│           ├── 名稱與權限設定
│           └── 🎯 <intent-filter> 啟動條件
│               ├── MAIN action
│               └── LAUNCHER category
```

### 完整程式碼

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.FirstAPP">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
 ---
### 啟動流程

1. 使用者點擊桌面上的 App 圖示
   ↓
2. Android Launcher 發送 Intent：
   Action: android.intent.action.MAIN
   Category: android.intent.category.LAUNCHER
   ↓
3. 系統查詢 AndroidManifest.xml
   ↓
4. 找到符合的 <intent-filter>
   ↓
5. 讀取 android:name=".MainActivity"
   ↓
6. 展開完整名稱：com.example.firstapp.MainActivity
   ↓
7. 載入並啟動 MainActivity
   ↓
8. 呼叫 onCreate()
   ↓
9. App 顯示畫面
---

## 逐行詳細解析

### 📄 第 1 行：XML 宣告

```xml
<?xml version="1.0" encoding="utf-8"?>
```

| 屬性 | 值 | 說明 |
|------|------|------|
| `<?xml ... ?>`     | 無 | XML 處理指令           |
| `version` | `1.0` | XML 規範版本 |
| `encoding` | `utf-8` | 字元編碼（支援中文、emoji） |

> **作用**：告訴系統這是一個 XML 格式的檔案

---

### 📦 第 2-3 行：Manifest 根標籤

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
```

#### 屬性說明

| 屬性 | 說明 | 範例 |
|------|------|------|
| `xmlns:android` | Android 命名空間 | `android:name`、`android:icon` |
| `xmlns:tools` | 開發工具命名空間 | `tools:context`（僅開發時使用） |

#### 💡 什麼是命名空間？

```
就像電話號碼的區碼：
+886 - 02 -1234-5678
  ↑    ↑
 國碼  區碼

android:name
   ↑
   命名空間前綴
```

---

### 🎯 第 5 行：Application 標籤

```xml
<application
```

> **作用**：定義 App 的全域設定和所有元件

**包含內容**：
- ✅ App 外觀（圖示、名稱、主題）
- ✅ App 行為（備份規則）
- ✅ App 元件（Activity、Service、Receiver）

---

### 💾 第 6-8 行：備份設定

```xml
android:allowBackup="true"
android:dataExtractionRules="@xml/data_extraction_rules"
android:fullBackupContent="@xml/backup_rules"
```

#### `allowBackup` 說明

| 值 | 效果 | 適用場景 |
|------|------|---------|
| `true` | ✅ 允許自動備份 | 一般 App、遊戲 |
| `false` | ❌ 禁止備份 | 金融 App、高安全性應用 |

#### 備份流程圖

```
使用者裝置 → [自動備份] → Google 雲端 → [重新安裝] → 恢復資料
```

---

### 🖼️ 第 9-10 行：圖示與名稱

```xml
android:icon="@mipmap/ic_launcher"
android:label="@string/app_name"
```

#### 圖示資源結構

```
res/
├── 📁 mipmap-mdpi/
│   └── ic_launcher.png    (48×48)
├── 📁 mipmap-hdpi/
│   └── ic_launcher.png    (72×72)
├── 📁 mipmap-xhdpi/
│   └── ic_launcher.png    (96×96)
├── 📁 mipmap-xxhdpi/
│   └── ic_launcher.png    (144×144)
└── 📁 mipmap-xxxhdpi/
    └── ic_launcher.png    (192×192)
```

> **系統會根據螢幕密度自動選擇合適的圖示**

#### 名稱資源

**路徑**：`@string/app_name` → `res/values/strings.xml`

```xml
<!-- res/values/strings.xml -->
<resources>
    <string name="app_name">FirstAPP</string>
</resources>
```

#### ❓ 為什麼不直接寫 "FirstAPP"？

| 原因 | 說明 |
|------|------|
| 🌍 **多語言支援** | 可建立 `values-zh/strings.xml` 提供中文 |
| 🔧 **統一管理** | 修改只需改一處 |
| ✅ **最佳實踐** | Android 官方推薦 |

---

### 🔵 第 11 行：圓形圖示

```xml
android:roundIcon="@mipmap/ic_launcher_round"
```

#### 視覺差異

| 類型 | 形狀 | 使用裝置 |
|------|------|---------|
| `icon` | 方形 ▢ | 一般裝置 |
| `roundIcon` | 圓形 ● | Google Pixel、部分廠商 |

```
方形圖示              圓形圖示
┌──────────┐         ╭──────────╮
│  [LOGO]  │         │  [LOGO]  │
└──────────┘         ╰──────────╯
```

---

### 🌐 第 12 行：RTL 支援

```xml
android:supportsRtl="true"
```

**RTL** = Right-To-Left（從右到左）

#### 支援語言

- 🇸🇦 阿拉伯語
- 🇮🇱 希伯來語
- 🇮🇷 波斯語

#### 介面鏡像效果

```
LTR（英文）：
┌─────────────────────┐
│ [←]  標題     [☰]  │
│                     │
│ 內容從左到右排列... │
└─────────────────────┘

RTL（阿拉伯文）：
┌─────────────────────┐
│ [☰]     標題   [←] │
│                     │
│ ...列排右到左從容內 │
└─────────────────────┘
```

---

### 🎨 第 13 行：主題設定

```xml
android:theme="@style/Theme.FirstAPP">
```

#### 主題繼承鏈

```
Google Material3 主題
    └── Theme.Material3.DayNight.NoActionBar
        ├── ✅ 預設顏色
        ├── ✅ 預設字體
        └── ✅ 預設樣式

        ↓ 繼承 (parent)

Base.Theme.FirstAPP
    └── 可覆寫任何設定

    ↓ 繼承

Theme.FirstAPP ← AndroidManifest 使用這個
```

- 繼承鏈的程式碼
```xml!
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Base.Theme.FirstAPP" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your light theme here. -->
        <!-- <item name="colorPrimary">@color/my_light_primary</item> -->
    </style>

    <style name="Theme.FirstAPP" parent="Base.Theme.FirstAPP" />
</resources>
```

#### 主題控制項目

| 分類 | 包含內容 |
|------|---------|
| 🎨 **顏色** | 主色、次色、背景色、文字色 |
| 🔤 **字體** | 字型、大小、粗細 |
| 🎯 **樣式** | 按鈕樣式、圓角、陰影 |

---

### 🪟 第 14-16 行：Activity 定義

```xml
<activity
    android:name=".MainActivity"
    android:exported="true">
```

#### `<activity>` 標籤

> ⚠️ **重要**：所有 Activity 都必須在此註冊，否則無法使用！

#### `android:name=".MainActivity"`

**路徑展開過程**：

```
.MainActivity
    ↓ (manifest package="com.example.firstapp")
com.example.firstapp.MainActivity
    ↓
src/main/java/com/example/firstapp/MainActivity.java
```

#### `android:exported="true"`

| 值 | 說明 | 適用場景 |
|------|------|---------|
| `true` | ✅ 允許外部啟動 | 主 Activity、分享功能 |
| `false` | ❌ 僅內部啟動 | 設定頁面、詳細頁面 |

#### ❓ 為什麼主 Activity 要 `exported="true"`？

```
使用者點擊桌面圖示
    ↓
桌面 Launcher (外部 App) 嘗試啟動
    ↓
├─ exported="false" → ❌ 安全檢查失敗
└─ exported="true"  → ✅ 允許啟動
```

---

### 🎯 第 17-20 行：Intent Filter

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
```

#### 完整翻譯

> 💬 「這個 Activity 是 App 的主要入口，請在桌面上顯示圖示，當使用者點擊時啟動它」

#### 組成說明

| 元素 | 作用 | 說明 |
|------|------|------|
| `<intent-filter>` | 定義可回應的 Intent | 過濾器容器 |
| `<action MAIN>` | 主要入口 | 標記為啟動點 |
| `<category LAUNCHER>` | 桌面圖示 | 顯示在桌面 |

#### Intent 配對流程

```
1. 使用者點擊圖示
    ↓
2. Launcher 發送 Intent {
     action: MAIN,
     category: LAUNCHER
   }
    ↓
3. 系統掃描所有 App 的 Manifest
    ↓
4. 找到符合的 <intent-filter>
    ↓
5. 啟動 MainActivity
    ↓
6. App 畫面顯示
```

---

## 完整啟動流程

### 🚀 從點擊到顯示（19 個步驟）

#### 階段 1️⃣：查找 App

```
1. 使用者在桌面看到「FirstAPP」圖示
   ├─ 圖示：android:icon="@mipmap/ic_launcher"
   └─ 名稱：android:label="@string/app_name"

2. 使用者點擊圖示
```

#### 階段 2️⃣：Intent 配對

```
3. Launcher 發送 Intent
   {
     action: "android.intent.action.MAIN",
     category: "android.intent.category.LAUNCHER"
   }

4. 系統掃描所有 AndroidManifest.xml

5. 找到符合的 <intent-filter> ✅
   ├─ <action MAIN> ✓
   └─ <category LAUNCHER> ✓
```

#### 階段 3️⃣：載入 Activity

```
6. 讀取 android:name=".MainActivity"

7. 展開完整類別名稱
   com.example.firstapp.MainActivity

8. 檢查 android:exported="true" ✓

9. 建立 MainActivity 物件
```

#### 階段 4️⃣：初始化

```
10. 應用主題
    android:theme="@style/Theme.FirstAPP"

11. 呼叫 onCreate(null)

12. super.onCreate() → 基礎初始化

13. setContentView(R.layout.activity_main)
```

#### 階段 5️⃣：建立 UI

```
14. 載入 activity_main.xml

15. 解析 <com.example.firstapp.GameView>

16. 建立 GameView 物件

17. 執行 init()
    ├─ 初始化畫筆
    └─ 啟動動畫 post(updateRunnable)
```

#### 階段 6️⃣：顯示畫面

```
18. 開始動畫循環
    update() → onDraw() → 等待 16ms → 重複

19. App 畫面顯示完成！ 🎉
```

> ⏱️ **總耗時**：通常 < 1 秒

---

## 資源參照系統

### 📂 資源參照語法

```xml
@資源類型/資源名稱
```

#### 常用資源類型

| 符號 | 資源類型 | 檔案位置 | 範例 |
|------|---------|---------|------|
| `@string/` | 字串 | `res/values/strings.xml` | `@string/app_name` |
| `@color/` | 顏色 | `res/values/colors.xml` | `@color/black` |
| `@style/` | 樣式/主題 | `res/values/themes.xml` | `@style/Theme.FirstAPP` |
| `@layout/` | 佈局 | `res/layout/*.xml` | `@layout/activity_main` |
| `@drawable/` | 圖片 | `res/drawable/*.png` | `@drawable/icon` |
| `@mipmap/` | 圖示 | `res/mipmap-*/*.png` | `@mipmap/ic_launcher` |
| `@dimen/` | 尺寸 | `res/values/dimens.xml` | `@dimen/padding` |

### 🗺️ 完整路徑對應圖

```
AndroidManifest.xml
│
├─ android:icon="@mipmap/ic_launcher"
│  └──> res/mipmap-*/ic_launcher.png
│
├─ android:label="@string/app_name"
│  └──> res/values/strings.xml
│       └──> <string name="app_name">FirstAPP</string>
│
├─ android:roundIcon="@mipmap/ic_launcher_round"
│  └──> res/mipmap-*/ic_launcher_round.png
│
├─ android:theme="@style/Theme.FirstAPP"
│  └──> res/values/themes.xml
│       └──> <style name="Theme.FirstAPP">
│            └──> parent="Base.Theme.FirstAPP"
│                 └──> parent="Theme.Material3..."
│                      └──> Google Material3 主題庫
│
├─ android:dataExtractionRules="@xml/data_extraction_rules"
│  └──> res/xml/data_extraction_rules.xml
│
└─ android:name=".MainActivity"
   └──> package + MainActivity
        └──> com.example.firstapp.MainActivity
             └──> .../MainActivity.java
```

### 💡 資源參照的兩種方式

#### XML 中使用 `@` 符號

```xml
android:theme="@style/Theme.FirstAPP"
android:text="@string/hello"
android:background="@color/white"
```

#### Java/Kotlin 中使用 `R` 類別

```java
setTheme(R.style.Theme_FirstAPP);    // 點變底線
setText(R.string.hello);
setBackgroundColor(R.color.white);
```

> ⚠️ **注意**：Java 中點(`.`)會轉成底線(`_`)

---

## 關鍵概念總結

### 1. 📦 資源參照系統

```
@ = 「查找資源」的指令

格式：@類型/名稱
範例：@string/app_name
     ↓
res/values/strings.xml 中的
<string name="app_name">
```

### 2. 🎯 Intent 與 Intent Filter

| 概念 | 說明 | 類比 |
|------|------|------|
| **Intent** | 「我想做某事」的請求 | 客人的點餐單 |
| **Intent Filter** | 「我可以處理這個請求」 | 餐廳的菜單 |

```
MAIN + LAUNCHER = 主入口 + 桌面圖示
```

### 3. 🏷️ 命名空間

| 前綴 | 說明 | 範例 |
|------|------|------|
| `android:` | Android 系統屬性 | `android:name` |
| `tools:` | 開發工具屬性 | `tools:context` |
| `app:` | 自訂或支援庫屬性 | `app:layout_constraint*` |

### 4. 🔗 繼承機制

```
父主題（定義所有設定）
    ↓ extends/parent
子主題（繼承所有設定）
    └─ 只需覆寫想改的部分
    └─ 其他保持原樣
```

---

## 🎓 類比總結

### AndroidManifest.xml = 公司登記證

```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- 📄 文件格式聲明 -->

<manifest>
<!-- 📋 這是一份登記證 -->

    <application
    <!-- 🏢 公司基本資料 -->

        android:icon="公司Logo"
        <!-- 🖼️ 商標 -->

        android:label="公司名稱"
        <!-- 📛 正式名稱 -->

        android:theme="裝潢風格">
        <!-- 🎨 企業形象 -->

        <activity
        <!-- 🏪 營業項目（部門）-->

            android:name=".MainActivity"
            <!-- 💼 主要業務部門 -->

            android:exported="true">
            <!-- 🚪 對外開放 -->

            <intent-filter>
            <!-- 📋 服務項目清單 -->

                <action MAIN />
                <!-- 🎯 主要服務窗口 -->

                <category LAUNCHER />
                <!-- 🏢 在商業區有店面 -->

            </intent-filter>
        </activity>
    </application>
</manifest>
```

---

## 📚 補充資料

### Intent Filter 其他範例

#### 範例 1：分享功能

```xml
<activity android:name=".ShareActivity">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

**效果**：當其他 App 分享文字時，會出現在選單中

#### 範例 2：開啟網頁連結

```xml
<activity android:name=".WebActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="https"
            android:host="www.example.com" />
    </intent-filter>
</activity>
```

**效果**：點擊 `https://www.example.com/*` 連結時，可用此 App 開啟

---

## ✅ 檢查清單

在發布 App 前，確認以下項目：

- [ ] `android:icon` 已設定且圖示美觀
- [ ] `android:label` 已設定且名稱正確
- [ ] `android:theme` 已套用正確主題
- [ ] 主 Activity 有 `MAIN` + `LAUNCHER` intent-filter
- [ ] 主 Activity 的 `android:exported="true"`
- [ ] 所有 Activity 都已在 Manifest 註冊
- [ ] 所需權限已聲明（如網路、相機等）

---

## 🔗 相關檔案

| 檔案 | 說明 |
|------|------|
| `AndroidManifest.xml` | App 設定檔（本檔案）|
| `MainActivity.java` | 主程式進入點 |
| `activity_main.xml` | 主畫面佈局 |
| `themes.xml` | 主題樣式定義 |
| `strings.xml` | 字串資源 |
| `colors.xml` | 顏色資源 |

---

**📅 文件版本**：v1.0
**👤 適用對象**：Android 初學者
**💡 建議**：保存此文件作為參考資料


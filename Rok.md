# 自動產生的「R」規則詳解

> Documentation：Android R 資源類別規則說明
> Author：York Lai
> Last Updated：2025/12/08

---

## 規則 1：資料夾名稱是固定的

**Android 只認識特定的資料夾名稱**

### 如何查看支援的資料夾類型

1. 右鍵點擊 `res/` 資料夾
2. 選擇 **New → Android Resource Directory**
3. 在 **"Resource type"** 下拉選單中
   → 就能看到所有支援的類型！

![Android Studio 資源類型選單](https://hackmd.io/_uploads/rk_JtWVfbe.png)

### 支援的資料夾類型

- `animator`
- `anim`
- `color`
- `drawable`
- `font`
- `layout`
- `menu`
- `mipmap`
- `raw`
- `values`
- `xml`
- `transition`
- `interpolator`
- `navigation`

---

## 規則 2：R 的格式是固定的

### 基本格式

```
R.資料夾類型.檔案名稱
  ↑        ↑
  固定     你可以自訂
```

- **資料夾類型** → 固定的（必須是 Android 認識的類型）
- **檔案名稱** → 你取的名稱

---

## 所有標準資源資料夾

| 資料夾名稱 | R 類型 | 用途 | 檔案類型 |
|-----------|--------|------|---------|
| `animator/` | `R.animator.xxx` | 屬性動畫 | XML |
| `anim/` | `R.anim.xxx` | 視圖動畫 | XML |
| `color/` | `R.color.xxx` | 顏色狀態列表 | XML |
| `drawable/` | `R.drawable.xxx` | 圖片、形狀 | PNG, JPG, XML |
| `mipmap/` | `R.mipmap.xxx` | App 圖示 | PNG |
| `layout/` | `R.layout.xxx` | 畫面佈局 | XML |
| `menu/` | `R.menu.xxx` | 選單定義 | XML |
| `raw/` | `R.raw.xxx` | 原始檔案 | 任何格式 |
| `values/` | 特殊 | 字串、顏色、尺寸等 | XML |
| `xml/` | `R.xml.xxx` | 任意 XML | XML |
| `font/` | `R.font.xxx` | 字型檔案 | TTF, OTF |
| `transition/` | `R.transition.xxx` | 場景轉換 | XML |

---

## values/ 資料夾的子類型

**特別注意**：`values/` 資料夾不看檔案名稱，看 XML 內容中的標籤！

| XML 標籤 | R 類型 | 範例 |
|---------|--------|------|
| `<string>` | `R.string.xxx` | `R.string.app_name` |
| `<color>` | `R.color.xxx` | `R.color.black` |
| `<dimen>` | `R.dimen.xxx` | `R.dimen.padding` |
| `<style>` | `R.style.xxx` | `R.style.Theme_FirstAPP` |
| `<array>` | `R.array.xxx` | `R.array.countries` |
| `<plurals>` | `R.plurals.xxx` | `R.plurals.items` |
| `<bool>` | `R.bool.xxx` | `R.bool.is_tablet` |
| `<integer>` | `R.integer.xxx` | `R.integer.max_count` |
| `<id>` | `R.id.xxx` | `R.id.custom_id` |

---

## 總結

### ✅ 你能控制的

```
檔案名稱：
res/layout/my_custom_name.xml  → R.layout.my_custom_name
                ↑
            你可以取任何名稱
```

### ❌ 你不能控制的

```
資料夾名稱：
res/layout/  ← 必須是 Android 認識的名稱
    ↑
    不能改成其他名稱
```

---

**📅 文件版本**：v1.0
**👤 適用對象**：Android 初學者

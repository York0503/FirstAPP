package com.example.firstapp;

/**
 * 碰撞偵測器（職責單一：只負責碰撞邏輯）
 *
 * 目前實作：道路邊界碰撞（clampToRoad）
 * 預留擴展：障礙物碰撞（AABB 矩形相交檢測）
 *
 * 類比後端：類似 Validator 工具類 — 靜態方法、無狀態
 */
public class CollisionDetector {

    /**
     * 道路邊界碰撞：確保車輛不會開出道路
     *
     * 邏輯：
     * - 如果車輛左邊界 < 道路左邊界 → 修正 car.x（貼齊左邊界）
     * - 如果車輛右邊界 > 道路右邊界 → 修正 car.x（貼齊右邊界）
     *
     * 效果：車輛碰到道路邊緣就被「推回去」，不會開到草地上
     *
     * @param car  車輛實體（讀取 x 和 width，可能修正 x）
     * @param road 道路實體（讀取 roadLeft 和 roadRight）
     */
    public static void clampToRoad(Car car, Road road) {
        float halfW = car.getWidth() / 2f;  // 半車寬（車輛以中心座標定位）

        // 檢查左邊界：車輛左邊緣 = car.x - halfW
        if (car.getX() - halfW < road.getRoadLeft()) {
            car.setX(road.getRoadLeft() + halfW);   // 貼齊左邊界
        }

        // 檢查右邊界：車輛右邊緣 = car.x + halfW
        if (car.getX() + halfW > road.getRoadRight()) {
            car.setX(road.getRoadRight() - halfW);  // 貼齊右邊界
        }
    }
}

package math3D;

import java.awt.*;

/**
 * VTs类（View To Screen）提供视图坐标到屏幕坐标的转换工具。
 * 将以视图中心为原点的坐标系转换为以左上角为原点的屏幕坐标系。
 * 屏幕尺寸固定为700x700。
 */
public class VTs {

  /**
   * 将视图X坐标转换为屏幕X坐标。
   * 屏幕X = 视图X + 屏幕宽度/2
   * 
   * @param x 视图X坐标
   * @return 屏幕X坐标
   */
  public int viewX_To_screenX(int x) {
    return x + 700 / 2;
  }

  /**
   * 将视图Y坐标转换为屏幕Y坐标。
   * 屏幕Y = -视图Y + 屏幕高度/2（Y轴方向翻转）
   * 
   * @param y 视图Y坐标
   * @return 屏幕Y坐标
   */
  public int viewY_To_screenY(int y) {
    return -y + 700 / 2;
  }

  /**
   * 将Point从视图坐标转换为屏幕坐标（静态方法）。
   * X: 视图X + 350, Y: 350 - 视图Y
   * 
   * @param point 待转换的点（原地修改并返回）
   * @return 转换后的屏幕坐标点
   */
  public static Point view_To_screen(Point point) {
    point.x = point.x + 700 / 2;
    point.y = 700 / 2 - point.y;
    return point;
  }

  /*
   * public Point view_To_screen1(Point point){
   * point.x=point.x+700/2;
   * point.y=700/2-point.y;
   * return point;
   * }
   */
}
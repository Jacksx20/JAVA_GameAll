import java.awt.Graphics2D;

import math3D.Point3D;

/**
 * EdgeBlock类表示魔方的棱块，每个棱块由2个可见的正方形面组成。
 * 魔方共有12个棱块，每个棱块位于2个面的交汇处。
 */
public class EdgeBlock {

	/** 棱块的第一个可见面 */
	private Square square1;
	/** 棱块的第二个可见面 */
	private Square square2;
	
	/**
	 * 棱块构造函数
	 * @param square1 第一个可见面
	 * @param square2 第二个可见面
	 */
	public EdgeBlock(Square square1, Square square2) {
		super();
		this.square1 = square1;
		this.square2 = square2;
	}
	
	/**
	 * 棱块绕经过(0,0,0)，point两点的直线旋转angle角度
	 * @param point 空间一点，确定旋转轴
	 * @param angle 旋转角度
	 * @param clockwise true为顺时针，false为逆时针
	 */
	public void rotate(Point3D point, double angle, boolean clockwise){
		square1.rotate(point, angle, clockwise);
		square2.rotate(point, angle, clockwise);
	}
	
	/**
	 * 绕XY轴旋转指定角度
	 * @param angleX 绕X轴旋转的角度
	 * @param angleY 绕Y轴旋转的角度
	 */
	public void rotateXY(double angleX, double angleY) {
		square1.rotateXY(angleX, angleY);
		square2.rotateXY(angleX, angleY);
	}
	
	/**
	 * 绘制棱块的2个可见面。
	 * @param graphics2D 图形绘制上下文
	 */
	public void draw(Graphics2D graphics2D){
		square1.draw(graphics2D);
		square2.draw(graphics2D);
	}
}

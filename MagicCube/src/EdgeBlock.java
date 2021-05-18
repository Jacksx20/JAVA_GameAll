import java.awt.Graphics2D;

import math3D.Point3D;

/**
 * 棱块，共12个
 * @author Administrator
 *
 */
public class EdgeBlock {

	private Square square1;
	private Square square2;
	
	/**
	 * 棱块构造函数
	 * @param square1
	 * @param square2
	 */
	public EdgeBlock(Square square1, Square square2) {
		super();
		this.square1 = square1;
		this.square2 = square2;
	}
	
	/**
	 * 楞块绕经过(0,0,0)，point两点的直线旋转angle角度
	 * @param point：空间一点
	 * @param angle：旋转角度
	 * @param clockwise: true为顺时针，false为逆时针
	 */
	public void rotate(Point3D point, double angle, boolean clockwise){
		square1.rotate(point, angle, clockwise);
		square2.rotate(point, angle, clockwise);
	}
	
	/**
	 * 绕XY中旋转角度angle
	 * @param angle
	 */
	public void rotateXY(double angleX, double angleY) {
		square1.rotateXY(angleX, angleY);
		square2.rotateXY(angleX, angleY);
	}
	
	public void draw(Graphics2D graphics2D){
		square1.draw(graphics2D);
		square2.draw(graphics2D);
	}
}

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math3D.Point3D;

/**
 * 魔方角块，共16个
 *
 */
public class CornerBlock {

	private Square square1;
	private Square square2;
	private Square square3;
	
	/**
	 * 角块构造函数
	 * @param square1
	 * @param square2
	 * @param square3
	 */
	public CornerBlock(Square square1, Square square2, Square square3) {
		super();
		this.square1 = square1;
		this.square2 = square2;
		this.square3 = square3;
	}
	
	/**
	 * 角块绕经过(0,0,0)，point两点的直线旋转angle角度
	 * @param point：空间一点
	 * @param angle：旋转角度
	 * @param clockwise: true为顺时针，false为逆时针
	 */
	public void rotate(Point3D point, double angle, boolean clockwise){
		square1.rotate(point, angle, clockwise);
		square2.rotate(point, angle, clockwise);
		square3.rotate(point, angle, clockwise);
	}
	
	/**
	 * 绕XY中旋转角度angle
	 * @param angle
	 */
	public void rotateXY(double angleX, double angleY) {
		square1.rotateXY(angleX, angleY);
		square2.rotateXY(angleX, angleY);
		square3.rotateXY(angleX, angleY);
	}
	
	public void draw(Graphics2D graphics2D){
		square1.draw(graphics2D);
		square2.draw(graphics2D);
		square3.draw(graphics2D);
	}
	
	public Point3D getCornerPoint(){
		Point3D point3D=new Point3D();
		List<Point3D> pointSet1=new ArrayList<Point3D>();
		pointSet1.add(square1.getPoint1());pointSet1.add(square1.getPoint2());
		pointSet1.add(square1.getPoint3());pointSet1.add(square1.getPoint4());
		List<Point3D> pointSet2=new ArrayList<Point3D>();
		pointSet2.add(square1.getPoint1());pointSet2.add(square1.getPoint2());
		pointSet2.add(square1.getPoint3());pointSet2.add(square1.getPoint4());
		pointSet1.retainAll(pointSet2);
		List<Point3D> pointSet3=new ArrayList<Point3D>();
		pointSet3.add(square1.getPoint1());pointSet3.add(square1.getPoint2());
		pointSet3.add(square1.getPoint3());pointSet3.add(square1.getPoint4());
		pointSet1.retainAll(pointSet3);
		point3D=pointSet1.get(0);
		return point3D;
	}
	
	public Point3D getBackPoint(Point3D centerPoint){
		double max=0,distance=0;
		Point3D point3D=new Point3D();
		List<Point3D> list=new ArrayList<Point3D>();
		list.add(square1.getPoint1());list.add(square1.getPoint2());
		list.add(square1.getPoint3());list.add(square1.getPoint4());
		list.add(square2.getPoint1());list.add(square2.getPoint2());
		list.add(square2.getPoint3());list.add(square2.getPoint4());
		list.add(square3.getPoint1());list.add(square3.getPoint2());
		list.add(square3.getPoint3());list.add(square3.getPoint4());
		for (Iterator<Point3D> iterator = list.iterator(); iterator.hasNext();) {
			Point3D point3d = (Point3D) iterator.next();
			distance=Math.sqrt(Math.pow(point3d.x-centerPoint.x, 2)
					+Math.pow(point3d.y-centerPoint.y, 2)
					+Math.pow(point3d.z-centerPoint.z, 2));
			if (distance>max) {
				max=distance;
				point3D=point3d;
			}
		}
		return point3D;
	}
}

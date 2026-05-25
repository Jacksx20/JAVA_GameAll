import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math3D.Point3D;

/**
 * CornerBlock类表示魔方的角块，每个角块由3个可见的正方形面组成。
 * 魔方共有8个角块，每个角块位于3个面的交汇处。
 */
public class CornerBlock {

	/** 角块的第一个可见面 */
	private Square square1;
	/** 角块的第二个可见面 */
	private Square square2;
	/** 角块的第三个可见面 */
	private Square square3;
	
	/**
	 * 角块构造函数
	 * @param square1 第一个可见面
	 * @param square2 第二个可见面
	 * @param square3 第三个可见面
	 */
	public CornerBlock(Square square1, Square square2, Square square3) {
		super();
		this.square1 = square1;
		this.square2 = square2;
		this.square3 = square3;
	}
	
	/**
	 * 角块绕经过(0,0,0)，point两点的直线旋转angle角度
	 * @param point 空间一点，确定旋转轴
	 * @param angle 旋转角度
	 * @param clockwise true为顺时针，false为逆时针
	 */
	public void rotate(Point3D point, double angle, boolean clockwise){
		square1.rotate(point, angle, clockwise);
		square2.rotate(point, angle, clockwise);
		square3.rotate(point, angle, clockwise);
	}
	
	/**
	 * 绕XY轴旋转指定角度
	 * @param angleX 绕X轴旋转的角度
	 * @param angleY 绕Y轴旋转的角度
	 */
	public void rotateXY(double angleX, double angleY) {
		square1.rotateXY(angleX, angleY);
		square2.rotateXY(angleX, angleY);
		square3.rotateXY(angleX, angleY);
	}
	
	/**
	 * 绘制角块的3个可见面。
	 * @param graphics2D 图形绘制上下文
	 */
	public void draw(Graphics2D graphics2D){
		square1.draw(graphics2D);
		square2.draw(graphics2D);
		square3.draw(graphics2D);
	}
	
	/**
	 * 获取角块的角点坐标。
	 * 通过求3个面的顶点集合的交集来找到公共角点。
	 * @return 角点的3D坐标
	 */
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
	
	/**
	 * 获取角块中距离中心点最远的顶点坐标。
	 * 遍历角块3个面的所有顶点，找到距离中心点最远的那个点。
	 * @param centerPoint 中心点坐标
	 * @return 距离中心点最远的顶点3D坐标
	 */
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

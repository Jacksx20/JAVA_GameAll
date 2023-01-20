import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import math3D.Point3D;
import math3D.VTs;


public class CenterBlock {

	private Square square;
	
	

	public Square getSquare() {
		return square;
	}

	public void setSquare(Square square) {
		this.square = square;
	}

	/**
	 * 中心块构造函数
	 * @param square
	 */
	public CenterBlock(Square square) {
		super();
		this.square = square;
	}
	
	/**
	 * 中心块绕经过(0,0,0)，point两点的直线旋转angle角度
	 * @param point：空间一点
	 * @param angle：旋转角度
	 * @param clockwise: true为顺时针，false为逆时针
	 */
	public void rotate(Point3D point, double angle, boolean clockwise){
		square.rotate(point, angle, clockwise);
	}
	
	/**
	 * 绕XY中旋转角度angle
	 * @param angle
	 */
	public void rotateXY(double angleX, double angleY) {
		square.rotateXY(angleX, angleY);
	}
	
	public Point3D getCenterPoint(){
		Point3D centerPoint=new Point3D();
		centerPoint.x=(square.getPoint1().x+square.getPoint3().x)/2;
		centerPoint.y=(square.getPoint1().y+square.getPoint3().y)/2;
		centerPoint.z=(square.getPoint1().z+square.getPoint3().z)/2;
		return centerPoint;
	}
	
	/**
	 * 获得中心块对应的整个面的2D投影
	 * @return
	 */
	public Polygon getPolygon(){
		Point3D point1=new Point3D(square.getPoint1().x, square.getPoint1().y, square.getPoint1().z);
		Point3D point2=new Point3D(square.getPoint2().x, square.getPoint2().y, square.getPoint2().z);
		Point3D point3=new Point3D(square.getPoint3().x, square.getPoint3().y, square.getPoint3().z);
		Point3D point4=new Point3D(square.getPoint4().x, square.getPoint4().y, square.getPoint4().z);
		Point point11=new Point();Point point22=new Point();
		Point point33=new Point();Point point44=new Point();
		point1.getPoint2D(point11);point2.getPoint2D(point22);
		point3.getPoint2D(point33);point4.getPoint2D(point44);
		VTs.view_To_screen(point11);VTs.view_To_screen(point22);
		VTs.view_To_screen(point33);VTs.view_To_screen(point44);
		int x[]={(int)point11.x,(int)point22.x,(int)point33.x,(int)point44.x};
		int y[]={(int)point11.y,(int)point22.y,(int)point33.y,(int)point44.y};
		Polygon polygon=new Polygon(x, y, 4);
		
		
		return polygon;
	}
	
	public void draw(Graphics2D graphics2D){
		square.draw(graphics2D);
	}
}

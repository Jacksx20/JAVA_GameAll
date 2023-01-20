import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import math3D.Point3D;
import math3D.VTs;
/**
 * 魔方面上的小正方形
 *
 */
public class Square {

	private Point3D point1;
	private Point3D point2;
	private Point3D point3;
	private Point3D point4;
	private Color color;
	private double Nx, Ny, Nz;
	
	public Point3D getPoint1() {
		return point1;
	}



	public void setPoint1(Point3D point1) {
		this.point1 = point1;
	}



	public Point3D getPoint2() {
		return point2;
	}



	public void setPoint2(Point3D point2) {
		this.point2 = point2;
	}



	public Point3D getPoint3() {
		return point3;
	}



	public void setPoint3(Point3D point3) {
		this.point3 = point3;
	}



	public Point3D getPoint4() {
		return point4;
	}



	public void setPoint4(Point3D point4) {
		this.point4 = point4;
	}

	private final int VIEWERROR=1000;
	
	/**
	 * 小方块构造函数
	 * @param point1
	 * @param point2
	 * @param point3
	 * @param point4
	 * @param color
	 */
	public Square(Point3D point1, Point3D point2, Point3D point3,
			Point3D point4, Color color) {
		super();
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
		this.point4 = point4;
		this.color = color;
	}




	/**
	 * 小方格绕经过(0,0,0)，point两点的直线旋转angle角度
	 * @param point：空间一点
	 * @param angle：旋转角度
	 * @param clockwise: true为顺时针，false为逆时针
	 */
	public void rotate(Point3D point, double angle, boolean clockwise){
		if (clockwise) {
			point1.rotateXYZ(point.x, point.y, point.z, -angle);
			point2.rotateXYZ(point.x, point.y, point.z, -angle);
			point3.rotateXYZ(point.x, point.y, point.z, -angle);
			point4.rotateXYZ(point.x, point.y, point.z, -angle);
		} else {
			point1.rotateXYZ(point.x, point.y, point.z, angle);
			point2.rotateXYZ(point.x, point.y, point.z, angle);
			point3.rotateXYZ(point.x, point.y, point.z, angle);
			point4.rotateXYZ(point.x, point.y, point.z, angle);
		}
	}
	
	/**
	 * 绕X中旋转角度angle
	 * @param angle
	 */
	public void rotateX(double angle) {
		point1.rotateX(angle);
		point2.rotateX(angle);
		point3.rotateX(angle);
		point4.rotateX(angle);
	}

	/**
	 * 绕Y中旋转角度angle
	 * @param angle
	 */

	public void rotateY(double angle) {
		point1.rotateY(angle);
		point2.rotateY(angle);
		point3.rotateY(angle);
		point4.rotateY(angle);
	}
	
	/**
	 * 绕XY中旋转角度angle
	 * @param angle
	 */
	public void rotateXY(double angleX, double angleY) {
		point1.rotateXY(angleX,angleY);
		point2.rotateXY(angleX,angleY);
		point3.rotateXY(angleX,angleY);
		point4.rotateXY(angleX,angleY);
	}
	
	private void vectNormal(){
		double Ux=point1.x-point2.x; double Uy=point1.y-point2.y; double Uz=point1.z-point2.z;
		  double Vx=point2.x-point3.x; double Vy=point2.y-point3.y; double Vz=point2.z-point3.z; 
		  
		  this.Nx=Uy*Vz - Uz*Vy;
		  this.Ny=Uz*Vx - Ux*Vz;
		  this.Nz=Ux*Vy - Uy*Vx;
	}
	
	public double getHidden() {
		vectNormal();
		return Nz;
	}
	
	public void draw(Graphics2D graphics2D){
		if (getHidden()>VIEWERROR) {
			Point p1=new Point();Point p2=new Point();
			Point p3=new Point();Point p4=new Point();
			point1.getPoint2D(p1);point2.getPoint2D(p2);
			point3.getPoint2D(p3);point4.getPoint2D(p4);
			VTs.view_To_screen(p1);VTs.view_To_screen(p2);
			VTs.view_To_screen(p3);VTs.view_To_screen(p4);
			
			graphics2D.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			graphics2D.setColor(Color.black);
			graphics2D.drawLine(p1.x, p1.y, p2.x, p2.y);
			graphics2D.drawLine(p2.x, p2.y, p3.x, p3.y);
			graphics2D.drawLine(p3.x, p3.y, p4.x, p4.y);
			graphics2D.drawLine(p4.x, p4.y, p1.x, p1.y);
			
			graphics2D.setColor(color);
			
			int X1234[] = { p1.x, p2.x, p3.x, p4.x };
			int Y1234[] = { p1.y, p2.y, p3.y, p4.y };
			graphics2D.fillPolygon(X1234, Y1234, 4);
		}
	}
}

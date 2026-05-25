import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import math3D.Point3D;
import math3D.VTs;

/**
 * Square类表示魔方面上的一个小正方形面。
 * 由4个3D顶点定义，具有颜色属性，支持3D旋转和2D投影绘制。
 */
public class Square {

	/** 正方形的4个3D顶点 */
	private Point3D point1;
	private Point3D point2;
	private Point3D point3;
	private Point3D point4;
	/** 正方形的颜色 */
	private Color color;
	/** 法向量分量，用于背面剔除判断 */
	private double Nx, Ny, Nz;

	/**
	 * 获取第1个顶点
	 * @return 第1个顶点的3D坐标
	 */
	public Point3D getPoint1() {
		return point1;
	}

	/**
	 * 设置第1个顶点
	 * @param point1 第1个顶点的3D坐标
	 */
	public void setPoint1(Point3D point1) {
		this.point1 = point1;
	}

	/**
	 * 获取第2个顶点
	 * @return 第2个顶点的3D坐标
	 */
	public Point3D getPoint2() {
		return point2;
	}

	/**
	 * 设置第2个顶点
	 * @param point2 第2个顶点的3D坐标
	 */
	public void setPoint2(Point3D point2) {
		this.point2 = point2;
	}

	/**
	 * 获取第3个顶点
	 * @return 第3个顶点的3D坐标
	 */
	public Point3D getPoint3() {
		return point3;
	}

	/**
	 * 设置第3个顶点
	 * @param point3 第3个顶点的3D坐标
	 */
	public void setPoint3(Point3D point3) {
		this.point3 = point3;
	}

	/**
	 * 获取第4个顶点
	 * @return 第4个顶点的3D坐标
	 */
	public Point3D getPoint4() {
		return point4;
	}

	/**
	 * 设置第4个顶点
	 * @param point4 第4个顶点的3D坐标
	 */
	public void setPoint4(Point3D point4) {
		this.point4 = point4;
	}

	/** 可见性判断阈值，法向量Z分量大于此值时认为面朝向观察者 */
	private final int VIEWERROR=1000;
	
	/**
	 * 小正方形构造函数
	 * @param point1 第1个顶点
	 * @param point2 第2个顶点
	 * @param point3 第3个顶点
	 * @param point4 第4个顶点
	 * @param color  面的颜色
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
	 * 小正方形绕经过(0,0,0)和point的直线旋转angle角度。
	 * 顺时针时取负角度，逆时针时取正角度。
	 * @param point 空间一点，确定旋转轴方向
	 * @param angle 旋转角度（弧度）
	 * @param clockwise true为顺时针，false为逆时针
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
	 * 绕X轴旋转指定角度
	 * @param angle 旋转角度（弧度）
	 */
	public void rotateX(double angle) {
		point1.rotateX(angle);
		point2.rotateX(angle);
		point3.rotateX(angle);
		point4.rotateX(angle);
	}

	/**
	 * 绕Y轴旋转指定角度
	 * @param angle 旋转角度（弧度）
	 */
	public void rotateY(double angle) {
		point1.rotateY(angle);
		point2.rotateY(angle);
		point3.rotateY(angle);
		point4.rotateY(angle);
	}
	
	/**
	 * 绕XY轴旋转指定角度（先绕X轴再绕Y轴）
	 * @param angleX 绕X轴旋转的角度
	 * @param angleY 绕Y轴旋转的角度
	 */
	public void rotateXY(double angleX, double angleY) {
		point1.rotateXY(angleX,angleY);
		point2.rotateXY(angleX,angleY);
		point3.rotateXY(angleX,angleY);
		point4.rotateXY(angleX,angleY);
	}
	
	/**
	 * 计算面的法向量。
	 * 通过两个边向量的叉积得到法向量(Nx, Ny, Nz)。
	 */
	private void vectNormal(){
		double Ux=point1.x-point2.x; double Uy=point1.y-point2.y; double Uz=point1.z-point2.z;
		double Vx=point2.x-point3.x; double Vy=point2.y-point3.y; double Vz=point2.z-point3.z; 
		  
		this.Nx=Uy*Vz - Uz*Vy;
		this.Ny=Uz*Vx - Ux*Vz;
		this.Nz=Ux*Vy - Uy*Vx;
	}
	
	/**
	 * 获取面的隐藏值（法向量Z分量），用于背面剔除。
	 * 值越大表示面越朝向观察者。
	 * @return 法向量的Z分量
	 */
	public double getHidden() {
		vectNormal();
		return Nz;
	}
	
	/**
	 * 绘制小正方形面。
	 * 仅当面的法向量Z分量大于VIEWERROR时才绘制（背面剔除）。
	 * 先绘制黑色边框，再填充面的颜色。
	 * @param graphics2D 图形绘制上下文
	 */
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

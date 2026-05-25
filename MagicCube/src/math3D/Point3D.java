package math3D;

import java.awt.Point;

/**
 * Point3D类表示三维空间中的点，实现了Cloneable接口。
 * 提供3D旋转（绕X/Y/Z轴及任意轴）、透视投影到2D等功能。
 */
public class Point3D implements Cloneable {

	/** X坐标 */
	public double x;
	/** Y坐标 */
	public double y;
	/** Z坐标 */
	public double z;
	/** 透视投影的距离参数 */
	private double d;

	/**
	 * 默认构造函数，创建原点(0,0,0)。
	 */
	public Point3D() {
		super();
	}

	/**
	 * 带参数的构造函数，创建指定坐标的3D点。
	 * 
	 * @param x X坐标
	 * @param y Y坐标
	 * @param z Z坐标
	 */
	public Point3D(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * 将3D点透视投影到2D点，结果存入传入的Point对象。
	 * 使用透视投影公式，视距由视角80度和屏幕尺寸2000计算。
	 * 
	 * @param point 用于存储2D投影结果的Point对象
	 */
	public void getPoint2D(Point point) {
		d = ((double) 2000 / 2) / (double) Math.tan(80 / 2); // 视距2000
		point.x = (int) ((d * this.x) / (d - (-z)));
		point.y = (int) ((d * this.y) / (d - (-z)));
	}

	/**
	 * 将3D点透视投影到2D点，返回新的Point对象。
	 * 使用透视投影公式，视距由视角80度和屏幕尺寸2000计算。
	 * 
	 * @return 2D投影的Point对象
	 */
	public Point getPoint2D() {
		Point point = new Point();
		d = ((double) 2000 / 2) / (double) Math.tan(80 / 2); // 视距2000
		point.x = (int) ((d * this.x) / (d - (-z)));
		point.y = (int) ((d * this.y) / (d - (-z)));
		return point;
	}

	/**
	 * 绕X轴旋转指定角度。
	 * 使用旋转矩阵：y'=y*cos-y*z*sin, z'=y*sin+z*cos
	 * 
	 * @param angle 旋转角度（弧度）
	 */
	public void rotateX(double angle) {
		double sinAngle = (double) Math.sin(angle);
		double cosAngle = (double) Math.cos(angle);
		double y = this.y;
		double z = this.z;
		this.y = y * cosAngle - z * sinAngle;
		this.z = y * sinAngle + z * cosAngle;
	}

	/**
	 * 绕Y轴旋转指定角度。
	 * 使用旋转矩阵：z'=z*cos-x*sin, x'=z*sin+x*cos
	 * 
	 * @param angle 旋转角度（弧度）
	 */
	public void rotateY(double angle) {
		double sinAngle = (double) Math.sin(angle);
		double cosAngle = (double) Math.cos(angle);
		double z = this.z;
		double x = this.x;
		this.z = z * cosAngle - x * sinAngle;
		this.x = z * sinAngle + x * cosAngle;
	}

	/**
	 * 绕Z轴旋转指定角度。
	 * 使用旋转矩阵：x'=x*cos-y*sin, y'=x*sin+y*cos
	 * 
	 * @param angle 旋转角度（弧度）
	 */
	public void rotateZ(double angle) {
		double sinAngle = (double) Math.sin(angle);
		double cosAngle = (double) Math.cos(angle);
		double x = this.x;
		double y = this.y;
		this.x = x * cosAngle - y * sinAngle;
		this.y = x * sinAngle + y * cosAngle;
	}

	/**
	 * 先绕X轴再绕Y轴旋转指定角度。
	 * 
	 * @param angleX 绕X轴旋转的角度
	 * @param angleY 绕Y轴旋转的角度
	 */
	public void rotateXY(double angleX, double angleY) {
		rotateX(angleX);
		rotateY(angleY);
	}

	/**
	 * 绕经过原点(0,0,0)和点(x,y,z)的直线旋转角度angle。
	 * 使用罗德里格斯旋转公式(Rodrigues' rotation formula)的矩阵形式：
	 * 将任意轴旋转转换为3x3旋转矩阵与坐标向量的乘积。
	 * 
	 * @param x     旋转轴方向向量的X分量
	 * @param y     旋转轴方向向量的Y分量
	 * @param z     旋转轴方向向量的Z分量
	 * @param angle 旋转角度（弧度）
	 */
	public void rotateXYZ(double x, double y, double z, double angle) {
		/*
		 * double angleX=Math.acos(y/Math.sqrt(y*y+z*z));
		 * double angleZ=Math.acos(Math.sqrt(y*y+z*z)/Math.sqrt(x*x+y*y+z*z));
		 * rotateX(-angleX);
		 * rotateZ(angleZ);
		 * rotateY(angle);
		 * rotateZ(-angleZ);
		 * rotateX(angleX);
		 */
		double cosAngle = Math.cos(angle);
		double sinAngle = Math.sin(angle);
		double r = Math.sqrt(x * x + y * y + z * z);
		double ax = x / r, ay = y / r, az = z / r;

		// 旋转矩阵的9个元素
		double m11 = ax * ax * (1 - cosAngle) + cosAngle;
		double m12 = ay * ax * (1 - cosAngle) + az * sinAngle;
		double m13 = az * ax * (1 - cosAngle) - ay * sinAngle;

		double m21 = ax * ay * (1 - cosAngle) - az * sinAngle;
		double m22 = ay * ay * (1 - cosAngle) + cosAngle;
		double m23 = az * ay * (1 - cosAngle) + ax * sinAngle;

		double m31 = ax * az * (1 - cosAngle) + ay * sinAngle;
		double m32 = ay * az * (1 - cosAngle) - ax * sinAngle;
		double m33 = az * az * (1 - cosAngle) + cosAngle;

		// 矩阵乘法：新坐标 = 旋转矩阵 * 原坐标
		double x1 = this.x;
		double y1 = this.y;
		double z1 = this.z;
		this.x = x1 * m11 + y1 * m21 + z1 * m31;
		this.y = x1 * m12 + y1 * m22 + z1 * m32;
		this.z = x1 * m13 + y1 * m23 + z1 * m33;
	}

	/**
	 * 克隆当前3D点，创建一个坐标相同的新Point3D对象。
	 * 
	 * @return 克隆的Point3D对象
	 */
	@Override
	public Point3D clone() {
		Point3D point3D = new Point3D();
		try {// 调用Object的clone方法
			point3D = (Point3D) super.clone();
			point3D.x = this.x;
			point3D.y = this.y;
			point3D.z = this.z;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return point3D;
	}
}

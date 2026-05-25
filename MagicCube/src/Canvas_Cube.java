import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import math3D.Point3D;
import math3D.VTs;

/**
 * 三阶魔方（Rubik's Cube）的3D渲染画布类。
 * <p>
 * 该类继承自 {@link JPanel}，在面板上绘制并管理一个完整的三阶魔方。
 * 魔方由 8 个角块（{@link CornerBlock}）、12 个棱块（{@link EdgeBlock}）和 6 个中心块（{@link CenterBlock}）组成，
 * 共有 6 个面：蓝色（前）、红色（右）、绿色（后）、橙色（左）、黄色（上）、白色（下）。
 * </p>
 * <p>
 * 功能概述：
 * <ul>
 *   <li>3D 透视渲染：通过 {@link math3D.VTs} 将三维坐标投影到二维屏幕</li>
 *   <li>面选择：鼠标点击检测哪个面被选中（用于后续旋转操作）</li>
 *   <li>整体旋转：鼠标拖拽旋转整个魔方视角</li>
 *   <li>面旋转动画：支持 6 个面分别顺/逆时针旋转 90°，带逐帧动画效果</li>
 *   <li>深度排序绘制：根据面朝向判断绘制顺序，保证正确的遮挡关系</li>
 *   <li>双缓冲：重写 {@link #update(Graphics)} 实现双缓冲消除闪烁</li>
 * </ul>
 * </p>
 * <p>
 * 块编号约定（block 数组，block[0] 不使用）：
 * <pre>
 *   索引  1~8  : 蓝面上的块（4角块 + 4棱块）
 *   索引  9~12 : 赤道上的棱块（黄橙、黄红、白橙、白红）
 *   索引 13~20 : 绿面上的块（4角块 + 4棱块）
 * </pre>
 * </p>
 *
 * @see CornerBlock
 * @see EdgeBlock
 * @see CenterBlock
 * @see math3D.VTs
 */
public class Canvas_Cube extends JPanel{


	/** 序列化版本UID */
	private static final long serialVersionUID = 1L;

	/**
	 * 视图误差阈值，用于判断面是否面向观察者。
	 * 当面的 hidden 值大于此阈值时，认为该面背对观察者（不可见）。
	 */
	private final int VIEWERROR=1000;

	/**
	 * 旋转状态标志。
	 * true 表示正在执行面旋转动画，此时禁止其他旋转操作和整体拖拽旋转。
	 */
	private boolean rotating=false;
	
	/**
	 * 判断当前是否正在执行面旋转动画。
	 * @return true 表示正在旋转，false 表示空闲
	 */
	public boolean isRotating() {
		return rotating;
	}

	/**
	 * 设置面旋转动画状态。
	 * @param rotating true 表示正在旋转，false 表示空闲
	 */
	public void setRotating(boolean rotating) {
		this.rotating = rotating;
	}

	/**
	 * 背面多边形，用于在旋转动画期间填充旋转面的背面区域（灰色），
	 * 避免旋转过程中出现镂空效果。
	 */
	private Polygon backPolygon=new Polygon();
	
	/** 蓝色面标识常量（前面，z=+120） */
	public final String BLUE="blue";
	/** 红色面标识常量（右面，x=+120） */
	public final String RED="red";
	/** 绿色面标识常量（后面，z=-120） */
	public final String GREEN="green";
	/** 橙色面标识常量（左面，x=-120） */
	public final String ORANGE="orange";
	/** 黄色面标识常量（上面，y=+120） */
	public final String YELLOW="yellow";
	/** 白色面标识常量（下面，y=-120） */
	public final String WHITE="white";

	/**
	 * 当前被选中的面标识。
	 * 值为 BLUE/RED/GREEN/ORANGE/YELLOW/WHITE 或 null（未选中）。
	 * 鼠标点击某面时设置，用于确定后续面旋转操作的目标面。
	 */
	public String selected=null;
	
	/** 自定义橙色（RGB: 255,100,0），标准 Java Color.orange 色值不同 */
	private final Color colorOrange=new Color(255,100,0);
	/** 自定义黄色（RGB: 255,255,100），比标准 Color.yellow 更亮 */
	private final Color colorYellow=new Color(255,255,100);

	// ==================== 魔方的8个顶点 ====================
	// 顶点命名约定：a~h 对应魔方的8个角，坐标范围 [-120, 120]
	// a: 左上前（x-, y+, z+）  b: 左上后（x-, y+, z-）
	// c: 右上后（x+, y+, z-）  d: 右上前（x+, y+, z+）
	// e: 左下前（x-, y-, z+）  f: 左下后（x-, y-, z-）
	// g: 右下后（x+, y-, z-）  h: 右下前（x+, y-, z+）

	/** 顶点 a：左上前角 (-120, +120, +120) */
	private Point3D a=new Point3D(-120, 120, 120);
	/** 顶点 b：左上后角 (-120, +120, -120) */
	private Point3D b=new Point3D(-120, 120, -120);
	/** 顶点 c：右上后角 (+120, +120, -120) */
	private Point3D c=new Point3D(120, 120, -120);
	/** 顶点 d：右上前角 (+120, +120, +120) */
	private Point3D d=new Point3D(120, 120, 120);
	/** 顶点 e：左下前角 (-120, -120, +120) */
	private Point3D e=new Point3D(-120, -120, 120);
	/** 顶点 f：左下后角 (-120, -120, -120) */
	private Point3D f=new Point3D(-120, -120, -120);
	/** 顶点 g：右下后角 (+120, -120, -120) */
	private Point3D g=new Point3D(120, -120, -120);
	/** 顶点 h：右下前角 (+120, -120, +120) */
	private Point3D h=new Point3D(120, -120, 120);
	
	// ==================== 各棱上的三等分点 ====================
	// 棱上的点命名约定：两个顶点字母组合表示该棱，如 ad 表示从 a 到 d 方向距 a 三分之一处的点，
	// da 表示从 d 到 a 方向距 d 三分之一处的点。这些点用于划分角块和棱块的边界。

	// --- 前面（蓝色面，z=+120）棱上的点 ---
	/** 前面左棱上：a 向 d 方向 1/3 处 (-40, +120, +120) */
	private Point3D ad=new Point3D(-40, 120, 120);
	/** 前面左棱上：d 向 a 方向 1/3 处 (+40, +120, +120) */
	private Point3D da=new Point3D(40, 120, 120);
	/** 前面右棱上：d 向 h 方向 1/3 处 (+120, +40, +120) */
	private Point3D dh=new Point3D(120, 40, 120);
	/** 前面右棱上：h 向 d 方向 1/3 处 (+120, -40, +120) */
	private Point3D hd=new Point3D(120, -40, 120);
	/** 前面下棱上：h 向 e 方向 1/3 处 (+40, -120, +120) */
	private Point3D he=new Point3D(40, -120, 120);
	/** 前面下棱上：e 向 h 方向 1/3 处 (-40, -120, +120) */
	private Point3D eh=new Point3D(-40, -120, 120);
	/** 前面左棱上：e 向 a 方向 1/3 处 (-120, -40, +120) */
	private Point3D ea=new Point3D(-120, -40, 120);
	/** 前面左棱上：a 向 e 方向 1/3 处 (-120, +40, +120) */
	private Point3D ae=new Point3D(-120, 40, 120);
	
	// --- 后面（绿色面，z=-120）棱上的点 ---
	/** 后面左棱上：b 向 c 方向 1/3 处 (-40, +120, -120) */
	private Point3D bc=new Point3D(-40, 120, -120);
	/** 后面左棱上：c 向 b 方向 1/3 处 (+40, +120, -120) */
	private Point3D cb=new Point3D(40, 120, -120);
	/** 后面右棱上：c 向 g 方向 1/3 处 (+120, +40, -120) */
	private Point3D cg=new Point3D(120, 40, -120);
	/** 后面右棱上：g 向 c 方向 1/3 处 (+120, -40, -120) */
	private Point3D gc=new Point3D(120, -40, -120);
	/** 后面下棱上：g 向 f 方向 1/3 处 (+40, -120, -120) */
	private Point3D gf=new Point3D(40, -120, -120);
	/** 后面下棱上：f 向 g 方向 1/3 处 (-40, -120, -120) */
	private Point3D fg=new Point3D(-40, -120, -120);
	/** 后面左棱上：f 向 b 方向 1/3 处 (-120, -40, -120) */
	private Point3D fb=new Point3D(-120, -40, -120);
	/** 后面左棱上：b 向 f 方向 1/3 处 (-120, +40, -120) */
	private Point3D bf=new Point3D(-120, 40, -120);
	
	// --- 左面（橙色面，x=-120）竖棱上的点 ---
	/** 左面竖棱上：a 向 b 方向 1/3 处 (-120, +120, +40) */
	private Point3D ab=new Point3D(-120, 120, 40);
	/** 左面竖棱上：b 向 a 方向 1/3 处 (-120, +120, -40) */
	private Point3D ba=new Point3D(-120, 120, -40);
	/** 左面竖棱上：e 向 f 方向 1/3 处 (-120, -120, +40) */
	private Point3D ef=new Point3D(-120, -120, 40);
	/** 左面竖棱上：f 向 e 方向 1/3 处 (-120, -120, -40) */
	private Point3D fe=new Point3D(-120, -120, -40);
	
	// --- 右面（红色面，x=+120）竖棱上的点 ---
	/** 右面竖棱上：d 向 c 方向 1/3 处 (+120, +120, +40) */
	private Point3D dc=new Point3D(120, 120, 40);
	/** 右面竖棱上：c 向 d 方向 1/3 处 (+120, +120, -40) */
	private Point3D cd=new Point3D(120, 120, -40);
	/** 右面竖棱上：h 向 g 方向 1/3 处 (+120, -120, +40) */
	private Point3D hg=new Point3D(120, -120, 40);
	/** 右面竖棱上：g 向 h 方向 1/3 处 (+120, -120, -40) */
	private Point3D gh=new Point3D(120, -120, -40);
	
	// ==================== 各中心块（面中心 3x3 的小正方形）的顶点 ====================

	// --- 黄色面中心块顶点（上面，y=+120）---
	/** 黄色中心块左前顶点 (-40, +120, +40) */
	private Point3D yellow_a=new Point3D(-40, 120, 40);
	/** 黄色中心块左后顶点 (-40, +120, -40) */
	private Point3D yellow_b=new Point3D(-40, 120, -40);
	/** 黄色中心块右后顶点 (+40, +120, -40) */
	private Point3D yellow_c=new Point3D(40, 120, -40);
	/** 黄色中心块右前顶点 (+40, +120, +40) */
	private Point3D yellow_d=new Point3D(40, 120, 40);
	
	// --- 白色面中心块顶点（下面，y=-120）---
	/** 白色中心块左前顶点 (-40, -120, +40) */
	private Point3D white_e=new Point3D(-40, -120, 40);
	/** 白色中心块左后顶点 (-40, -120, -40) */
	private Point3D white_f=new Point3D(-40, -120, -40);
	/** 白色中心块右后顶点 (+40, -120, -40) */
	private Point3D white_g=new Point3D(40, -120, -40);
	/** 白色中心块右前顶点 (+40, -120, +40) */
	private Point3D white_h=new Point3D(40, -120, 40);
	
	// --- 橙色面中心块顶点（左面，x=-120）---
	/** 橙色中心块上前顶点 (-120, +40, +40) */
	private Point3D orange_a=new Point3D(-120, 40, 40);
	/** 橙色中心块上后顶点 (-120, +40, -40) */
	private Point3D orange_b=new Point3D(-120, 40, -40);
	/** 橙色中心块下后顶点 (-120, -40, -40) */
	private Point3D orange_f=new Point3D(-120, -40, -40);
	/** 橙色中心块下前顶点 (-120, -40, +40) */
	private Point3D orange_e=new Point3D(-120, -40, 40);
	
	// --- 红色面中心块顶点（右面，x=+120）---
	/** 红色中心块上前顶点 (+120, +40, +40) */
	private Point3D red_d=new Point3D(120, 40, 40);
	/** 红色中心块上后顶点 (+120, +40, -40) */
	private Point3D red_c=new Point3D(120, 40, -40);
	/** 红色中心块下后顶点 (+120, -40, -40) */
	private Point3D red_g=new Point3D(120, -40, -40);
	/** 红色中心块下前顶点 (+120, -40, +40) */
	private Point3D red_h=new Point3D(120, -40, 40);
	
	// --- 蓝色面中心块顶点（前面，z=+120）---
	/** 蓝色中心块左上顶点 (-40, +40, +120) */
	private Point3D blue_a=new Point3D(-40, 40, 120);
	/** 蓝色中心块右上顶点 (+40, +40, +120) */
	private Point3D blue_d=new Point3D(40, 40, 120);
	/** 蓝色中心块右下顶点 (+40, -40, +120) */
	private Point3D blue_h=new Point3D(40, -40, 120);
	/** 蓝色中心块左下顶点 (-40, -40, +120) */
	private Point3D blue_e=new Point3D(-40, -40, 120);
	
	// --- 绿色面中心块顶点（后面，z=-120）---
	/** 绿色中心块左上顶点 (-40, +40, -120) */
	private Point3D green_b=new Point3D(-40, 40, -120);
	/** 绿色中心块右上顶点 (+40, +40, -120) */
	private Point3D green_c=new Point3D(40, 40, -120);
	/** 绿色中心块右下顶点 (+40, -40, -120) */
	private Point3D green_g=new Point3D(40, -40, -120);
	/** 绿色中心块左下顶点 (-40, -40, -120) */
	private Point3D green_f=new Point3D(-40, -40, -120);
	
	// ==================== 8个角块 ====================
	// 每个角块由 3 个可见面（Square）组成，clone() 防止顶点共享导致旋转时相互影响

	/**
	 * 角块 a：左上前角。
	 * 包含蓝色面、黄色面、橙色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_a=new CornerBlock(new Square(a.clone(), ae.clone(), 
															blue_a.clone(), ad.clone(), Color.blue),
													  new Square(a.clone(), ad.clone(), 
															yellow_a.clone(), ab.clone(), colorYellow), 
													  new Square(a.clone(), ab.clone(), 
															orange_a.clone(), ae.clone(), colorOrange));

	/**
	 * 角块 b：左上后角。
	 * 包含黄色面、橙色面、绿色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_b=new CornerBlock(new Square(b.clone(), ba.clone(), 
															yellow_b.clone(), bc.clone(), colorYellow),
													  new Square(b.clone(), bf.clone(), 
															orange_b.clone(), ba.clone(), colorOrange), 
													  new Square(b.clone(), bc.clone(), 
															green_b.clone(), bf.clone(), Color.green));

	/**
	 * 角块 c：右上后角。
	 * 包含黄色面、红色面、绿色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_c=new CornerBlock(new Square(c.clone(), cb.clone(), 
															yellow_c.clone(), cd.clone(), colorYellow),
													  new Square(c.clone(), cd.clone(), 
															red_c.clone(), cg.clone(), Color.red), 
													  new Square(c.clone(), cg.clone(), 
															green_c.clone(), cb.clone(), Color.green));

	/**
	 * 角块 d：右上前角。
	 * 包含黄色面、红色面、蓝色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_d=new CornerBlock(new Square(d.clone(), dc.clone(), 
															yellow_d.clone(), da.clone(), colorYellow),
													  new Square(d.clone(), dh.clone(), 
															red_d.clone(), dc.clone(), Color.red), 
													  new Square(d.clone(), da.clone(), 
															blue_d.clone(), dh.clone(), Color.blue));

	/**
	 * 角块 e：左下前角。
	 * 包含白色面、橙色面、蓝色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_e=new CornerBlock(new Square(e.clone(), ef.clone(), 
															white_e.clone(), eh.clone(), Color.white),
													  new Square(e.clone(), ea.clone(), 
															orange_e.clone(), ef.clone(), colorOrange), 
													  new Square(e.clone(), eh.clone(), 
															blue_e.clone(), ea.clone(), Color.blue));

	/**
	 * 角块 f：左下后角。
	 * 包含白色面、橙色面、绿色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_f=new CornerBlock(new Square(f.clone(), fg.clone(), 
															white_f.clone(), fe.clone(), Color.white),
													  new Square(f.clone(), fe.clone(), 
															orange_f.clone(), fb.clone(), colorOrange), 
													  new Square(f.clone(), fb.clone(), 
															green_f.clone(), fg.clone(), Color.green));

	/**
	 * 角块 g：右下后角。
	 * 包含白色面、绿色面、红色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_g=new CornerBlock(new Square(g.clone(), gh.clone(), 
															white_g.clone(), gf.clone(), Color.white),
													  new Square(g.clone(), gf.clone(), 
															green_g.clone(), gc.clone(), Color.green), 
													  new Square(g.clone(), gc.clone(), 
															red_g.clone(), gh.clone(), Color.red));

	/**
	 * 角块 h：右下前角。
	 * 包含白色面、红色面、蓝色面的 3 个正方形。
	 */
	private CornerBlock cornerBlock_h=new CornerBlock(new Square(h.clone(), he.clone(), 
															white_h.clone(), hg.clone(), Color.white),
													  new Square(h.clone(), hg.clone(), 
															red_h.clone(), hd.clone(), Color.red), 
													  new Square(h.clone(), hd.clone(), 
															blue_h.clone(), he.clone(), Color.blue));
	
	// ==================== 12个棱块 ====================
	// 每个棱块由 2 个可见面（Square）组成，命名约定：颜色1_颜色2 表示两相邻面的棱

	/** 棱块：蓝-黄交界棱（前面上方），由蓝色面和黄色面组成 */
	private EdgeBlock Blue_Yellow=new EdgeBlock(new Square(da.clone(), ad.clone(), 
														blue_a.clone(), blue_d.clone(), Color.blue),
												new Square(ad.clone(), da.clone(), 
														yellow_d.clone(), yellow_a.clone(), colorYellow));

	/** 棱块：黄-绿交界棱（上面后方），由黄色面和绿色面组成 */
	private EdgeBlock Yellow_Green=new EdgeBlock(new Square(cb.clone(), bc.clone(), 
														yellow_b.clone(), yellow_c.clone(), colorYellow),
												new Square(bc.clone(), cb.clone(), 
														green_c.clone(), green_b.clone(), Color.green));

	/** 棱块：绿-白交界棱（后面下方），由绿色面和白色面组成 */
	private EdgeBlock Green_White=new EdgeBlock(new Square(gf.clone(), fg.clone(), 
														green_f.clone(), green_g.clone(), Color.green),
												new Square(fg.clone(), gf.clone(), 
														white_g.clone(), white_f.clone(), Color.white));

	/** 棱块：白-蓝交界棱（下面前方），由白色面和蓝色面组成 */
	private EdgeBlock White_Blue=new EdgeBlock(new Square(he.clone(), eh.clone(), 
														white_e.clone(), white_h.clone(), Color.white),
												new Square(eh.clone(), he.clone(), 
														blue_h.clone(), blue_e.clone(), Color.blue));

	/** 棱块：黄-橙交界棱（上面左方），由黄色面和橙色面组成 */
	private EdgeBlock Yellow_Orange=new EdgeBlock(new Square(ba.clone(), ab.clone(), 
														yellow_a.clone(), yellow_b.clone(), colorYellow),
												new Square(ab.clone(), ba.clone(), 
														orange_b.clone(), orange_a.clone(), colorOrange));

	/** 棱块：黄-红交界棱（上面右方），由黄色面和红色面组成 */
	private EdgeBlock Yellow_Red=new EdgeBlock(new Square(dc.clone(), cd.clone(), 
														yellow_c.clone(), yellow_d.clone(), colorYellow),
												new Square(cd.clone(), dc.clone(), 
														red_d.clone(), red_c.clone(), Color.red));

	/** 棱块：红-白交界棱（右面下方），由红色面和白色面组成 */
	private EdgeBlock Red_White=new EdgeBlock(new Square(hg.clone(), gh.clone(), 
														red_g.clone(), red_h.clone(), Color.red),
												new Square(gh.clone(), hg.clone(), 
														white_h.clone(), white_g.clone(), Color.white));

	/** 棱块：白-橙交界棱（下面左方），由白色面和橙色面组成 */
	private EdgeBlock White_Orange=new EdgeBlock(new Square(ef.clone(), fe.clone(), 
														white_f.clone(), white_e.clone(), Color.white),
												new Square(fe.clone(), ef.clone(), 
														orange_e.clone(), orange_f.clone(), colorOrange));

	/** 棱块：橙-蓝交界棱（左面前方），由橙色面和蓝色面组成 */
	private EdgeBlock Orange_Blue=new EdgeBlock(new Square(ea.clone(), ae.clone(), 
														orange_a.clone(), orange_e.clone(), colorOrange),
												new Square(ae.clone(), ea.clone(), 
														blue_e.clone(), blue_a.clone(), Color.blue));

	/** 棱块：橙-绿交界棱（左面后方），由橙色面和绿色面组成 */
	private EdgeBlock Orange_Green=new EdgeBlock(new Square(bf.clone(), fb.clone(), 
														orange_f.clone(), orange_b.clone(), colorOrange),
												new Square(fb.clone(), bf.clone(), 
														green_b.clone(), green_f.clone(), Color.green));

	/** 棱块：红-绿交界棱（右面后方），由红色面和绿色面组成 */
	private EdgeBlock Red_Green=new EdgeBlock(new Square(gc.clone(), cg.clone(), 
														red_c.clone(), red_g.clone(), Color.red),
												new Square(cg.clone(), gc.clone(), 
														green_g.clone(), green_c.clone(), Color.green));

	/** 棱块：红-蓝交界棱（右面前方），由红色面和蓝色面组成 */
	private EdgeBlock Red_Blue=new EdgeBlock(new Square(dh.clone(), hd.clone(), 
														red_h.clone(), red_d.clone(), Color.red),
												new Square(hd.clone(), dh.clone(), 
														blue_d.clone(), blue_h.clone(), Color.blue));
	
	// ==================== 6个中心块 ====================
	// 每个中心块只有 1 个面（Square），位于面的正中央

	/** 蓝色面中心块（前面，z=+120） */
	private CenterBlock blue=new CenterBlock(new Square(blue_a.clone(), blue_e.clone(), blue_h.clone(), blue_d.clone(), Color.blue));
	/** 红色面中心块（右面，x=+120） */
	private CenterBlock red=new CenterBlock(new Square(red_g.clone(), red_c.clone(), red_d.clone(), red_h.clone(), Color.red));
	/** 绿色面中心块（后面，z=-120） */
	private CenterBlock green=new CenterBlock(new Square(green_b.clone(), green_c.clone(), green_g.clone(), green_f.clone(), Color.green));
	/** 橙色面中心块（左面，x=-120） */
	private CenterBlock orange=new CenterBlock(new Square(orange_a.clone(), orange_b.clone(), orange_f.clone(), orange_e.clone(), colorOrange));
	/** 黄色面中心块（上面，y=+120） */
	private CenterBlock yellow=new CenterBlock(new Square(yellow_a.clone(), yellow_d.clone(), yellow_c.clone(), yellow_b.clone(), colorYellow));
	/** 白色面中心块（下面，y=-120） */
	private CenterBlock white=new CenterBlock(new Square(white_e.clone(), white_f.clone(), white_g.clone(), white_h.clone(), Color.white));
	
	/** 鼠标拖拽时记录的上一次鼠标位置，用于计算拖拽增量 */
	Point oldPoint;

	/**
	 * 绕 X 轴和 Y 轴的旋转角度增量（弧度）。
	 * 由鼠标拖拽水平/垂直位移计算得出，用于整体旋转魔方视角。
	 */
	double angleX,angleY;
	  
	/** 双缓冲用离屏图像，在 {@link #update(Graphics)} 中创建和绘制 */
	private Image offScreenImage;
	
	/** 6 个中心块数组，便于批量遍历操作 */
	private CenterBlock[] centerBlocks={blue,red,green,orange,yellow,white};;
	/** 8 个角块数组，便于批量遍历操作 */
	private CornerBlock[] cornerBlocks={cornerBlock_a,cornerBlock_b,cornerBlock_c,cornerBlock_d,
										cornerBlock_e,cornerBlock_f,cornerBlock_g,cornerBlock_h};
	/** 12 个棱块数组，便于批量遍历操作 */
	private EdgeBlock[] edgeBlocks={Blue_Yellow,Yellow_Green,Green_White,White_Blue,Yellow_Orange,Yellow_Red,
									Red_White,White_Orange,Orange_Blue,Orange_Green,Red_Green,Red_Blue};

	/**
	 * 魔方块编号数组，block[0] 不使用。
	 * 索引 1~8 为蓝面上的块（4角+4棱），索引 9~12 为赤道棱块，索引 13~20 为绿面上的块。
	 * 旋转操作后通过交换数组中的引用来更新块的位置关系。
	 */
	private Object[] block=new Object[21];

	/**
	 * 旋转部分块数组（block1）。
	 * 存储当前选中面上的 9 个块（4角+4棱+1中心），这些块在面旋转时会一起转动。
	 */
	private Object[] block1=new Object[9];

	/**
	 * 未旋转部分块数组（block2）。
	 * 存储不属于当前选中面的 17 个块（4角+4棱+5中心），这些块在面旋转时保持静止。
	 */
	private Object[] block2=new Object[17];
	
	/**
	 * 构造方法：初始化画布，注册鼠标监听器，设置背景色，并按照编号填充 block 数组。
	 * <p>
	 * block 数组的编号方案：
	 * <pre>
	 *   1:cornerBlock_a  2:Blue_Yellow   3:cornerBlock_d  4:Orange_Blue
	 *   5:Red_Blue       6:cornerBlock_e 7:White_Blue     8:cornerBlock_h
	 *   9:Yellow_Orange  10:Yellow_Red   11:White_Orange  12:Red_White
	 *   13:cornerBlock_b 14:Yellow_Green 15:cornerBlock_c 16:Orange_Green
	 *   17:Red_Green     18:cornerBlock_f 19:Green_White  20:cornerBlock_g
	 * </pre>
	 * </p>
	 */
	public Canvas_Cube() {
		addMouseListener(new MyMouseListner());
	    addMouseMotionListener(new MyMouseMotionListner());
	    setBackground(Color.cyan);
	    
	    // 将各块按编号存入 block 数组（block[0] 留空不用）
	    block[1]=cornerBlock_a;block[2]=Blue_Yellow;block[3]=cornerBlock_d;block[4]=Orange_Blue;
	    block[5]=Red_Blue;block[6]=cornerBlock_e;block[7]=White_Blue;block[8]=cornerBlock_h;
	    block[9]=Yellow_Orange;block[10]=Yellow_Red;block[11]=White_Orange;block[12]=Red_White;
	    block[13]=cornerBlock_b;block[14]=Yellow_Green;block[15]=cornerBlock_c;block[16]=Orange_Green;
	    block[17]=Red_Green;block[18]=cornerBlock_f;block[19]=Green_White;block[20]=cornerBlock_g;
	}
	
	/**
	 * 蓝面旋转90度。
	 * <p>
	 * 该方法启动一个新线程执行逐帧旋转动画：
	 * <ul>
	 *   <li>每帧旋转角度为 π/90 弧度（约 2°），共执行 45 帧，总计 90°</li>
	 *   <li>旋转轴为蓝色面中心点的法线方向</li>
	 *   <li>旋转期间设置 rotating=true，禁止其他旋转操作</li>
	 *   <li>动画完成后更新 block 数组中块的位置引用</li>
	 * </ul>
	 * </p>
	 * <p>
	 * 蓝面上的块编号：角块 [1,3,6,8]，棱块 [2,4,5,7]，中心块 blue。
	 * 顺时针时角块轮换：1→6→8→3→1，棱块轮换：2→4→7→5→2。
	 * 逆时针时轮换方向相反。
	 * </p>
	 *
	 * @param clockWise true 为顺时针旋转；false 为逆时针旋转
	 */
	public void rotateBlue90(final Boolean clockWise){
		// 启动动画线程，逐帧旋转蓝色面上的所有块
		new Thread(new Runnable() {
			int count=0;
			@Override
			public void run() {
				setRotating(true);
				while (isRotating()) {
					// 旋转蓝面上的 4 个角块
					CornerBlock tempCornerBlock=(CornerBlock) block[1];
					tempCornerBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[3];
					tempCornerBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[6];
					tempCornerBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[8];
					tempCornerBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转蓝面上的 4 个棱块
					EdgeBlock tempEdgeBlock=(EdgeBlock) block[2];
					tempEdgeBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[4];
					tempEdgeBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[5];
					tempEdgeBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[7];
					tempEdgeBlock.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转蓝色中心块
					blue.rotate(blue.getCenterPoint(), Math.PI/90, clockWise);
					// 若蓝面可见（hidden < VIEWERROR），更新背面多边形用于填充
					if (blue.getSquare().getHidden()<VIEWERROR) {
						backPolygon=getPolygon1();
					}
					// 重绘画面
					update(getGraphics());
					count++;
					// 45 帧 × π/90 = π/2 = 90°，动画完成
					if (count>=45) {
						setRotating(false);
					}
				}
				// 旋转动画结束后短暂休眠，等待画面刷新稳定
				try{Thread.sleep(10);}
			      catch(InterruptedException e) {;}
			}
		}).start();
		// 动画线程启动后，立即更新 block 数组中的位置引用（逻辑位置与动画同步）
		if (clockWise) {
			// 顺时针：角块 1→6→8→3→1 循环替换，棱块 2→4→7→5→2 循环替换
			Object temp=block[1];
			block[1]=block[6];block[6]=block[8];block[8]=block[3];block[3]=temp;
			temp=block[2];
			block[2]=block[4];block[4]=block[7];block[7]=block[5];block[5]=temp;
		} else {
			// 逆时针：角块 1→3→8→6→1 循环替换，棱块 2→5→7→4→2 循环替换
			Object temp=block[1];
			block[1]=block[3];block[3]=block[8];block[8]=block[6];block[6]=temp;
			temp=block[2];
			block[2]=block[5];block[5]=block[7];block[7]=block[4];block[4]=temp;
		}
	}
	
	/**
	 * 橙面旋转90度。
	 * <p>
	 * 橙面上的块编号：角块 [13,1,18,6]，棱块 [9,16,4,11]，中心块 orange。
	 * 顺时针时角块轮换：13→18→6→1→13，棱块轮换：9→16→11→4→9。
	 * </p>
	 *
	 * @param clockWise true 为顺时针旋转；false 为逆时针旋转
	 */
	public void rotateOrange90(final Boolean clockWise){
		// 启动动画线程，逐帧旋转橙色面上的所有块
		new Thread(new Runnable() {
			int count=0;
			@Override
			public void run() {
				setRotating(true);
				while (isRotating()) {
					// 旋转橙面上的 4 个角块
					CornerBlock tempCornerBlock=(CornerBlock) block[13];
					tempCornerBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[1];
					tempCornerBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[18];
					tempCornerBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[6];
					tempCornerBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转橙面上的 4 个棱块
					EdgeBlock tempEdgeBlock=(EdgeBlock) block[9];
					tempEdgeBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[16];
					tempEdgeBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[4];
					tempEdgeBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[11];
					tempEdgeBlock.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转橙色中心块，更新背面多边形，重绘
					orange.rotate(orange.getCenterPoint(), Math.PI/90, clockWise);
					if (orange.getSquare().getHidden()<VIEWERROR) {
						backPolygon=getPolygon1();
					}
					update(getGraphics());
					count++;
					if (count>=45) {
						setRotating(false);
					}
				}
			}
			
		}).start();
		
		// 更新 block 数组中的位置引用
		if (clockWise) {
			// 顺时针：角块 13→18→6→1→13，棱块 9→16→11→4→9
			Object temp=block[13];
			block[13]=block[18];block[18]=block[6];block[6]=block[1];block[1]=temp;
			temp=block[9];
			block[9]=block[16];block[16]=block[11];block[11]=block[4];block[4]=temp;
		} else {
			// 逆时针：角块 13→1→6→18→13，棱块 9→4→11→16→9
			Object temp=block[13];
			block[13]=block[1];block[1]=block[6];block[6]=block[18];block[18]=temp;
			temp=block[9];
			block[9]=block[4];block[4]=block[11];block[11]=block[16];block[16]=temp;
		}
	}
	
	/**
	 * 绿面旋转90度。
	 * <p>
	 * 绿面上的块编号：角块 [15,13,20,18]，棱块 [14,17,16,19]，中心块 green。
	 * 顺时针时角块轮换：15→20→18→13→15，棱块轮换：14→17→19→16→14。
	 * </p>
	 *
	 * @param clockWise true 为顺时针旋转；false 为逆时针旋转
	 */
	public void rotateGreen90(final Boolean clockWise){
		// 启动动画线程，逐帧旋转绿色面上的所有块
		new Thread(new Runnable() {
			int count=0;
			@Override
			public void run() {
				setRotating(true);
				while (isRotating()) {
					// 旋转绿面上的 4 个角块
					CornerBlock tempCornerBlock=(CornerBlock) block[15];
					tempCornerBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[13];
					tempCornerBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[20];
					tempCornerBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[18];
					tempCornerBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转绿面上的 4 个棱块
					EdgeBlock tempEdgeBlock=(EdgeBlock) block[14];
					tempEdgeBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[17];
					tempEdgeBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[16];
					tempEdgeBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[19];
					tempEdgeBlock.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转绿色中心块，更新背面多边形，重绘
					green.rotate(green.getCenterPoint(), Math.PI/90, clockWise);
					if (green.getSquare().getHidden()<VIEWERROR) {
						backPolygon=getPolygon1();
					}
					update(getGraphics());
					count++;
					if (count>=45) {
						setRotating(false);
					}
				}
			}
		}).start();
		// 更新 block 数组中的位置引用
		if (clockWise) {
			// 顺时针：角块 15→20→18→13→15，棱块 14→17→19→16→14
			Object temp=block[15];
			block[15]=block[20];block[20]=block[18];block[18]=block[13];block[13]=temp;
			temp=block[14];
			block[14]=block[17];block[17]=block[19];block[19]=block[16];block[16]=temp;
		} else {
			// 逆时针：角块 15→13→18→20→15，棱块 14→16→19→17→14
			Object temp=block[15];
			block[15]=block[13];block[13]=block[18];block[18]=block[20];block[20]=temp;
			temp=block[14];
			block[14]=block[16];block[16]=block[19];block[19]=block[17];block[17]=temp;
		}
	}
	
	/**
	 * 红面旋转90度。
	 * <p>
	 * 红面上的块编号：角块 [3,15,8,20]，棱块 [10,5,17,12]，中心块 red。
	 * 顺时针时角块轮换：3→8→20→15→3，棱块轮换：10→5→12→17→10。
	 * </p>
	 *
	 * @param clockWise true 为顺时针旋转；false 为逆时针旋转
	 */
	public void rotateRed90(final Boolean clockWise){
		// 启动动画线程，逐帧旋转红色面上的所有块
		new Thread(new Runnable() {
			int count=0;
			@Override
			public void run() {
				setRotating(true);
				while (isRotating()) {
					// 旋转红面上的 4 个角块
					CornerBlock tempCornerBlock=(CornerBlock) block[3];
					tempCornerBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[15];
					tempCornerBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[8];
					tempCornerBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[20];
					tempCornerBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转红面上的 4 个棱块
					EdgeBlock tempEdgeBlock=(EdgeBlock) block[10];
					tempEdgeBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[5];
					tempEdgeBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[17];
					tempEdgeBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[12];
					tempEdgeBlock.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转红色中心块，更新背面多边形，重绘
					red.rotate(red.getCenterPoint(), Math.PI/90, clockWise);
					if (red.getSquare().getHidden()<VIEWERROR) {
						backPolygon=getPolygon1();
					}
					update(getGraphics());
					count++;
					if (count>=45) {
						setRotating(false);
					}
				}
			}
		}).start();
		// 更新 block 数组中的位置引用
		if (clockWise) {
			// 顺时针：角块 3→8→20→15→3，棱块 10→5→12→17→10
			Object temp=block[3];
			block[3]=block[8];block[8]=block[20];block[20]=block[15];block[15]=temp;
			temp=block[10];
			block[10]=block[5];block[5]=block[12];block[12]=block[17];block[17]=temp;
		} else {
			// 逆时针：角块 3→15→20→8→3，棱块 10→17→12→5→10
			Object temp=block[3];
			block[3]=block[15];block[15]=block[20];block[20]=block[8];block[8]=temp;
			temp=block[10];
			block[10]=block[17];block[17]=block[12];block[12]=block[5];block[5]=temp;
		}
	}
	
	/**
	 * 黄面旋转90度。
	 * <p>
	 * 黄面上的块编号：角块 [13,15,1,3]，棱块 [14,9,10,2]，中心块 yellow。
	 * 顺时针时角块轮换：13→1→3→15→13，棱块轮换：14→9→2→10→14。
	 * </p>
	 *
	 * @param clockWise true 为顺时针旋转；false 为逆时针旋转
	 */
	public void rotateYellow90(final Boolean clockWise){
		// 启动动画线程，逐帧旋转黄色面上的所有块
		new Thread(new Runnable() {
			int count=0;
			@Override
			public void run() {
				setRotating(true);
				while (isRotating()) {
					// 旋转黄面上的 4 个角块
					CornerBlock tempCornerBlock=(CornerBlock) block[13];
					tempCornerBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[15];
					tempCornerBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[1];
					tempCornerBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[3];
					tempCornerBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转黄面上的 4 个棱块
					EdgeBlock tempEdgeBlock=(EdgeBlock) block[14];
					tempEdgeBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[9];
					tempEdgeBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[10];
					tempEdgeBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[2];
					tempEdgeBlock.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转黄色中心块，更新背面多边形，重绘
					yellow.rotate(yellow.getCenterPoint(), Math.PI/90, clockWise);
					if (yellow.getSquare().getHidden()<VIEWERROR) {
						backPolygon=getPolygon1();
					}
					update(getGraphics());
					count++;
					if (count>=45) {
						setRotating(false);
					}
				}
			}
		}).start();
		// 更新 block 数组中的位置引用
		if (clockWise) {
			// 顺时针：角块 13→1→3→15→13，棱块 14→9→2→10→14
			Object temp=block[13];
			block[13]=block[1];block[1]=block[3];block[3]=block[15];block[15]=temp;
			temp=block[14];
			block[14]=block[9];block[9]=block[2];block[2]=block[10];block[10]=temp;
		} else {
			// 逆时针：角块 13→15→3→1→13，棱块 14→10→2→9→14
			Object temp=block[13];
			block[13]=block[15];block[15]=block[3];block[3]=block[1];block[1]=temp;
			temp=block[14];
			block[14]=block[10];block[10]=block[2];block[2]=block[9];block[9]=temp;
		}
	}
	
	/**
	 * 白面旋转90度。
	 * <p>
	 * 白面上的块编号：角块 [6,8,18,20]，棱块 [7,11,12,19]，中心块 white。
	 * 顺时针时角块轮换：6→18→20→8→6，棱块轮换：7→11→19→12→7。
	 * </p>
	 *
	 * @param clockWise true 为顺时针旋转；false 为逆时针旋转
	 */
	public void rotateWhite90(final Boolean clockWise){
		// 启动动画线程，逐帧旋转白色面上的所有块
		new Thread(new Runnable() {
			int count=0;
			@Override
			public void run() {
				setRotating(true);
				while (isRotating()) {
					// 旋转白面上的 4 个角块
					CornerBlock tempCornerBlock=(CornerBlock) block[6];
					tempCornerBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[8];
					tempCornerBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[18];
					tempCornerBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					tempCornerBlock=(CornerBlock) block[20];
					tempCornerBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转白面上的 4 个棱块
					EdgeBlock tempEdgeBlock=(EdgeBlock) block[7];
					tempEdgeBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[11];
					tempEdgeBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[12];
					tempEdgeBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					tempEdgeBlock=(EdgeBlock) block[19];
					tempEdgeBlock.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					
					// 旋转白色中心块，更新背面多边形，重绘
					white.rotate(white.getCenterPoint(), Math.PI/90, clockWise);
					if (white.getSquare().getHidden()<VIEWERROR) {
						backPolygon=getPolygon1();
					}
					update(getGraphics());
					count++;
					if (count>=45) {
						setRotating(false);
					}
				}
			}
		}).start();
		// 更新 block 数组中的位置引用
		if (clockWise) {
			// 顺时针：角块 6→18→20→8→6，棱块 7→11→19→12→7
			Object temp=block[6];
			block[6]=block[18];block[18]=block[20];block[20]=block[8];block[8]=temp;
			temp=block[7];
			block[7]=block[11];block[11]=block[19];block[19]=block[12];block[12]=temp;
		} else {
			// 逆时针：角块 6→8→20→18→6，棱块 7→12→19→11→7
			Object temp=block[6];
			block[6]=block[8];block[8]=block[20];block[20]=block[18];block[18]=temp;
			temp=block[7];
			block[7]=block[12];block[12]=block[19];block[19]=block[11];block[11]=temp;
		}
	}
	
	/**
	 * 根据当前选中的面（{@link #selected}），返回该面的正面边框多边形。
	 * <p>
	 * 正面边框用于在画面上绘制选中面的紫色高亮边框。
	 * </p>
	 *
	 * @return 选中面的正面边框 {@link Polygon}；若未选中任何面则返回 null
	 */
	public Polygon getPolygon(){
		if (selected==BLUE) {
			return getBluePolygon();
		}else if (selected==RED) {
			return getRedPolygon();
		}else if (selected==GREEN) {
			return getGreenPolygon();
		}else if (selected==ORANGE) {
			return getOrangePolygon();
		}else if (selected==YELLOW) {
			return getYellowPolygon();
		}else if (selected==WHITE) {
			return getWhitePolygon();
		}else {
			return null;
		}
	}
	
	/**
	 * 获得蓝色面正面边框多边形。
	 * <p>
	 * 取蓝面 4 个角块（block[1], block[3], block[6], block[8]）的角点，
	 * 经 3D→2D 投影和视口变换后构成四边形。
	 * </p>
	 *
	 * @return 蓝色面正面边框多边形
	 */
	public Polygon getBluePolygon(){
		Polygon polygon=new Polygon();
		// 获取蓝面 4 个角块的角点，投影到屏幕坐标
		CornerBlock cornerBlock1=(CornerBlock)block[1];
		Point point1=cornerBlock1.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point1);
		CornerBlock cornerBlock3=(CornerBlock)block[3];
		Point point3=cornerBlock3.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point3);
		CornerBlock cornerBlock6=(CornerBlock)block[6];
		Point point6=cornerBlock6.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point6);
		CornerBlock cornerBlock8=(CornerBlock)block[8];
		Point point8=cornerBlock8.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point8);
		// 按顺序连接 4 个角点构成边框多边形
		polygon.addPoint(point1.x, point1.y);polygon.addPoint(point3.x, point3.y);
		polygon.addPoint(point8.x, point8.y);polygon.addPoint(point6.x, point6.y);
		return polygon;
	}
	
	/**
	 * 获得橙色面正面边框多边形。
	 * <p>
	 * 取橙面 4 个角块（block[13], block[1], block[6], block[18]）的角点。
	 * </p>
	 *
	 * @return 橙色面正面边框多边形
	 */
	public Polygon getOrangePolygon(){
		Polygon polygon=new Polygon();
		// 获取橙面 4 个角块的角点，投影到屏幕坐标
		CornerBlock cornerBlock13=(CornerBlock)block[13];
		Point point13=cornerBlock13.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point13);
		CornerBlock cornerBlock1=(CornerBlock)block[1];
		Point point1=cornerBlock1.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point1);
		CornerBlock cornerBlock6=(CornerBlock)block[6];
		Point point6=cornerBlock6.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point6);
		CornerBlock cornerBlock18=(CornerBlock)block[18];
		Point point18=cornerBlock18.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point18);
		// 按顺序连接 4 个角点构成边框多边形
		polygon.addPoint(point13.x, point13.y);polygon.addPoint(point1.x, point1.y);
		polygon.addPoint(point6.x, point6.y);polygon.addPoint(point18.x, point18.y);
		return polygon;
	}
	
	/**
	 * 获得绿色面正面边框多边形。
	 * <p>
	 * 取绿面 4 个角块（block[15], block[13], block[18], block[20]）的角点。
	 * </p>
	 *
	 * @return 绿色面正面边框多边形
	 */
	public Polygon getGreenPolygon(){
		Polygon polygon=new Polygon();
		// 获取绿面 4 个角块的角点，投影到屏幕坐标
		CornerBlock cornerBlock15=(CornerBlock)block[15];
		Point point15=cornerBlock15.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point15);
		CornerBlock cornerBlock13=(CornerBlock)block[13];
		Point point13=cornerBlock13.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point13);
		CornerBlock cornerBlock18=(CornerBlock)block[18];
		Point point18=cornerBlock18.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point18);
		CornerBlock cornerBlock20=(CornerBlock)block[20];
		Point point20=cornerBlock20.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point20);
		// 按顺序连接 4 个角点构成边框多边形
		polygon.addPoint(point15.x, point15.y);polygon.addPoint(point13.x, point13.y);
		polygon.addPoint(point18.x, point18.y);polygon.addPoint(point20.x, point20.y);
		return polygon;
	}
	
	/**
	 * 获得红色面正面边框多边形。
	 * <p>
	 * 取红面 4 个角块（block[3], block[15], block[20], block[8]）的角点。
	 * </p>
	 *
	 * @return 红色面正面边框多边形
	 */
	public Polygon getRedPolygon(){
		Polygon polygon=new Polygon();
		// 获取红面 4 个角块的角点，投影到屏幕坐标
		CornerBlock cornerBlock3=(CornerBlock)block[3];
		Point point3=cornerBlock3.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point3);
		CornerBlock cornerBlock15=(CornerBlock)block[15];
		Point point15=cornerBlock15.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point15);
		CornerBlock cornerBlock20=(CornerBlock)block[20];
		Point point20=cornerBlock20.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point20);
		CornerBlock cornerBlock8=(CornerBlock)block[8];
		Point point8=cornerBlock8.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point8);
		// 按顺序连接 4 个角点构成边框多边形
		polygon.addPoint(point3.x, point3.y);polygon.addPoint(point15.x, point15.y);
		polygon.addPoint(point20.x, point20.y);polygon.addPoint(point8.x, point8.y);
		return polygon;
	}
	
	/**
	 * 获得黄色面正面边框多边形。
	 * <p>
	 * 取黄面 4 个角块（block[13], block[15], block[3], block[1]）的角点。
	 * </p>
	 *
	 * @return 黄色面正面边框多边形
	 */
	public Polygon getYellowPolygon(){
		Polygon polygon=new Polygon();
		// 获取黄面 4 个角块的角点，投影到屏幕坐标
		CornerBlock cornerBlock13=(CornerBlock)block[13];
		Point point13=cornerBlock13.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point13);
		CornerBlock cornerBlock15=(CornerBlock)block[15];
		Point point15=cornerBlock15.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point15);
		CornerBlock cornerBlock3=(CornerBlock)block[3];
		Point point3=cornerBlock3.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point3);
		CornerBlock cornerBlock1=(CornerBlock)block[1];
		Point point1=cornerBlock1.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point1);
		// 按顺序连接 4 个角点构成边框多边形
		polygon.addPoint(point13.x, point13.y);polygon.addPoint(point15.x, point15.y);
		polygon.addPoint(point3.x, point3.y);polygon.addPoint(point1.x, point1.y);
		return polygon;
	}
	
	/**
	 * 获得白色面正面边框多边形。
	 * <p>
	 * 取白面 4 个角块（block[6], block[8], block[20], block[18]）的角点。
	 * </p>
	 *
	 * @return 白色面正面边框多边形
	 */
	public Polygon getWhitePolygon(){
		Polygon polygon=new Polygon();
		// 获取白面 4 个角块的角点，投影到屏幕坐标
		CornerBlock cornerBlock6=(CornerBlock)block[6];
		Point point6=cornerBlock6.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point6);
		CornerBlock cornerBlock8=(CornerBlock)block[8];
		Point point8=cornerBlock8.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point8);
		CornerBlock cornerBlock20=(CornerBlock)block[20];
		Point point20=cornerBlock20.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point20);
		CornerBlock cornerBlock18=(CornerBlock)block[18];
		Point point18=cornerBlock18.getCornerPoint().getPoint2D();
		VTs.view_To_screen(point18);
		// 按顺序连接 4 个角点构成边框多边形
		polygon.addPoint(point6.x, point6.y);polygon.addPoint(point8.x, point8.y);
		polygon.addPoint(point20.x, point20.y);polygon.addPoint(point18.x, point18.y);
		return polygon;
	}
	

	/**
	 * 获得蓝色面背面边框多边形。
	 * <p>
	 * 背面边框用于在面旋转动画期间，填充旋转面背面区域（灰色），
	 * 防止旋转过程中露出空白。取蓝面 4 个角块的背面点（getBackPoint），
	 * 经 3D→2D 投影后构成四边形。
	 * </p>
	 *
	 * @return 蓝色面背面边框多边形
	 */
	public Polygon getBluePolygon1(){
		Polygon polygon=new Polygon();
		// 取蓝面 4 个角块的背面点，投影到屏幕坐标，构成背面多边形
		CornerBlock cornerBlock1=(CornerBlock)block[1];
		Point3D point1=cornerBlock1.getBackPoint(blue.getCenterPoint());
		VTs.view_To_screen(point1.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point1.getPoint2D()).x, 
				VTs.view_To_screen(point1.getPoint2D()).y);
		
		CornerBlock cornerBlock3=(CornerBlock)block[3];
		Point3D point3=cornerBlock3.getBackPoint(blue.getCenterPoint());
		VTs.view_To_screen(point3.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point3.getPoint2D()).x,
				VTs.view_To_screen(point3.getPoint2D()).y);
		
		CornerBlock cornerBlock8=(CornerBlock)block[8];
		Point3D point8=cornerBlock8.getBackPoint(blue.getCenterPoint());
		VTs.view_To_screen(point8.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point8.getPoint2D()).x, 
				VTs.view_To_screen(point8.getPoint2D()).y);
		
		CornerBlock cornerBlock6=(CornerBlock)block[6];
		Point3D point6=cornerBlock6.getBackPoint(blue.getCenterPoint());
		VTs.view_To_screen(point6.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point6.getPoint2D()).x,
				VTs.view_To_screen(point6.getPoint2D()).y);
		
		return polygon;
	}
	
	/**
	 * 获得橙色面背面边框多边形。
	 * <p>
	 * 取橙面 4 个角块的背面点，经 3D→2D 投影后构成四边形。
	 * </p>
	 *
	 * @return 橙色面背面边框多边形
	 */
	public Polygon getOrangePolygon1(){
		Polygon polygon=new Polygon();
		// 取橙面 4 个角块的背面点，投影到屏幕坐标，构成背面多边形
		CornerBlock cornerBlock13=(CornerBlock)block[13];
		Point3D point13=cornerBlock13.getBackPoint(orange.getCenterPoint());
		VTs.view_To_screen(point13.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point13.getPoint2D()).x, 
				VTs.view_To_screen(point13.getPoint2D()).y);
		
		CornerBlock cornerBlock1=(CornerBlock)block[1];
		Point3D point1=cornerBlock1.getBackPoint(orange.getCenterPoint());
		VTs.view_To_screen(point1.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point1.getPoint2D()).x,
				VTs.view_To_screen(point1.getPoint2D()).y);
		
		CornerBlock cornerBlock6=(CornerBlock)block[6];
		Point3D point6=cornerBlock6.getBackPoint(orange.getCenterPoint());
		VTs.view_To_screen(point6.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point6.getPoint2D()).x, 
				VTs.view_To_screen(point6.getPoint2D()).y);
		
		CornerBlock cornerBlock18=(CornerBlock)block[18];
		Point3D point18=cornerBlock18.getBackPoint(orange.getCenterPoint());
		VTs.view_To_screen(point18.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point18.getPoint2D()).x,
				VTs.view_To_screen(point18.getPoint2D()).y);
		
		return polygon;
	}
	
	/**
	 * 获得绿色面背面边框多边形。
	 * <p>
	 * 取绿面 4 个角块的背面点，经 3D→2D 投影后构成四边形。
	 * </p>
	 *
	 * @return 绿色面背面边框多边形
	 */
	public Polygon getGreenPolygon1(){
		Polygon polygon=new Polygon();
		// 取绿面 4 个角块的背面点，投影到屏幕坐标，构成背面多边形
		CornerBlock cornerBlock15=(CornerBlock)block[15];
		Point3D point15=cornerBlock15.getBackPoint(green.getCenterPoint());
		VTs.view_To_screen(point15.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point15.getPoint2D()).x,
				VTs.view_To_screen(point15.getPoint2D()).y);
		
		CornerBlock cornerBlock13=(CornerBlock)block[13];
		Point3D point13=cornerBlock13.getBackPoint(green.getCenterPoint());
		VTs.view_To_screen(point13.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point13.getPoint2D()).x, 
				VTs.view_To_screen(point13.getPoint2D()).y);
		
		CornerBlock cornerBlock18=(CornerBlock)block[18];
		Point3D point18=cornerBlock18.getBackPoint(green.getCenterPoint());
		VTs.view_To_screen(point18.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point18.getPoint2D()).x, 
				VTs.view_To_screen(point18.getPoint2D()).y);
		
		CornerBlock cornerBlock20=(CornerBlock)block[20];
		Point3D point20=cornerBlock20.getBackPoint(green.getCenterPoint());
		VTs.view_To_screen(point20.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point20.getPoint2D()).x,
				VTs.view_To_screen(point20.getPoint2D()).y);
		
		return polygon;
	}
	
	/**
	 * 获得红色面背面边框多边形。
	 * <p>
	 * 取红面 4 个角块的背面点，经 3D→2D 投影后构成四边形。
	 * </p>
	 *
	 * @return 红色面背面边框多边形
	 */
	public Polygon getRedPolygon1(){
		Polygon polygon=new Polygon();
		// 取红面 4 个角块的背面点，投影到屏幕坐标，构成背面多边形
		CornerBlock cornerBlock3=(CornerBlock)block[3];
		Point3D point3=cornerBlock3.getBackPoint(red.getCenterPoint());
		VTs.view_To_screen(point3.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point3.getPoint2D()).x, 
				VTs.view_To_screen(point3.getPoint2D()).y);
		
		CornerBlock cornerBlock15=(CornerBlock)block[15];
		Point3D point15=cornerBlock15.getBackPoint(red.getCenterPoint());
		VTs.view_To_screen(point15.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point15.getPoint2D()).x,
				VTs.view_To_screen(point15.getPoint2D()).y);
		
		CornerBlock cornerBlock20=(CornerBlock)block[20];
		Point3D point20=cornerBlock20.getBackPoint(red.getCenterPoint());
		VTs.view_To_screen(point20.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point20.getPoint2D()).x,
				VTs.view_To_screen(point20.getPoint2D()).y);
		
		CornerBlock cornerBlock8=(CornerBlock)block[8];
		Point3D point8=cornerBlock8.getBackPoint(red.getCenterPoint());
		VTs.view_To_screen(point8.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point8.getPoint2D()).x, 
				VTs.view_To_screen(point8.getPoint2D()).y);
		
		return polygon;
	}
	
	/**
	 * 获得黄色面背面边框多边形。
	 * <p>
	 * 取黄面 4 个角块的背面点，经 3D→2D 投影后构成四边形。
	 * </p>
	 *
	 * @return 黄色面背面边框多边形
	 */
	public Polygon getYellowPolygon1(){
		Polygon polygon=new Polygon();
		// 取黄面 4 个角块的背面点，投影到屏幕坐标，构成背面多边形
		CornerBlock cornerBlock13=(CornerBlock)block[13];
		Point3D point13=cornerBlock13.getBackPoint(yellow.getCenterPoint());
		VTs.view_To_screen(point13.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point13.getPoint2D()).x, 
				VTs.view_To_screen(point13.getPoint2D()).y);
		
		CornerBlock cornerBlock15=(CornerBlock)block[15];
		Point3D point15=cornerBlock15.getBackPoint(yellow.getCenterPoint());
		VTs.view_To_screen(point15.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point15.getPoint2D()).x,
				VTs.view_To_screen(point15.getPoint2D()).y);
		
		CornerBlock cornerBlock3=(CornerBlock)block[3];
		Point3D point3=cornerBlock3.getBackPoint(yellow.getCenterPoint());
		VTs.view_To_screen(point3.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point3.getPoint2D()).x,
				VTs.view_To_screen(point3.getPoint2D()).y);
		
		CornerBlock cornerBlock1=(CornerBlock)block[1];
		Point3D point1=cornerBlock1.getBackPoint(yellow.getCenterPoint());
		VTs.view_To_screen(point1.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point1.getPoint2D()).x, 
				VTs.view_To_screen(point1.getPoint2D()).y);
		
		return polygon;
	}
	
	/**
	 * 获得白色面背面边框多边形。
	 * <p>
	 * 取白面 4 个角块的背面点，经 3D→2D 投影后构成四边形。
	 * </p>
	 *
	 * @return 白色面背面边框多边形
	 */
	public Polygon getWhitePolygon1(){
		Polygon polygon=new Polygon();
		// 取白面 4 个角块的背面点，投影到屏幕坐标，构成背面多边形
		CornerBlock cornerBlock6=(CornerBlock)block[6];
		Point3D point6=cornerBlock6.getBackPoint(white.getCenterPoint());
		VTs.view_To_screen(point6.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point6.getPoint2D()).x, 
				VTs.view_To_screen(point6.getPoint2D()).y);
		
		CornerBlock cornerBlock8=(CornerBlock)block[8];
		Point3D point8=cornerBlock8.getBackPoint(white.getCenterPoint());
		VTs.view_To_screen(point8.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point8.getPoint2D()).x,
				VTs.view_To_screen(point8.getPoint2D()).y);
		
		CornerBlock cornerBlock20=(CornerBlock)block[20];
		Point3D point20=cornerBlock20.getBackPoint(white.getCenterPoint());
		VTs.view_To_screen(point20.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point20.getPoint2D()).x,
				VTs.view_To_screen(point20.getPoint2D()).y);
		
		CornerBlock cornerBlock18=(CornerBlock)block[18];
		Point3D point18=cornerBlock18.getBackPoint(white.getCenterPoint());
		VTs.view_To_screen(point18.getPoint2D());
		polygon.addPoint(VTs.view_To_screen(point18.getPoint2D()).x, 
				VTs.view_To_screen(point18.getPoint2D()).y);
		
		return polygon;
	}
	
	/**
	 * 获得魔方当前选中面的背面多边形，用于旋转动画期间填充灰色背景区域。
	 * <p>
	 * 根据 {@link #selected} 的值分发到对应面的背面多边形方法。
	 * 若未选中任何面，则返回上一次缓存的 {@link #backPolygon}。
	 * </p>
	 *
	 * @return 当前选中面的背面边框多边形
	 */
	public Polygon getPolygon1(){
		Polygon polygon=new Polygon();
		if (selected==BLUE) {
			polygon=getBluePolygon1();
		} else if(selected==ORANGE){
			polygon=getOrangePolygon1();
		} else if(selected==GREEN){
			polygon=getGreenPolygon1();
		} else if(selected==RED){
			polygon=getRedPolygon1();
		} else if(selected==YELLOW){
			polygon=getYellowPolygon1();
		} else if(selected==WHITE){
			polygon=getWhitePolygon1();
		} else {
			// 未选中任何面，返回缓存的背面多边形
			polygon= backPolygon;
		}
		return polygon;
	}
	
	/**
	 * 重写 JPanel 的绘制方法，完成魔方的完整渲染。
	 * <p>
	 * 绘制顺序：
	 * <ol>
	 *   <li>调用 super.paintComponent 绘制背景</li>
	 *   <li>用灰色填充背面多边形（旋转面背面区域）</li>
	 *   <li>调用 {@link #drawCube} 绘制所有块（按深度排序）</li>
	 *   <li>用洋红色描边选中面的正面边框</li>
	 * </ol>
	 * </p>
	 *
	 * @param g 图形上下文
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics2D=(Graphics2D)g;
		// 用灰色填充背面多边形（旋转动画时防止镂空）
		graphics2D.setColor(Color.gray);
		if (backPolygon!=null) {
			graphics2D.fillPolygon(backPolygon);
		}
		/*for (int i = 0; i < centerBlocks.length; i++) {
			centerBlocks[i].draw(graphics2D);
		}
		for (int i = 0; i < edgeBlocks.length; i++) {
			edgeBlocks[i].draw(graphics2D);
		}
		for (int i = 0; i < cornerBlocks.length; i++) {
			cornerBlocks[i].draw(graphics2D);
		}*/
		// 按深度排序绘制魔方所有块
		drawCube(graphics2D);
		// 用洋红色描边选中面的正面边框
		graphics2D.setColor(Color.magenta);
		if (getPolygon()!=null) {
			graphics2D.drawPolygon(getPolygon());
		}
	}
	
	/**
	 * 绘制魔方，按深度排序正确处理遮挡关系。
	 * <p>
	 * 绘制逻辑：
	 * <ol>
	 *   <li>调用 {@link #getBlocks()} 将块分为旋转部分 block1 和非旋转部分 block2</li>
	 *   <li>根据选中面中心块的 hidden 值判断面的朝向：
	 *       <ul>
	 *         <li>若 hidden > VIEWERROR：面背对观察者，先绘制非旋转部分（远处），再绘制旋转部分（近处）</li>
	 *         <li>否则：面朝向观察者，先绘制旋转部分，再绘制非旋转部分</li>
	 *       </ul>
	 *   </li>
	 *   <li>若未选中任何面，按 中心块→棱块→角块 顺序绘制所有块</li>
	 * </ol>
	 * </p>
	 *
	 * @param graphics2D 图形上下文
	 */
	private void drawCube(Graphics2D graphics2D){
		// 将块分为旋转部分 block1 和非旋转部分 block2
		getBlocks();
		// 蓝面：根据朝向决定绘制顺序
		if (selected==BLUE) {
			if (blue.getSquare().getHidden()>VIEWERROR) {
				// 蓝面背对观察者：先画远处（非旋转），再画近处（旋转）
				drawBlocks(graphics2D,block2);
				drawBlocks(graphics2D,block1);
			}else {
				// 蓝面朝向观察者：先画旋转部分，再画非旋转部分
				drawBlocks(graphics2D,block1);
				drawBlocks(graphics2D,block2);
			}
		}
		
		// 橙面
		if (selected==ORANGE) {
			if (orange.getSquare().getHidden()>VIEWERROR) {
				drawBlocks(graphics2D,block2);
				drawBlocks(graphics2D,block1);
			}else {
				drawBlocks(graphics2D,block1);
				drawBlocks(graphics2D,block2);
			}
		}
		
		// 绿面
		if (selected==GREEN) {
			if (green.getSquare().getHidden()>VIEWERROR) {
				drawBlocks(graphics2D,block2);
				drawBlocks(graphics2D,block1);
			}else {
				drawBlocks(graphics2D,block1);
				drawBlocks(graphics2D,block2);
			}
		}
		
		// 红面
		if (selected==RED) {
			if (red.getSquare().getHidden()>VIEWERROR) {
				drawBlocks(graphics2D,block2);
				drawBlocks(graphics2D,block1);
			}else {
				drawBlocks(graphics2D,block1);
				drawBlocks(graphics2D,block2);
			}
		}
		
		// 黄面
		if (selected==YELLOW) {
			if (yellow.getSquare().getHidden()>VIEWERROR) {
				drawBlocks(graphics2D,block2);
				drawBlocks(graphics2D,block1);
			}else {
				drawBlocks(graphics2D,block1);
				drawBlocks(graphics2D,block2);
			}
		}
		
		// 白面
		if (selected==WHITE) {
			if (white.getSquare().getHidden()>VIEWERROR) {
				drawBlocks(graphics2D,block2);
				drawBlocks(graphics2D,block1);
			}else {
				drawBlocks(graphics2D,block1);
				drawBlocks(graphics2D,block2);
			}
		}
		
		// 未选中任何面：按 中心块→棱块→角块 顺序绘制所有块
		if (selected==null) {
			for (int i = 0; i < centerBlocks.length; i++) {
				centerBlocks[i].draw(graphics2D);
			}
			for (int i = 0; i < edgeBlocks.length; i++) {
				edgeBlocks[i].draw(graphics2D);
			}
			for (int i = 0; i < cornerBlocks.length; i++) {
				cornerBlocks[i].draw(graphics2D);
			}
		}
	}

	/**
	 * 根据当前选中的面，将魔方的块分为旋转部分（block1）和非旋转部分（block2）。
	 * <p>
	 * 旋转部分 block1 包含选中面上的 9 个块（4角+4棱+1中心），
	 * 非旋转部分 block2 包含其余 17 个块（4角+4棱+5中心）。
	 * 分组后用于 {@link #drawCube} 中按深度排序正确绘制。
	 * </p>
	 * <p>
	 * 每个面的块分配方案：
	 * <ul>
	 *   <li>蓝面 block1: [1,2,3,4,5,6,7,8] + blue</li>
	 *   <li>橙面 block1: [13,9,1,4,6,11,18,16] + orange</li>
	 *   <li>绿面 block1: [15,14,13,17,16,20,19,18] + green</li>
	 *   <li>红面 block1: [3,10,15,5,17,8,12,20] + red</li>
	 *   <li>黄面 block1: [13,14,15,9,10,1,2,3] + yellow</li>
	 *   <li>白面 block1: [6,7,8,11,12,18,19,20] + white</li>
	 * </ul>
	 * </p>
	 */
	private void getBlocks(){
		if (selected==BLUE) {
			// 蓝面上的 9 个块（旋转部分）
			block1[0]=block[1];
			block1[1]=block[2];
			block1[2]=block[3];
			block1[3]=block[4];
			block1[4]=block[5];
			block1[5]=block[6];
			block1[6]=block[7];
			block1[7]=block[8];
			block1[8]=blue;
			// 蓝面外的 17 个块（非旋转部分）
			block2[0]=block[9];block2[1]=block[10];block2[2]=block[11];
			block2[3]=block[12];block2[4]=block[13];block2[5]=block[14];
			block2[6]=block[15];block2[7]=block[16];block2[8]=block[17];
			block2[9]=block[18];block2[10]=block[19];block2[11]=block[20];
			block2[12]=orange;block2[13]=green;block2[14]=red;
			block2[15]=yellow;block2[16]=white;
		} else if (selected==ORANGE) {
			// 橙面上的 9 个块
			block1[0]=block[13];
			block1[1]=block[9];
			block1[2]=block[1];
			block1[3]=block[4];
			block1[4]=block[6];
			block1[5]=block[11];
			block1[6]=block[18];
			block1[7]=block[16];
			block1[8]=orange;
			// 橙面外的 17 个块
			block2[0]=block[2];block2[1]=block[3];block2[2]=block[5];
			block2[3]=block[7];block2[4]=block[8];block2[5]=block[10];
			block2[6]=block[12];block2[7]=block[14];block2[8]=block[15];
			block2[9]=block[17];block2[10]=block[19];block2[11]=block[20];
			block2[12]=blue;block2[13]=green;block2[14]=red;
			block2[15]=yellow;block2[16]=white;
		} else if (selected==GREEN){
			// 绿面上的 9 个块
			block1[0]=block[15];
			block1[1]=block[14];
			block1[2]=block[13];
			block1[3]=block[17];
			block1[4]=block[16];
			block1[5]=block[20];
			block1[6]=block[19];
			block1[7]=block[18];
			block1[8]=green;
			// 绿面外的 17 个块
			block2[0]=block[1];block2[1]=block[2];block2[2]=block[3];
			block2[3]=block[4];block2[4]=block[5];block2[5]=block[6];
			block2[6]=block[7];block2[7]=block[8];block2[8]=block[9];
			block2[9]=block[10];block2[10]=block[11];block2[11]=block[12];
			block2[12]=blue;block2[13]=orange;block2[14]=red;
			block2[15]=yellow;block2[16]=white;
		} else if (selected==RED){
			// 红面上的 9 个块
			block1[0]=block[3];
			block1[1]=block[10];
			block1[2]=block[15];
			block1[3]=block[5];
			block1[4]=block[17];
			block1[5]=block[8];
			block1[6]=block[12];
			block1[7]=block[20];
			block1[8]=red;
			// 红面外的 17 个块
			block2[0]=block[1];block2[1]=block[2];block2[2]=block[4];
			block2[3]=block[6];block2[4]=block[7];block2[5]=block[9];
			block2[6]=block[11];block2[7]=block[13];block2[8]=block[14];
			block2[9]=block[16];block2[10]=block[18];block2[11]=block[19];
			block2[12]=blue;block2[13]=orange;block2[14]=green;
			block2[15]=yellow;block2[16]=white;
		} else if (selected==YELLOW){
			// 黄面上的 9 个块
			block1[0]=block[13];
			block1[1]=block[14];
			block1[2]=block[15];
			block1[3]=block[9];
			block1[4]=block[10];
			block1[5]=block[1];
			block1[6]=block[2];
			block1[7]=block[3];
			block1[8]=yellow;
			// 黄面外的 17 个块
			block2[0]=block[4];block2[1]=block[5];block2[2]=block[6];
			block2[3]=block[7];block2[4]=block[8];block2[5]=block[11];
			block2[6]=block[12];block2[7]=block[16];block2[8]=block[17];
			block2[9]=block[18];block2[10]=block[19];block2[11]=block[20];
			block2[12]=blue;block2[13]=orange;block2[14]=green;
			block2[15]=red;block2[16]=white;
		} else if (selected==WHITE){
			// 白面上的 9 个块
			block1[0]=block[6];
			block1[1]=block[7];
			block1[2]=block[8];
			block1[3]=block[11];
			block1[4]=block[12];
			block1[5]=block[18];
			block1[6]=block[19];
			block1[7]=block[20];
			block1[8]=white;
			// 白面外的 17 个块
			block2[0]=block[1];block2[1]=block[2];block2[2]=block[3];
			block2[3]=block[4];block2[4]=block[5];block2[5]=block[16];
			block2[6]=block[9];block2[7]=block[10];block2[8]=block[13];
			block2[9]=block[14];block2[10]=block[15];block2[11]=block[17];
			block2[12]=blue;block2[13]=orange;block2[14]=green;
			block2[15]=red;block2[16]=yellow;
		}
	}
	
	/**
	 * 绘制一个块数组中的所有块。
	 * <p>
	 * 遍历数组中的每个对象，根据其运行时类型（CornerBlock、EdgeBlock 或 CenterBlock）
	 * 进行类型转换后调用对应的 draw 方法。
	 * </p>
	 *
	 * @param graphics2D 图形上下文
	 * @param objects    要绘制的块数组，元素类型为 CornerBlock、EdgeBlock 或 CenterBlock
	 */
	private void drawBlocks(Graphics2D graphics2D,Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			// 根据运行时类型分发到对应的 draw 方法
			if (objects[i].getClass().equals(CornerBlock.class)) {
				CornerBlock temp=(CornerBlock)objects[i];
				temp.draw(graphics2D);
			} else if ((objects[i].getClass().equals(EdgeBlock.class))){
				EdgeBlock temp=(EdgeBlock)objects[i];
				temp.draw(graphics2D);
			} else {
				CenterBlock temp=(CenterBlock)objects[i];
				temp.draw(graphics2D);
			}
		}
	}
	
	/**
	 * 绘制所有 6 个中心块（不区分旋转/非旋转部分）。
	 * <p>
	 * 该方法在某些特殊场景下使用，直接依次绘制 6 个中心块。
	 * </p>
	 *
	 * @param graphics2D 图形上下文
	 */
	public void drawCenterBlock(Graphics2D graphics2D){
		blue.draw(graphics2D);
		orange.draw(graphics2D);
		green.draw(graphics2D);
		yellow.draw(graphics2D);
		red.draw(graphics2D);
		white.draw(graphics2D);
	}
	
	/**
	 * 重写 update 方法，实现双缓冲绘制以消除闪烁。
	 * <p>
	 * 双缓冲流程：
	 * <ol>
	 *   <li>创建 800×800 的离屏图像</li>
	 *   <li>在离屏图像上执行 paint 绘制</li>
	 *   <li>将离屏图像一次性绘制到屏幕</li>
	 *   <li>释放离屏图像的图形上下文</li>
	 * </ol>
	 * </p>
	 *
	 * @param g 图形上下文
	 */
	 @Override
	 public void update(Graphics g) {
		 super.update(g);
		 
		 // 创建离屏图像用于双缓冲
		 offScreenImage=this.createImage(800, 800);
		 Graphics gImage=offScreenImage.getGraphics();
		 // 先在离屏图像上绘制
		 paint(gImage);
		 // 将离屏图像一次性绘制到屏幕，消除闪烁
		 g.drawImage(offScreenImage, 0, 0, null);
		 // 释放离屏图形上下文资源
		 gImage.dispose();
	 }
	
	/**
	 * 重写 paint 方法。由于 {@link #update(Graphics)} 中已调用 paint 绘制到离屏图像，
	 * 此处仅调用 super.paint 确保背景正确绘制。
	 *
	 * @param g 图形上下文
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	/**
	 * 鼠标点击监听器（内部类）。
	 * <p>
	 * 处理鼠标按下事件，实现面选择功能：
	 * <ul>
	 *   <li>记录鼠标位置到 {@link #oldPoint}，供拖拽监听器使用</li>
	 *   <li>检测鼠标点击位置落在哪个面的正面边框多边形内</li>
	 *   <li>仅当选中面背对观察者（hidden > VIEWERROR）且当前未在旋转时，才设为选中面</li>
	 *   <li>更新背面多边形并重绘画面</li>
	 * </ul>
	 * <p>
	 * 注意：面朝向判断条件 hidden > VIEWERROR 表示该面"不可见"（背对观察者），
	 * 这是因为点击的是魔方背面可见的区域，选中后旋转该面使得它转到正面。
	 * </p>
	 */
	private class MyMouseListner extends MouseAdapter{
		  @Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			// 记录鼠标按下位置
			oldPoint=e.getPoint();
			// 检测点击位置落在哪个面内，且该面背对观察者、当前未在旋转
			if (getBluePolygon().contains(e.getPoint())
					&&blue.getSquare().getHidden()>VIEWERROR&&!isRotating()) {
				selected=BLUE;
			}else if (getRedPolygon().contains(e.getPoint())
					&&red.getSquare().getHidden()>VIEWERROR&&!isRotating()) {
				selected=RED;
			}else if (getGreenPolygon().contains(e.getPoint())
					&&green.getSquare().getHidden()>VIEWERROR&&!isRotating()) {
				selected=GREEN;
			}else if (getOrangePolygon().contains(e.getPoint())
					&&orange.getSquare().getHidden()>VIEWERROR&&!isRotating()) {
				selected=ORANGE;
			}else if (getYellowPolygon().contains(e.getPoint())
					&&yellow.getSquare().getHidden()>VIEWERROR&&!isRotating()) {
				selected=YELLOW;
			}else if (getWhitePolygon().contains(e.getPoint())
					&&white.getSquare().getHidden()>VIEWERROR&&!isRotating()) {
				selected=WHITE;
			}else {
				// 点击不在任何面内，取消选中
				selected=null;
			}
			// 更新背面多边形并重绘
			backPolygon=getPolygon1();
			update(getGraphics());
		}
	  }
	  
	  /**
	   * 鼠标拖拽监听器（内部类）。
	   * <p>
	   * 处理鼠标拖拽事件，实现整体旋转魔方视角功能：
	   * <ul>
	   *   <li>根据鼠标拖拽的水平和垂直位移计算绕 Y 轴和 X 轴的旋转角度</li>
	   *   <li>位移 × π/200 将像素距离转换为弧度（缩放因子控制旋转灵敏度）</li>
	   *   <li>仅当未在执行面旋转动画时才进行整体旋转</li>
	   *   <li>旋转所有块（中心块、角块、棱块）后更新画面</li>
	   *   <li>更新 oldPoint 为当前鼠标位置，供下次拖拽计算增量</li>
	   * </ul>
	   * </p>
	   */
	  private class MyMouseMotionListner extends MouseMotionAdapter{
		  @Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			// 根据鼠标位移计算旋转角度增量（弧度），π/200 为灵敏度缩放因子
			angleX=(e.getY()-oldPoint.y)*Math.PI/200;
			angleY=(e.getX()-oldPoint.x)*Math.PI/200;
			// 仅在未执行面旋转动画时，才允许整体视角旋转
			if (!isRotating()) {
				// 绕 X/Y 轴旋转所有中心块
				for (int i = 0; i < centerBlocks.length; i++) {
					centerBlocks[i].rotateXY(angleX, angleY);
				}
				// 绕 X/Y 轴旋转所有角块
				for (int i = 0; i < cornerBlocks.length; i++) {
					cornerBlocks[i].rotateXY(angleX, angleY);
				}
				// 绕 X/Y 轴旋转所有棱块
				for (int i = 0; i < edgeBlocks.length; i++) {
					edgeBlocks[i].rotateXY(angleX, angleY);
				}
			}
			// 更新背面多边形
			backPolygon=getPolygon1();
			// 更新鼠标位置，供下次拖拽计算增量
			oldPoint=e.getPoint();
			// 重绘画面
			update(getGraphics());
		}
	  }
	  
	  
	
}

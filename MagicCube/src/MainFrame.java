import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import math3D.VTs;

/**
 * MainFrame类是魔方游戏的主窗口，继承自JFrame并实现Runnable接口。
 * 负责创建游戏窗口、处理键盘事件、管理魔方的旋转操作。
 */
public class MainFrame extends JFrame implements Runnable {

	/** 序列化版本号 */
	private static final long serialVersionUID = 1L;

	/** 视图变换工具对象 */
	VTs vTs = new VTs();

	/** 鼠标上一次的位置，用于计算旋转角度 */
	Point oldPoint;
	/** 绕X轴和Y轴的旋转角度 */
	double angleX,angleY;

	/** 魔方画布组件 */
	private Canvas_Cube canvas_Cube=new Canvas_Cube();

	/**
	 * 程序入口点，创建主窗口实例。
	 * @param args 命令行参数
	 */
	public static void main(String args[]) {
		MainFrame workStart=new MainFrame();
	}

	/**
	 * 构造函数，初始化魔方游戏主窗口。
	 * 设置窗口标题为"MagicCube"，大小为700x700，
	 * 添加键盘监听器和操作提示标签，添加画布组件。
	 */
	public MainFrame() {
		super("MagicCube");
		setSize(700, 700);
		setVisible(true);
		setResizable(false);
		addKeyListener(new MyKeyListenner());
		JLabel label=new JLabel("鼠标选择要旋转的面。按D键或→键顺时针旋转；按S键或←键逆时针旋转");
		canvas_Cube.add(label);
		add(canvas_Cube);
	}

	/**
	 * 处理窗口事件，窗口关闭时退出程序。
	 * @param e 窗口事件
	 */
	public void processWindowEvent(WindowEvent e) {
		if(e.getID() == WindowEvent.WINDOW_CLOSING) {
	       System.exit(0);
		}
	}

	/**
	 * Run方法（线程体），当前为空循环。
	 * 实现Runnable接口要求的方法。
	 */
	public void run() {
		while(true) {
		}
	}

	/**
	 * 绘制窗口内容，将Graphics转换为Graphics2D后委托给paintComponents。
	 * @param g 图形上下文
	 */
	public void paint(Graphics g) {
		Graphics2D graphics2D=(Graphics2D)g;
		paintComponents(graphics2D);
	}
	
	/**
	 * MyKeyListenner内部类，键盘事件监听器。
	 * 处理方向键和A/D键来旋转魔方的指定面。
	 * 左箭头/A键：逆时针旋转选中的面
	 * 右箭头/D键：顺时针旋转选中的面
	 */
	private class MyKeyListenner extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			super.keyPressed(e);
			if (e.getKeyCode()==KeyEvent.VK_CONTROL) {
				System.out.println("制表符");
			} else if((e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_A)&&!canvas_Cube.isRotating()) {
				// 左方向键或A键：逆时针旋转选中的面
				System.out.println("左");
				if (canvas_Cube.selected==canvas_Cube.BLUE) {
					canvas_Cube.rotateBlue90(false);
				}else if (canvas_Cube.selected==canvas_Cube.ORANGE) {
					canvas_Cube.rotateOrange90(false);
				}else if (canvas_Cube.selected==canvas_Cube.GREEN) {
					canvas_Cube.rotateGreen90(false);
				}else if (canvas_Cube.selected==canvas_Cube.RED) {
					canvas_Cube.rotateRed90(false);
				}else if (canvas_Cube.selected==canvas_Cube.YELLOW) {
					canvas_Cube.rotateYellow90(false);
				}else if (canvas_Cube.selected==canvas_Cube.WHITE) {
					canvas_Cube.rotateWhite90(false);
				}
				canvas_Cube.update(canvas_Cube.getGraphics());
			}else if((e.getKeyCode()==KeyEvent.VK_RIGHT||e.getKeyCode()==KeyEvent.VK_D)&&!canvas_Cube.isRotating()) {
				// 右方向键或D键：顺时针旋转选中的面
				System.out.println("右");
				if (canvas_Cube.selected==canvas_Cube.BLUE) {
					canvas_Cube.rotateBlue90(true);
				}else if (canvas_Cube.selected==canvas_Cube.ORANGE) {
					canvas_Cube.rotateOrange90(true);
				}else if (canvas_Cube.selected==canvas_Cube.GREEN) {
					canvas_Cube.rotateGreen90(true);
				}else if (canvas_Cube.selected==canvas_Cube.RED) {
					canvas_Cube.rotateRed90(true);
				}else if (canvas_Cube.selected==canvas_Cube.YELLOW) {
					canvas_Cube.rotateYellow90(true);
				}else if (canvas_Cube.selected==canvas_Cube.WHITE) {
					canvas_Cube.rotateWhite90(true);
				}
				canvas_Cube.update(canvas_Cube.getGraphics());
			}
		}
	}
}

package com.game;

import java.awt.*;
import javax.swing.*;

/**
 * My2048类是2048游戏的主窗口，继承自JFrame。
 * 负责创建游戏窗口、设置布局，并启动游戏逻辑。
 */
public class My2048 extends JFrame 
{ 
  /**
   * 构造函数，初始化2048游戏主窗口。
   * 设置窗口标题为"2048"，窗口大小为400x400，
   * 起始位置为(500,200)，布局为4x4的GridLayout网格布局，
   * 然后创建Operation对象启动游戏逻辑，并设置窗口可见。
   */
  public My2048()
  {
    setTitle("2048");//设置标题
    setSize(400, 400);//设定窗口大小
    setLocation(500, 200);//设定窗口起始位置
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(new GridLayout(4, 4, 5, 5));//设定布局方式为GridLayout型
    new Operation(this);
    this.setVisible(true);//设为可视
  }

  /**
   * 程序入口点。
   * 先尝试设置Substance外观风格，若失败则打印异常信息。
   * 然后设置Frame的默认外观装饰，最后创建My2048实例启动游戏。
   * @param args 命令行参数
   */
  public static void main(String args[])
  {
    try
    {
      UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");//设定UI
    }
    catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();//接受抛出的异常
    }
    JFrame.setDefaultLookAndFeelDecorated(true);//设定Frame的缺省外观
    new My2048();
  }
  
}

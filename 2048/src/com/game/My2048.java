package com.game;

import java.awt.*;
import javax.swing.*;
public class My2048 extends JFrame 
{ 
  public My2048()//构造函数 
  {
    setTitle("2048");//设置标题
    setSize(400, 400);//设定窗口大小
    setLocation(500, 200);//设定窗口起始位置
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(new GridLayout(4, 4, 5, 5));//设定布局方式为GridLayout型
    new Operation(this);
    this.setVisible(true);//设为可视
  }
  
  public static void main(String args[]) //程序入口点
  {
    try
    {
      UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");//设定UI
    } //接受抛出的异常
    catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }
    JFrame.setDefaultLookAndFeelDecorated(true);//设定Frame的缺省外观
    new My2048();
  }
  
}
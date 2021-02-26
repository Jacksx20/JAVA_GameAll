package com.game;

import java.awt.*;
import javax.swing.*;
public class My2048 extends JFrame 
{ 
  public My2048()//���캯�� 
  {
    setTitle("2048");//���ñ���
    setSize(400, 400);//�趨���ڴ�С
    setLocation(500, 200);//�趨������ʼλ��
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout(new GridLayout(4, 4, 5, 5));//�趨���ַ�ʽΪGridLayout��
    new Operation(this);
    this.setVisible(true);//��Ϊ����
  }
  
  public static void main(String args[]) //������ڵ�
  {
    try
    {
      UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");//�趨UI
    } //�����׳����쳣
    catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e)
    {
      e.printStackTrace();
    }
    JFrame.setDefaultLookAndFeelDecorated(true);//�趨Frame��ȱʡ���
    new My2048();
  }
  
}
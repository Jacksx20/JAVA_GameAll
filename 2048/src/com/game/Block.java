package com.game;

import javax.swing.*;
import java.awt.*;

/**
 * Block类表示2048游戏中的一个方块，继承自JLabel。
 * 每个方块持有一个数值，并根据数值显示不同的背景颜色和文本。
 */
public class Block extends JLabel 
{
  /** 方块当前持有的数值 */
  private int value;

  /**
   * 构造函数，初始化方块。
   * 将数值设为0，设定字体为40号普通字体，背景色设为灰色。
   */
  public Block() 
  {
    value = 0;//初始化值为0
    setFont(new Font("font", Font.PLAIN, 40));//设定字体
    setBackground(Color.gray);//设定初始颜色为灰色
  }
  
  /**
   * 获取方块当前的数值。
   * @return 方块的数值
   */
  public int getValue()
  {
    return value;
  }
  
  /**
   * 设置方块的数值。
   * 如果数值为0则不显示文本，否则显示对应数值的文本。
   * 设置完数值后自动调用setColor()更新背景颜色。
   * @param value 要设置的数值
   */
  public void setValue(int value)
  {
    this.value = value;
    String text = String.valueOf(value);
    if (value != 0)
      setText(text);
    else
      setText("");//如果值为0则不显示
    setColor();
  }
  
  /**
   * 根据方块的数值设定不同的背景颜色。
   * 不同数值对应不同的颜色，形成2048游戏经典的渐变色效果：
   * 0-灰色，2-浅米色，4-浅棕色，8/16-橙色，32-深橙色，
   * 64-红橙色，128-浅黄色，256-黄色，512-深黄色，
   * 1024-金黄色，2048-亮金黄色，4096-红色。
   */
  public void setColor()
  {
    switch (value) 
      {
    case 0:
      setBackground(Color.gray);
      break;
    case 2:
      setBackground(new Color(238, 228, 218));
      break;
    case 4:
      setBackground(new Color(238, 224, 198));
      break;
    case 8:
      setBackground(new Color(243, 177, 116));
      break;
    case 16:
      setBackground(new Color(243, 177, 116));
      break;
    case 32:
      setBackground(new Color(248, 149, 90));
      break;
    case 64:
      setBackground(new Color(249, 94, 50));
      break;
    case 128:
      setBackground(new Color(239, 207, 108));
      break;
    case 256:
      setBackground(new Color(239, 207, 99));
      break;
    case 512:
      setBackground(new Color(239, 203, 82));
      break;
    case 1024:
      setBackground(new Color(239, 199, 57));
      break;
    case 2048:
      setBackground(new Color(239, 195, 41));
      break;
    case 4096:
      setBackground(new Color(255, 60, 57));
      break;
      }
  }
}

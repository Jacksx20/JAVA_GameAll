package com.game;

import java.awt.event.*;
import javax.swing.*;

/**
 * Operation类实现了2048游戏的核心逻辑，包括方块的移动、合并、
 * 新方块的出现、游戏胜负判断等功能。实现KeyListener接口监听键盘事件。
 */
public class Operation implements KeyListener
{
  /** 存储游戏面板上16个方块的数组 */
  Block[] block;
  /** 游戏面板 */
  JPanel panel;
  /** 标记向上、下、左、右是否可以移动 */
  public boolean up,down,left,right;
  /** 累计移动的次数 */
  int moveFlag;
  /** 标记是否还能在面板上加入新的数字（是否有空位） */
  boolean numFlag;

  /**
   * 构造函数，初始化游戏逻辑。
   * 获取主窗口的内容面板，创建16个方块，初始化移动标志，
   * 添加方块到面板，随机出现2个初始方块，并注册键盘监听器。
   * @param frame 游戏主窗口
   */
  public Operation(JFrame frame) 
  {
    this.panel = (JPanel)frame.getContentPane();//构造出panel
    block = new Block[16];//构造出长度为16的数组
    numFlag = true;//初始化
    moveFlag = 0;
    up=true;down=true;left=true;right=true;
    addBlock();
    for (int i = 0; i < 2; i++)
      appearBlock();
    frame.addKeyListener(this);
  }
  
  /**
   * 将16个方块添加到游戏面板中。
   * 每个方块设置为居中对齐且不透明，然后添加到panel。
   */
  private void addBlock() 
  {
    for (int i = 0; i < 16; i++) //往panel里加入block
    {
      block[i] = new Block();
      block[i].setHorizontalAlignment(JLabel.CENTER);// 不透明的标签
      block[i].setOpaque(true);
      panel.add(block[i]);  
    }
  } 

  /**
   * 在面板上随机出现一个新方块。
   * 随机选择一个空位（值为0的方块），以50%概率放置2或4。
   * 如果没有空位则不做任何操作。
   */
  public void appearBlock() 
  {
    while (numFlag) //当还能加入随机的一个新的值得时候
    {
      int index = (int) (Math.random() * 16);//取一个0到15的随机整数，这个数作为随机加入盘中的2或4的位置
      if (block[index].getValue() == 0)//如果这个数所在的block数组中值为0，即在为空的时候，加入一个2或4的数字
      {
        if (Math.random() < 0.5)
        {
          block[index].setValue(2);
        }
        else
        {
          block[index].setValue(4);
        }
        break;//跳出while
      }
    }
  }
  
  /**
   * 判断面板上是否还有空位。
   * 统计非零方块的数量，如果全部16个方块都有值，
   * 则将numFlag设为false，表示无法再添加新方块。
   */
  public void judgeAppear()
  {
    int sum = 0;
    for (int i = 0; i < 16; i++) 
    {
      if (block[i].getValue() != 0)
      {
        sum++;
      }
    }
    if (sum == 16)
      numFlag = false;
  }
  
  /**
   * 从指定位置开始，按指定步长在指定范围内查找第一个非空方块。
   * @param i    起始位置
   * @param j    查找步长（正数向后，负数向前）
   * @param a    查找范围下界
   * @param b    查找范围上界
   * @return     第一个非空方块的位置索引，未找到则返回-1
   */
  public int Find(int i,int j,int a,int b)
  {
    while(i<b&&i>=a)
    {
       if(block[i].getValue()!=0)
       {
        return i;
       }
       i=i+j;
    }
    return -1;
  }

  /**
   * 向上移动方块并合并相同数值的方块。
   * 遍历每一列（4列），从上到下依次处理：
   * 先将空位填充（把下方非空方块移上来），
   * 再合并相邻且相同数值的方块。
   */
  public void upBlock()
  {
    int i=0,j=0;int t=0;int valueJ=0;int valueI=0;int index=0;
    for(i=0;i<4;i++)
    {
      index=i;
      for(j=i+4;j<16;j+=4)
      {  
        valueJ=0; valueI=0;
        if(block[index].getValue()==0)
        {
          t=Find(index,4,0,16);
          if(t!=-1)
          {
            block[index].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueI=block[index].getValue();
        if(block[j].getValue()==0)
        {
          t=Find(j,4,0,16);
          if(t!=-1)
          {
            block[j].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueJ=block[j].getValue();
        if(valueI==valueJ&&valueI!=0&&valueJ!=0)
        {
          block[index].setValue(valueI+valueJ);
          block[j].setValue(0);
          numFlag = true;
        }
        index=j;
      }
        
    }
  }

  /**
   * 向下移动方块并合并相同数值的方块。
   * 遍历每一列（4列），从下到上依次处理：
   * 先将空位填充（把上方非空方块移下来），
   * 再合并相邻且相同数值的方块。
   */
  public void downBlock() {
  
    int i=0,j=0;int t=0;int valueJ=0;int valueI=0;int index=0;
    for(i=12;i<16;i++)
    {
      index=i;
      for(j=i-4;j>=0;j-=4)
      {  
        valueJ=0; valueI=0;
        if(block[index].getValue()==0)
        {
          t=Find(index,-4,0,16);
          if(t!=-1)
          {
            block[index].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueI=block[index].getValue();
        if(block[j].getValue()==0)
        {
          t=Find(j,-4,0,16);
          if(t!=-1)
          {
            block[j].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueJ=block[j].getValue();
        if(valueI==valueJ&&valueI!=0&&valueJ!=0)
        {
          block[index].setValue(valueI+valueJ);
          block[j].setValue(0);
          numFlag = true;
        }
        index=j;
      }
        
    }
  }

  /**
   * 向右移动方块并合并相同数值的方块。
   * 遍历每一行（4行），从右到左依次处理：
   * 先将空位填充（把左方非空方块移过来），
   * 再合并相邻且相同数值的方块。
   */
  public void rightBlock() 
  {
    int i=0,j=0;int t=0;int valueJ=0;int valueI=0;int index=0;
    for(i=3;i<16;i+=4)
    {
      index=i;
      for(j=i-1;j>i-4;j--)
      {  
        valueJ=0; valueI=0;
        if(block[index].getValue()==0)
        {
          t=Find(index,-1,i-3,index+1);
          if(t!=-1)
          {
            block[index].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueI=block[index].getValue();
        if(block[j].getValue()==0)
        {
          t=Find(j,-1,i-3,j+1);
          if(t!=-1)
          {
            block[j].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueJ=block[j].getValue();
        if(valueI==valueJ&&valueI!=0&&valueJ!=0)
        {
          block[index].setValue(valueI+valueJ);
          block[j].setValue(0);
          numFlag = true;
        }
        index=j;
      }
        
    }
  }

  /**
   * 向左移动方块并合并相同数值的方块。
   * 遍历每一行（4行），从左到右依次处理：
   * 先将空位填充（把右方非空方块移过来），
   * 再合并相邻且相同数值的方块。
   */
  public void leftBlock() 
  {
    int i=0,j=0;int t=0;int valueJ=0;int valueI=0;int index=0;
    for(i=0;i<16;i+=4)
    {
      index=i;
      for(j=i+1;j<i+4;j++)
      {  
        valueJ=0; valueI=0;
        if(block[index].getValue()==0)
        {
          t=Find(index,1,index,i+4);
          if(t!=-1)
          {
            block[index].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueI=block[index].getValue();
        if(block[j].getValue()==0)
        {
          t=Find(j,1,j,i+4);
          if(t!=-1)
          {
            block[j].setValue(block[t].getValue());
            block[t].setValue(0);
          }
          else
          {
            break;
          }
        }
        valueJ=block[j].getValue();
        if(valueI==valueJ&&valueI!=0&&valueJ!=0)
        {
          block[index].setValue(valueI+valueJ);
          block[j].setValue(0);
          numFlag = true;
        }
        index=j;
      }
        
    }
  }

  /**
   * 判断游戏是否结束（失败）。
   * 当不能再添加新方块且四个方向都不能移动时，
   * 在面板中央显示"GAME OVER"文字，并添加鼠标点击监听器以重启游戏。
   */
  public void over() 
  {
    if (numFlag ==false&& up==false&&down==false&&left==false&&right==false) //当不能添加元素，并且不可移动的步数超过36就输了，输了的时候在盘中央显示GAMEOVER
    {
      block[4].setText("G");
      block[5].setText("A");
      block[6].setText("M");
      block[7].setText("E");
      block[8].setText("O");
      block[9].setText("V");
      block[10].setText("E");
      block[11].setText("R"); 
      block[11].addMouseListener(new MouseAdapter() {public void mousePressed(MouseEvent e){reStart();}});
    }
  }
    
  /**
   * 判断游戏是否胜利。
   * 当面板中出现2048时，在面板上显示"YOU WIN"文字，
   * 并添加鼠标点击监听器以重启游戏。
   */
  public void win()
  { 
    block[0].setText("Y");
    block[1].setText("O");
    block[2].setText("U");
    block[13].setText("W");
    block[14].setText("I");
    block[15].setText("N");
    block[15].addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        reStart();
      }
    });
  }

  /**
   * 重启游戏。
   * 重置所有移动标志和方块数值，然后随机出现2个新方块。
   */
  public void reStart()
  {
    numFlag=true;
    moveFlag=0;
    up=true;down=true;left=true;right=true;
    for(int i=0;i<16;i++)
      block[i].setValue(0);
    for (int i = 0; i < 2; i++)
      appearBlock();
  }

  /**
   * 键盘按键事件处理。
   * 根据按下的方向键调用对应的移动函数，然后判断是否可以添加新方块、
   * 添加新方块、判断游戏是否结束。如果当前方向无法移动则锁定该方向。
   * @param e 键盘事件
   */
  public void keyPressed(KeyEvent e)
  {
    switch (e.getKeyCode()) {
    case KeyEvent.VK_UP:
      if(up){
      upBlock();}
      judgeAppear();
      appearBlock();
      over();
        
      if(numFlag==false)
      {
        up=false;
      }
      else
      {
        up=true;down=true;left=true;right=true;
      }
      break;
    case KeyEvent.VK_DOWN:
      if(down){
      downBlock();}
      judgeAppear();
      appearBlock();
      over();
      if(numFlag==false)
      {
        down=false;
      }
      else
      {
        up=true;down=true;left=true;right=true;
      }
      break;
    case KeyEvent.VK_LEFT:
      if(left){
      leftBlock();}
      judgeAppear();
      appearBlock();
      over();
        
      if(numFlag==false)
      {
        left=false;
      }
      else
      {
        up=true;down=true;left=true;right=true;
      }
      break;
    case KeyEvent.VK_RIGHT:
      if(right){
      rightBlock();}
      judgeAppear();
      appearBlock();
      over();
        
      if(numFlag==false)
      {
        right=false;
      }
      else
      {
        up=true;down=true;left=true;right=true;
      }
      break;
    }
  
  }

  /**
   * 键盘键入事件（未使用，KeyListener接口要求实现）。
   */
  public void keyTyped(KeyEvent e) {
  
  }

  /**
   * 键盘释放事件（未使用，KeyListener接口要求实现）。
   */
  public void keyReleased(KeyEvent e) {
  
  }
  
}

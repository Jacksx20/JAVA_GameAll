# JAVA_小游戏合集

**[English](./README_EN.md)** | **中文**

这里整理了一些以前上学时学习过的练手用的JAVA小游戏合集，涵盖了JAVA多方面的知识点

一、Java基础(语言、集合框架、OOP、设计模式等)

二、Java高级(JavaEE、框架、服务器、工具等)

三、多线程和并发

四、Java虚拟机

五、数据库(Sql、MySQL、Redis等)

六、算法与数据结构

---

## 项目概述

Java 小游戏合集，包含 3 个独立子项目，涵盖 Java GUI（Swing）、3D 图形渲染、多线程动画、事件监听、第三方库使用等知识点。

---

## 目录结构

```
JAVA_GameAll/
│
├── 2048/                          # 2048 数字拼图游戏
│   ├── src/                       # 源代码
│   │   ├── com/game/
│   │   │   ├── Block.java         # 方块类，表示单个数字格子（JLabel）
│   │   │   ├── My2048.java        # 主窗口类，游戏入口（JFrame）
│   │   │   └── Operation.java     # 核心逻辑类，处理移动/合并/胜负判断（KeyListener）
│   │   └── META-INF/
│   │       └── MANIFEST.MF       # JAR 打包清单
│   ├── out/                       # 编译输出
│   │   └── production/2048/
│   │       ├── com/game/          # 编译后的 .class 文件
│   │       └── META-INF/
│   └── README.md                  # 2048 游戏规则与攻略
│
├── MagicCube/                     # 3D 魔方游戏
│   ├── src/                       # 源代码
│   │   ├── Canvas_Cube.java       # 魔方画布，渲染/旋转/面选择（JPanel，核心类）
│   │   ├── CenterBlock.java       # 中心块，每个面中心的一个块（1个Square面）
│   │   ├── CornerBlock.java       # 角块，3个面的交汇块（3个Square面）
│   │   ├── EdgeBlock.java         # 棱块，2个面的交汇块（2个Square面）
│   │   ├── MainFrame.java         # 主窗口类，游戏入口（JFrame）
│   │   ├── Square.java            # 小正方形面，4个3D顶点+颜色，支持旋转和绘制
│   │   └── math3D/                # 3D数学工具包
│   │       ├── Point3D.java       # 三维点类，旋转/透视投影
│   │       ├── VTs.java           # 视图坐标→屏幕坐标转换
│   │       ├── normal_hidden.class
│   │       ├── project3D_To_2D.class
│   │       ├── rotate_calculator.class
│   │       └── view_To_screen_coordinates.class
│   ├── bin/                       # Eclipse 编译输出
│   ├── out/                       # IDEA 编译输出
│   ├── .classpath                 # Eclipse 类路径配置
│   ├── .project                   # Eclipse 项目配置
│   ├── MagicCube.iml              # IntelliJ 模块描述
│   └── REDEME.MD                  # 魔方说明
│
├── 表白二维码/                     # 二维码生成器
│   ├── src/                       # 源代码
│   │   ├── day20200305/
│   │   │   └── Code01.java        # 二维码生成主类（ZXing库）
│   │   └── lib/
│   │       └── core-3.3.3.jar    # ZXing 二维码核心库
│   ├── out/                       # 编译输出
│   │   └── production/表白二维码/
│   │       ├── day20200305/       # 编译后的 .class 文件
│   │       └── lib/
│   │           └── core-3.3.3.jar
│   ├── TT.jpg                     # 生成的二维码图片输出
│   └── 表白二维码.iml              # IntelliJ 模块描述
│
└── README.md                      # 项目总说明
```

---

## 子项目说明

### 1. 2048 游戏

| 文件 | 职责 |
|------|------|
| `Block.java` | 数字方块组件，根据数值显示不同背景色（0~4096渐变色映射） |
| `My2048.java` | 主窗口，4×4 网格布局，Substance 皮肤 |
| `Operation.java` | 核心逻辑：方块移动/合并、新方块出现、胜负判断、键盘监听 |

**操作**：方向键控制滑动，相同数字合并，出现 2048 胜利，无法移动则失败

### 2. 魔方游戏

| 文件 | 职责 |
|------|------|
| `MainFrame.java` | 主窗口，键盘监听（D/→顺时针，S/←逆时针） |
| `Canvas_Cube.java` | 核心画布，魔方渲染、面旋转动画（多线程）、鼠标选面/拖拽旋转 |
| `CenterBlock.java` | 中心块（6个），决定面颜色 |
| `CornerBlock.java` | 角块（8个），3面交汇 |
| `EdgeBlock.java` | 棱块（12个），2面交汇 |
| `Square.java` | 小正方形面，3D顶点+颜色，背面剔除绘制 |
| `Point3D.java` | 3D点，支持绕X/Y/Z/任意轴旋转、透视投影到2D |
| `VTs.java` | 视图坐标到屏幕坐标的平移/翻转转换 |

**魔方结构**：6中心块 + 8角块 + 12棱块 = 26块，6面（蓝/红/绿/橙/黄/白）

**操作**：鼠标点击选面，D/→顺时针旋转，S/←逆时针旋转，鼠标拖拽旋转整体视角

### 3. 表白二维码

| 文件 | 职责 |
|------|------|
| `Code01.java` | 使用 ZXing 库生成 QR Code 二维码图片 |
| `core-3.3.3.jar` | ZXing 二维码编解码核心库 |

**功能**：将文本内容（如"我爱你"）编码为 400×400 的 JPG 二维码图片

---

## 技术要点

| 知识点 | 涉及项目 |
|--------|----------|
| Swing GUI（JFrame/JPanel/JLabel） | 2048、魔方 |
| 键盘/鼠标事件监听 | 2048、魔方 |
| 3D 图形渲染与透视投影 | 魔方 |
| 罗德里格斯旋转公式（任意轴旋转） | 魔方 |
| 多线程动画 | 魔方 |
| 双缓冲绘图 | 魔方 |
| 第三方库调用（ZXing） | 表白二维码 |
| 集合框架（Map/List） | 魔方、表白二维码 |

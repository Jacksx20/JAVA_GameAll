# JAVA_ Small Game Collection

**English** | **[中文](./README.md)**

A collection of Java mini-games built for practice, covering various Java knowledge areas:

1. Java Fundamentals (Language, Collection Framework, OOP, Design Patterns, etc.)

2. Advanced Java (JavaEE, Frameworks, Servers, Tools, etc.)

3. Multithreading and Concurrency

4. Java Virtual Machine

5. Database (SQL, MySQL, Redis, etc.)

6. Algorithms and Data Structures

---

## Project Overview

A Java mini-game collection containing 3 independent sub-projects, covering Java GUI (Swing), 3D graphics rendering, multi-threaded animation, event listeners, third-party library usage, and more.

---

## Directory Structure

```
JAVA_GameAll/
│
├── 2048/                          # 2048 Number Puzzle Game
│   ├── src/                       # Source code
│   │   ├── com/game/
│   │   │   ├── Block.java         # Block component, represents a single number cell (JLabel)
│   │   │   ├── My2048.java        # Main window class, game entry point (JFrame)
│   │   │   └── Operation.java     # Core logic: move/merge, spawn, win/lose, key listener
│   │   └── META-INF/
│   │       └── MANIFEST.MF       # JAR manifest
│   ├── out/                       # Compiled output
│   │   └── production/2048/
│   │       ├── com/game/          # Compiled .class files
│   │       └── META-INF/
│   └── README.md                  # 2048 game rules & strategy
│
├── MagicCube/                     # 3D Rubik's Cube Game
│   ├── src/                       # Source code
│   │   ├── Canvas_Cube.java       # Cube canvas, rendering/rotation/face selection (JPanel, core)
│   │   ├── CenterBlock.java       # Center block, one per face (1 Square face)
│   │   ├── CornerBlock.java       # Corner block, 3-face intersection (3 Square faces)
│   │   ├── EdgeBlock.java         # Edge block, 2-face intersection (2 Square faces)
│   │   ├── MainFrame.java         # Main window class, game entry point (JFrame)
│   │   ├── Square.java            # Small square face, 4 3D vertices + color, rotation & drawing
│   │   └── math3D/                # 3D math utility package
│   │       ├── Point3D.java       # 3D point class, rotation & perspective projection
│   │       ├── VTs.java           # View-to-screen coordinate conversion
│   │       ├── normal_hidden.class
│   │       ├── project3D_To_2D.class
│   │       ├── rotate_calculator.class
│   │       └── view_To_screen_coordinates.class
│   ├── bin/                       # Eclipse compiled output
│   ├── out/                       # IDEA compiled output
│   ├── .classpath                 # Eclipse classpath config
│   ├── .project                   # Eclipse project config
│   ├── MagicCube.iml              # IntelliJ module descriptor
│   └── REDEME.MD                  # Cube description
│
├── 表白二维码/                     # QR Code Generator
│   ├── src/                       # Source code
│   │   ├── day20200305/
│   │   │   └── Code01.java        # QR code generation main class (ZXing library)
│   │   └── lib/
│   │       └── core-3.3.3.jar    # ZXing QR code core library
│   ├── out/                       # Compiled output
│   │   └── production/表白二维码/
│   │       ├── day20200305/       # Compiled .class files
│   │       └── lib/
│   │           └── core-3.3.3.jar
│   ├── TT.jpg                     # Generated QR code image output
│   └── 表白二维码.iml              # IntelliJ module descriptor
│
└── README.md                      # Project overview
```

---

## Sub-Project Details

### 1. 2048 Game

| File | Description |
|------|-------------|
| `Block.java` | Number block component, displays different background colors based on value (0~4096 gradient mapping) |
| `My2048.java` | Main window, 4×4 grid layout, Substance look-and-feel |
| `Operation.java` | Core logic: block move/merge, new block spawn, win/lose detection, key listener |

**Controls**: Arrow keys to slide, same numbers merge, reaching 2048 wins, no more moves loses

### 2. Rubik's Cube Game

| File | Description |
|------|-------------|
| `MainFrame.java` | Main window, key listener (D/→ clockwise, S/← counter-clockwise) |
| `Canvas_Cube.java` | Core canvas, cube rendering, face rotation animation (multi-threaded), mouse face selection/drag rotation |
| `CenterBlock.java` | Center blocks (6), determines face color |
| `CornerBlock.java` | Corner blocks (8), 3-face intersection |
| `EdgeBlock.java` | Edge blocks (12), 2-face intersection |
| `Square.java` | Small square face, 3D vertices + color, back-face culling drawing |
| `Point3D.java` | 3D point, supports X/Y/Z/arbitrary axis rotation, perspective projection to 2D |
| `VTs.java` | View-to-screen coordinate translation and flip conversion |

**Cube Structure**: 6 center blocks + 8 corner blocks + 12 edge blocks = 26 blocks, 6 faces (blue/red/green/orange/yellow/white)

**Controls**: Click to select a face, D/→ rotate clockwise, S/← rotate counter-clockwise, drag to rotate whole cube view

### 3. QR Code Generator

| File | Description |
|------|-------------|
| `Code01.java` | Generates QR Code image using ZXing library |
| `core-3.3.3.jar` | ZXing barcode/QR code core library |

**Function**: Encodes text content (e.g. "I love you") into a 400×400 JPG QR code image

---

## Technical Highlights

| Topic | Projects |
|-------|----------|
| Swing GUI (JFrame/JPanel/JLabel) | 2048, Magic Cube |
| Keyboard/Mouse Event Listeners | 2048, Magic Cube |
| 3D Graphics Rendering & Perspective Projection | Magic Cube |
| Rodrigues' Rotation Formula (arbitrary axis rotation) | Magic Cube |
| Multi-threaded Animation | Magic Cube |
| Double Buffering Drawing | Magic Cube |
| Third-party Library (ZXing) | QR Code Generator |
| Collections Framework (Map/List) | Magic Cube, QR Code Generator |

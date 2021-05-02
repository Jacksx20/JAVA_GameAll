package day20200305;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码表白女神or感恩妈妈？
 * 
 * Jack20
 * core-3.3.3.jar
 * 
 * 
 */
public class Code01 {
	
	public static void main(String[] args) {
		getCode(400,400,"jpg","我爱你","C:\\Users\\Jack淳\\Desktop\\表白二维码\\TT.jpg");
	}
	
	//设置两个常量用来标注颜色
	private static final int BLACK = Color.BLACK.getRGB();
	private static final int WHITE = Color.WHITE.getRGB();
	
	/**
	 * 获取二维码图片
	 * @param width 图片宽度
	 * @param height 图片高度
	 * @param type 图片类型（jpg,png）
	 * @param content 二维码携带的内容
	 * @param path 存放的路径
	 * 
	 * 集合，IO文件的操作，图片，常量，jar
	 */
	public static void getCode(int width,int height,String type,String content,String path) {
		//1.设置二维码的基本信息（纠错等级，留白）
		
		Map map = new HashMap();
		//添加put()
		//字符集
		map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		//留白
		map.put(EncodeHintType.MARGIN, 2);
		//纠错等级	L(7%) M(15%) Q(25%) H(30%)
		map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		//二维码图片输出流
		MultiFormatWriter mu = new MultiFormatWriter();
		//encode():内容，码的类型，宽度，高度，基本信息
		//BitMatrix:二维矩阵类
		try {
			BitMatrix bit = mu.encode(content, BarcodeFormat.QR_CODE, width, height, map);
			
			//画图
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			
			for(int i=0; i<width; i++) {
				for(int j=0; j<height; j++) {
					//get(x,y):没有值false(白色)	有值true（黑色）
					//三目运算符
					int rgb = bit.get(i, j)?BLACK:WHITE;
					image.setRGB(i, j, rgb);
				}
			}
			
			File file = new File(path);
			//将图像写入到File文件中
			boolean flag = ImageIO.write(image,type,file);
			if(!flag) {
				System.out.println("垃圾，赶紧找静静好好学习");
			}
			System.out.println("你可真是太胖了");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		
		
	}
	
}











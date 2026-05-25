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
 * Code01类用于生成表白二维码图片。
 * 使用ZXing库将文本内容编码为QR Code二维码，
 * 并将生成的二维码图片保存为指定格式的文件。
 * 
 * 依赖: core-3.3.3.jar (ZXing二维码核心库)
 */
public class Code01 {
	
	/**
	 * 程序入口点，生成一个400x400的JPG格式二维码，
	 * 内容为"我爱你"，保存到指定路径。
	 * @param args 命令行参数
	 */
	public static void main(String[] args) {
		getCode(400,400,"jpg","我爱你","C:\\Users\\Jack\\Desktop\\表白二维码\\TT.jpg");
	}
	
	/** 二维码中黑色模块的RGB颜色值 */
	private static final int BLACK = Color.BLACK.getRGB();
	/** 二维码中白色模块的RGB颜色值 */
	private static final int WHITE = Color.WHITE.getRGB();
	
	/**
	 * 生成二维码图片并保存到指定路径。
	 * 步骤：
	 * 1. 设置二维码基本信息（字符集、留白边距、纠错等级）
	 * 2. 使用ZXing的MultiFormatWriter将内容编码为BitMatrix二维矩阵
	 * 3. 将BitMatrix转换为BufferedImage图片（黑色/白色模块）
	 * 4. 将图片写入文件
	 * 
	 * @param width   二维码图片宽度（像素）
	 * @param height  二维码图片高度（像素）
	 * @param type    图片格式类型（如"jpg"、"png"）
	 * @param content 二维码携带的文本内容
	 * @param path    输出图片文件的保存路径
	 */
	public static void getCode(int width,int height,String type,String content,String path) {
		// 1.设置二维码的基本信息（纠错等级，留白）
		Map map = new HashMap();
		// 字符集设为UTF-8，支持中文内容
		map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		// 留白边距设为2
		map.put(EncodeHintType.MARGIN, 2);
		// 纠错等级设为L(7%): L(7%) M(15%) Q(25%) H(30%)
		map.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		
		// 创建ZXing的多格式编码器
		MultiFormatWriter mu = new MultiFormatWriter();
		try {
			// 将内容编码为QR Code类型的BitMatrix二维矩阵
			// 参数: 内容, 码类型, 宽度, 高度, 基本信息配置
			BitMatrix bit = mu.encode(content, BarcodeFormat.QR_CODE, width, height, map);
			
			// 创建BufferedImage用于绘制二维码图片
			BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
			
			// 遍历矩阵的每个像素点，根据BitMatrix的值设置黑色或白色
			for(int i=0; i<width; i++) {
				for(int j=0; j<height; j++) {
					// bit.get(i,j): 有值true(黑色)，无值false(白色)
					int rgb = bit.get(i, j)?BLACK:WHITE;
					image.setRGB(i, j, rgb);
				}
			}
			
			// 将图像写入到指定路径的文件中
			File file = new File(path);
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

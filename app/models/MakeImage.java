/*
 *	画像の生成クラス
 *	@author Kotaro Nishida
 */
package models;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MakeImage {

	public static Image createImage(int width,int height){
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

    public static int a(int c){
        return c>>>24;
    }
    public static int r(int c){
        return c>>16&0xff;
    }
    public static int g(int c){
        return c>>8&0xff;
    }
    public static int b(int c){
        return c&0xff;
    }
    public static int rgb(int r,int g,int b){
        return 0xff000000 | r <<16 | g <<8 | b;
    }
    public static int argb(int a,int r,int g,int b){
        return a<<24 | r <<16 | g <<8 | b;
    }

	/**
	* イメージ→バイト列に変換
	* @param img イメージデータ
	* @param format フォーマット名
	* @return バイト列
	*/
	public static byte[] getBytesFromImage(BufferedImage img, String format) throws IOException{

		if(format == null) {
			format = "png";
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, format, baos);
		return baos.toByteArray();
	}

	/**

	* バイト列→イメージを作成
	* @param bytes
	* @return イメージデータ
	*/
	public static BufferedImage getImageFromBytes(byte[] bytes) throws IOException{
		ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
		BufferedImage img = ImageIO.read(baos);
		return img;
	}

}

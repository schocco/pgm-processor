/**
 * 
 */
package com.is_gr8.imageprocessor;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author rocco
 *
 */
public class PgmProcessor {

	/**
	 * 
	 */
	public PgmProcessor() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * returns a copy of the image with all bytes inverted.
	 * @param img the img to be inverted
	 * @return shallow copy with inverted bytes
	 */
	public static PgmImage invert(PgmImage img){
		PgmImage pgm = PgmImage.clone(img);
		byte[][] body = pgm.getPixels();
		
		for(int row = 0; row < body.length; row++){
			for(int col = 0; col < body[row].length; col++){
				// invert all bits and only keep the 8 lower bits b4
				// casting to byte again
				body[row][col] = (byte) (~body[row][col] & 0xff);
			}
		}
		pgm.setPixels(body);
		return pgm;
	}
	
	/**
	 * Write image to disk
	 * @param img
	 * @throws IOException 
	 */
	public static void writeToDisk(PgmImage img) throws IOException{
		FileWriter fw = new FileWriter("imagename.pgm");
		fw.write("just a test");
		byte[][] body = img.getPixels();
		for(int row = 0; row < body.length; row++){
			for(int col = 0; col < body[row].length; col++){
				// invert all bits and only keep the 8 lower bits b4
				// casting to byte again
				fw.append((char) body[row][col]);
			}
			fw.append(System.getProperty("line.separator"));
		}
		
	}

}

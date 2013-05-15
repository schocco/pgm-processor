/**
 * 
 */
package com.is_gr8.imageprocessor;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * See http://en.wikipedia.org/wiki/Netpbm_format for PGM info. Full
 * documentation at http://netpbm.sourceforge.net/doc/pbm.html.
 * 
 * @author rocco
 * 
 */
public class PgmImage {
	/** log4j logger. */
	Logger logger = Logger.getLogger(PgmImage.class);
	
	/** source file of the image. */
	private File file;
	/** the PGM magic number. Either P1, P2, P3, P4, P5 or P6. */
	private String magicNumber;
	/** image width. */
	private int width;
	/** image heigth. */
	private int height;
	/** Numbers of grey between black and white (only for PGM and PPM). */
	private int maxValue;
	/** pixel bytes. First index for rows, second for columns. */
	private byte[][] pixels;

	/** returns a shallow copy of the image. The File reference remains identical. */
	static PgmImage clone(PgmImage img){
		return new PgmImage(img.getFile());
	}
	
	
	/** default constructor. */
	public PgmImage() {

	}

	/**
	 * @param src
	 *            the source file of the image.
	 */
	public PgmImage(final File src) {
		this.file = src;
		this.readFile();
	}

	/**
	 * @return the file
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Reads the meta info of the img
	 * and trys to load all pixel data into memory. Use with caution when handling
	 * large files!
	 * 
	 * Based on http://stackoverflow.com/questions/11922252/reading-a-pgm-file-in-java
	 */
	private void readFile() {
		try {

			BufferedReader br = new BufferedReader(new FileReader(file));
			this.magicNumber = br.readLine(); // first line contains P2 or P5
			String line = br.readLine();
			while (line.startsWith("#")) { //ignore comments
				line = br.readLine();
			}
			Scanner s = new Scanner(line);
			this.width = s.nextInt();
			this.height = s.nextInt();
			line = br.readLine();// third line contains maxVal
			s = new Scanner(line);
			this.maxValue = s.nextInt();
			
			this.pixels = new byte[height][width];

			int count = 0;
			int b = 0;
			try {
				while (count < height * width) {
					b = br.read();
					if (b < 0)
						break;

					if (b == '\n') { // do nothing if new line encountered
					}
					else {
						if ("P5".equals(this.magicNumber)) { // Binary format
							pixels[count / width][count % width] = (byte) b;//(byte) ((b >> 8) & 0xFF);
							count++;
							pixels[count / width][count % width] = (byte) b;//(byte) (b & 0xFF);
							count++;
						} else { // ASCII format
							pixels[count / width][count % width] = (byte) b;
							count++;
						}
					}
				}
			} catch (EOFException eof) {
				eof.printStackTrace(System.out);
			}
			logger.debug("Height=" + height);
			logger.debug("Width=" + height);
			logger.debug("Required elements=" + (height * width));
			logger.debug("Obtained elements=" + count);
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			return;
		}
	}


	/**
	 * @param file
	 *            the file to set
	 */
	public final void setFile(final File src) {
		this.file = src;
	}

	/**
	 * @return the pixels
	 */
	public final byte[][] getPixels() {
		return pixels;
	}

	/**
	 * @param pixels the pixels to set
	 */
	public final void setPixels(byte[][] pixels) {
		this.pixels = pixels;
	}

	/**
	 * @return the magicNumber
	 */
	public final String getMagicNumber() {
		return magicNumber;
	}

	/**
	 * @return the width
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * @return the maxValue
	 */
	public final int getMaxValue() {
		return maxValue;
	}

}

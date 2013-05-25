/**
 * 
 */
package com.is_gr8.imageprocessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
		this.readHeader();
		this.readBody();
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
	private void readHeader() {
		
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
			line = br.readLine(); // third line contains maxVal
			s = new Scanner(line);
			this.maxValue = s.nextInt();
			this.pixels = new byte[height][width];
			br.close();

			logger.debug("Height=" + height);
			logger.debug("Width=" + width);
			logger.debug("Required elements=" + (height * width));
		} catch (Throwable t) {
			t.printStackTrace(System.err);
			return;
		}
	}


	
	private void readBody() {
		DataInputStream stream;
		try {
			stream = new DataInputStream(new BufferedInputStream(new FileInputStream(this.file)));
			int newlinecount = 0;
			char previous = (char) 'a';
			char c;
			do{
				c = (char) stream.readByte();
				if(c == '\n' || c == '\r'){
					newlinecount++;
					logger.debug("Encountered newline.");
				}
				if(c == (char) '#' && (previous == '\n' || previous == '\r')){
					newlinecount--;
					logger.debug("Encountered comment");
				}
				previous = c;
			} while(newlinecount < 3);
			logger.debug("Skipped header. Start reading binary content...");
			for(int row = 0; row < this.height; row++){
				for(int col = 0; col < this.width; col++){
					byte b = stream.readByte();
					this.pixels[row][col] = b;
				}
			}
		} catch (FileNotFoundException e1) {
			logger.error("File not found", e1);
		} catch (IOException e) {
			logger.error("IO stuff went wrong when reading the file body.", e);
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
	
	public final HashMap<String,String> getInfoMap(){
		HashMap<String,String> infomap = new HashMap<String,String>();
		infomap.put("Magic Number", magicNumber);
		infomap.put("Width", String.valueOf(width));
		infomap.put("Height", String.valueOf(height));
		infomap.put("Max Value", String.valueOf(maxValue));
		infomap.put("File path", String.valueOf(file.getAbsolutePath()));
		infomap.put("File size", String.format("%d Bytes", file.length()));
		return infomap;
	}

}

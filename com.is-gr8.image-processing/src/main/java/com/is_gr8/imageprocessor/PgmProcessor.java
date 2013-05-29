/**
 * 
 */
package com.is_gr8.imageprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 * @author rocco
 *
 */
public class PgmProcessor {

	private static Logger logger = Logger.getLogger(PgmProcessor.class);
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
	public static PgmImage invert(final PgmImage img, boolean clone){
		PgmImage pgm;
		if(clone){
			pgm = PgmImage.clone(img);
		} else {
			pgm = img;
		}
		
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
	
	public static PgmImage smooth(final PgmImage img, final int intensity){
		byte[][] pixels = img.getPixels();
		byte[][] blurred = new byte[pixels.length][pixels[0].length];
		
		for(int row=0; row<pixels.length; row++){
			for(int col=0; col<pixels[row].length; col++){
				blurred[row][col] = getAverageValue(pixels, row, col, intensity);
			}
		}
		img.setPixels(blurred);
		return img;
	}
	
	private static byte getAverageValue(byte[][] pixels, int row, int col, int intensity) {
		//    3 2 1 0 1 2 3
		// 3  X X X X X X X
		// 2  X X X X X X X
		// 1  X X X X X X X
		// 0  X X X _ X X X
		// 1  X X X X X X X
		// 2  X X X X X X X
		// 3  X X X X X X X		
		
		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics();
		//distance from the central pixel
		int distance = intensity - 1 / 2;
		//iterate through the array to collect all surrounding pixels
		int rowcount = row + distance;
		int colcount = col + distance;
		
		for(int r = row - distance; row < rowcount; row++){
			for(int c = col - distance; col < colcount; col++){
				try{
					stats.addValue(pixels[r][c] & 0xff);
				} catch(ArrayIndexOutOfBoundsException e){
					logger.debug("message: " + e.getMessage());
					//TODO: use mid pixel instead
					//ignore out of bounds.
					//occurs for corner/border pixels only
				}
			}
		}
		// Compute some statistics
		byte mean = (byte) stats.getMean();
		return mean;
		//TODO: multiply bucket with mask/kernel
		//kernel.addMask(stats)
	}

	/**
	 * Counts how often a color value occurs in the image.
	 * @param img pgm image
	 * @return array where each index represents a color value and the
	 * value represents the number of occurences.
	 */
	public static double[] getHistogram(final PgmImage img){
		double[] distribution = new double[img.getMaxValue()+1];
		byte[][] pixels = img.getPixels();
		for(byte[] arr : pixels){
			for(byte b : arr){
				distribution[(int) b & 0xff] += 1;
			}
		}
		return distribution;
	}
	
	/**
	 * Returns the raw data and does not count the number of occurences as opposed
	 * to {@link #getHistogram(PgmImage)}.
	 * This is needed by the jfreechartlibrary for histogram chart creation.
	 * @param img
	 * @return array where each field represents a pixel.
	 */
	public static double[] getHistogramData(final PgmImage img){
		if(img.getPixels() == null || img.getPixels().length < 1){
			throw new IllegalArgumentException("image must have a body.");
		}
		byte[][] pixels = img.getPixels();
		int n = 0;
		double[] d = new double[pixels.length * pixels[0].length];
		for(byte[] arr : pixels){
			for(byte i: arr){
				d[n++] = i & 0xff;
			}
		}
		return d;
	}
	
	/**
	 * Write image to disk
	 * @param img pgm image
	 * @throws IOException 
	 */
	public static void writeToDisk(final PgmImage img, String path) throws IOException{
		StringWriter sw = new StringWriter();
		String linesep = System.getProperty("line.separator");
		sw.write(img.getMagicNumber());
		sw.write(linesep);
		sw.write(String.format("%d %d", img.getWidth(), img.getHeight()));
		sw.write(linesep);
		sw.write(img.getMaxValue() + "");
		sw.write(linesep);

		logger.debug("Header string to be written: \n" + sw.toString());
		
		String s = sw.toString();    
		byte[] header = s.getBytes();
		byte[][] body = img.getPixels();
		
		FileOutputStream output = new FileOutputStream(path);
		output.write(header);
		
		for(int row = 0; row < body.length; row++){
			for(int col = 0; col < body[row].length; col++){
				output.write(body[row][col]);
			}
		}
		output.close();
		img.setFile(new File(path));
		
	}

}

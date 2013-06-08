/**
 * 
 */
package com.is_gr8.imageprocessor.convolution;

import org.apache.log4j.Logger;

import com.is_gr8.imageprocessor.PgmProcessor;

/**
 * @author rocco
 *
 */
public class PrewittThread implements Runnable {
	Logger logger = Logger.getLogger(PrewittThread.class);
	private byte[][] pixels;
	private byte[][] edges;
	int row;
	Kernel horizontal;
	Kernel vertical;
	
	/**
	 * 
	 */
	public PrewittThread(byte[][] pixels2, byte[][] edges2, int row2, Kernel hoz, Kernel vert) {
		this.pixels = pixels2;
		this.edges = edges2;
		this.row = row2;
		this.horizontal = hoz;
		this.vertical = vert;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for(int col=0; col<pixels[row].length; col++){
			//buckets.add(getBucket(pixels, row, col, intensity));
			PixelBucket hozBucket = PgmProcessor.getBucket(pixels, row, col, horizontal);
			PixelBucket vertBucket = PgmProcessor.getBucket(pixels, row, col, vertical);
			double gy = hozBucket.getEndValue();
			double gx = vertBucket.getEndValue();
			try{
				double gradient = Math.sqrt(gy * gy + gx * gx);
				edges[row][col] = (byte) Math.round(gradient);
			} catch(ArithmeticException ex){
				// could occur when the kernel wasnt initialized properly.
				logger.error("Could not calculate new pixelvalue. Aborting.");
				break;
			}	
		}

	}

}

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
public class HoughThread implements Runnable {
	private Logger logger = Logger.getLogger(HoughThread.class);
	private int row;
	private int cols;
	private int[][] accumulator;
	private int[] weights;
	private double theta;
	private byte[][] pixels;
	private int r;
	private int rMax;
	private static final int THETA_STEP = 2;
	private static final int COLOR_THRESHOLD = 10;
	
	/**
	 * 
	 * @param row
	 * @param accumulator
	 * @param weights
	 * @param pixels
	 */
	public HoughThread(int row, int[][] accumulator, int[] weights, byte[][] pixels) {
		this.row = row;
		this.rMax = accumulator.length;
		this.accumulator = accumulator;
		this.weights = weights;
		this.pixels = pixels;
		cols = pixels[0].length;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		for (int col = 0; col < cols; col++) {
			//ignore white pixels:
			if(255 - COLOR_THRESHOLD > (pixels[row][col] & 0xff)){
				// get r for 0 < theta < 2 pi (360°)
				for(int t = 0; t < PgmProcessor.THETA_MAX; t += THETA_STEP){
					theta = (double) t * Math.PI / 180; //TODO: could be cached for better performance
					// r = x cos theta + y sin theta
					r = (int) Math.round((row * Math.cos(theta) + col * Math.sin(theta)));

					if(r > 0 && r < rMax){
						//use weight to differentiate between dark and light values
						//FIXME: potential concurrent write issue
						accumulator[r][t] += weights[pixels[row][col] & 0xff]; 
					}
				}
			}
		}

	}

}

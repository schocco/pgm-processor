/**
 * 
 */
package com.is_gr8.imageprocessor.convolution;

import org.apache.log4j.Logger;

/**
 * @author rocco
 *
 */
public class HoughThread implements Runnable {
	private Logger logger = Logger.getLogger(HoughThread.class);
	private int row;
	private int cols;
	private int thetaMax;
	private int[][] accumulator;
	private int[] weights;
	private double theta;
	private byte[][] pixels;
	private int r;
	private int rMax;
	
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
		this.thetaMax = 180;
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
			if(255 > (pixels[row][col] & 0xff)){
				// get r for 0 < theta < 2 pi (360°)
				for(int t = 0; t<thetaMax; t++){
					theta = Math.toRadians(t); //inexact transformation
					// r = x cos theta + y sin theta
					r = (int) (row * Math.cos(theta) + col * Math.sin(theta));
					// increment accumulator[r][theta]
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

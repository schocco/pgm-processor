/**
 * 
 */
package com.is_gr8.imageprocessor.convolution;


import com.is_gr8.imageprocessor.PgmImage;

/**
 * Class to store results of hough transform.
 * @author rocco
 *
 */
public class HoughTransResult {
	/**
	 * Image that was used as an input for the hough transform.
	 */
	private PgmImage originalImage;
	/**
	 * accumulator array as image representation.
	 */
	private PgmImage accumulatorImage;
	/**
	 * accumulator array.
	 */
	private int[][] accumulator;

	/**
	 * 
	 */
	public HoughTransResult() {
		// TODO Auto-generated constructor stub
	}
	
	public HoughTransResult(PgmImage img) {
		this.originalImage = img;
	}

	/**
	 * @return the originalImage
	 */
	public final PgmImage getOriginalImage() {
		return originalImage;
	}

	/**
	 * @param originalImage the originalImage to set
	 */
	public final void setOriginalImage(PgmImage originalImage) {
		this.originalImage = originalImage;
	}

	/**
	 * @return the accumulatorImage
	 */
	public final PgmImage getAccumulatorImage() {
		return accumulatorImage;
	}

	/**
	 * @param accumulatorImage the accumulatorImage to set
	 */
	public final void setAccumulatorImage(PgmImage accumulatorImage) {
		this.accumulatorImage = accumulatorImage;
	}

	/**
	 * @return the accumulator
	 */
	public final int[][] getAccumulator() {
		return accumulator;
	}

	/**
	 * @param accumulator the accumulator to set
	 */
	public final void setAccumulator(int[][] accumulator) {
		this.accumulator = accumulator;
	}

}

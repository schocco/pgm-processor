package com.is_gr8.imageprocessor.blur;

/**
 * created May 29, 2013
 *
 * NxN Kernel with the weights to be used for the blurring.
 * Will be multiplied with the bucket fields.
 *
 * @author Rocco
 * 
 */
public class Kernel {
	/** number of rows/colums of the NxN mask. */
	private int size;
	/** 2d array containing the weights of the kernel. */
	private int[][] weights = null;
	/** sum of all weights. */
	private int sum = 0;
	
	/**
	 * @param size number of rows/cols
	 */
	private Kernel(int size){
		this.size = size;
		this.weights = new int[size][size];
	}
	
	/**
	 * 
	 * @param size number of rows/cols
	 * @return gaussian convolution kernel
	 */
	public static Kernel getGaussianKernel(final int size){
		Kernel k = new Kernel(size);
		//TODO: set gaussian blur mask
		return k;
	}


	/**
	 * 
	 * @param size number of rows/cols
	 * @return square convolution kernel
	 */
	public static Kernel getSquareKernel(final int size){
		Kernel k = new Kernel(size);
		for(int r = 0; r < k.weights.length; r++){
			for(int c = 0; c < k.weights[r].length; c++){
				k.weights[r][c] = 1;
			}
		}
		k.sum = size * size;
		return k;
	}
	
	/**
	 * @return the size
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * @return the weights
	 */
	public final int[][] getWeights() {
		return weights;
	}

	/**
	 * @return the sum
	 */
	public final int getSum() {
		return sum;
	}

}

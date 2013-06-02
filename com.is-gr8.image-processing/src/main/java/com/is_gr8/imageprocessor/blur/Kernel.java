package com.is_gr8.imageprocessor.blur;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.log4j.Logger;



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
	/** logger. */
	private static Logger logger = Logger.getLogger(Kernel.class);
	
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
		int mean = size/2;
		double sigma = 1.02;
		Gaussian nd = new Gaussian(mean, sigma);
		
		double[] multipliers = new double[size];
		//get row of numbers
		double s = 1.0 / nd.value(size-1);
		
		for(int i=0; i< size; i++){
			multipliers[i] = nd.value(i) * s;
		}
		
		logger.debug("Creating 2d Gaussian Kernel.");
		for(int r=0; r<k.weights.length; r++){
			StringBuilder sb = new StringBuilder();
			for(int c=0; c<k.weights[r].length; c++){
				double decker = (multipliers[r] * multipliers[c])/Math.pow(multipliers[0], 2);
				k.weights[r][c] = (int) Math.round(decker);
				k.sum += (int) Math.round(decker);
				sb.append(String.format("%d\t", k.weights[r][c]));
			}
			logger.info(sb.toString());
		}
		
		//FIXME: numbers are pretty large. it might be better to store doubles instead of
		// integers and to apply the division
		// directly in the kernel to avoid huge multiplications in the blur process
		return k;
	}
	
	/**
	 * @deprecated for testing only
	 * @param args
	 */
	public static void main(String[] args){
		System.out.println("\nsize 3:");
		getGaussianKernel(3);
		System.out.println("\nsize 5:");
		getGaussianKernel(5);
		System.out.println("\nsize 7:");
		getGaussianKernel(7);
		System.out.println("\nsize 9:");
		getGaussianKernel(9);
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

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
	private double[][] weights = null;
	/** logger. */
	private static Logger logger = Logger.getLogger(Kernel.class);
	/**
	 * standard deviation to be used for the creation of the gaussian
	 * convolution mask. Higher values flatten the curve.
	 */
	private static final double SIGMA = 1.02;
	
	/**
	 * @param size number of rows/cols
	 */
	private Kernel(int size){
		this.size = size;
		this.weights = new double[size][size];
	}
	
	/**
	 * 
	 * @param size number of rows/cols
	 * @return gaussian convolution kernel
	 */
	public static Kernel getGaussianKernel(final int size){
		logger.debug("Creating 2d Gaussian Kernel.");
		
		Kernel k = new Kernel(size);
		int mean = size/2;
		double sum = 0.0;
		Gaussian nd = new Gaussian(mean, SIGMA);
		
		double[] multipliers = new double[size];
		//get row of numbers and multiply so that corners are 1s
		double s = 1.0 / nd.value(size-1);		
		for(int i=0; i< size; i++){
			multipliers[i] = nd.value(i) * s;
		}
		
		for(int r=0; r<k.weights.length; r++){
			StringBuilder sb = new StringBuilder();
			for(int c=0; c<k.weights[r].length; c++){
				double decker = (multipliers[r] * multipliers[c])/Math.pow(multipliers[0], 2);
				k.weights[r][c] = Math.round(decker);
				sum += Math.round(decker);
				sb.append(String.format("%.1f\t", k.weights[r][c]));
			}
			logger.info(sb.toString());
		}
		
		divideBySum(k, sum);
		
		return k;
	}

	/**
	 * @param kernel
	 * @param sum the sum of all fields in the kernel
	 */
	private static void divideBySum(Kernel kernel, double sum) {
		//divide values by the sum
		for(int r=0; r<kernel.weights.length; r++){
			for(int c=0; c<kernel.weights[r].length; c++){
				kernel.weights[r][c] /= sum;
			}
		}
	}


	/**
	 * 
	 * @param size number of rows/cols
	 * @return square convolution kernel
	 */
	public static Kernel getSquareKernel(final int size){
		Kernel k = new Kernel(size);
		double sum = size * size;
		for(int r = 0; r < k.weights.length; r++){
			for(int c = 0; c < k.weights[r].length; c++){
				k.weights[r][c] = 1;
			}
		}
		divideBySum(k, sum);
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
	public final double[][] getWeights() {
		return weights;
	}

}

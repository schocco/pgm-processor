package com.is_gr8.imageprocessor.convolution;

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
	/** direction. */
	public enum Direction {HORIZONTAL, VERTICAL};
	
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
	 * Creates a prewitt edge filter.
	 * The higher the distance from the central row, the heigher the weight.
	 * So a row in a 5x5 horizontal kernel will consist of [-2, -1, 0, 1, 2].
	 * 
	 * @param size number of rows/cols
	 * @param direction whether the kernel should be used for horizontal or vertical edge detection
	 * @return a kernel
	 */
	public static Kernel getPrewittFilter(final int size, final Direction direction) {
		double[] row = new double[size];
		Kernel kernel = new Kernel(size);
		StringBuilder sb = new StringBuilder();
		int startvalue = -size/2;
		
		switch (direction) {
		case HORIZONTAL:
			logger.debug("Creating horizontal prewitt filter...");
			//create row that can be used multiple times
			for(int i = 0; i < size; i++){
				sb.append(startvalue + "\t");
				row[i] = startvalue++;				
			}
			for(int i = 0; i < size; i++){
				kernel.weights[i] = row;
				logger.debug(sb.toString());
			}
			break;
		case VERTICAL:
			logger.debug("Creating vertical prewitt filter...");
			for(int n = size-1; n >= 0; n--){
				for(int i = 0; i < size; i++){
					sb.append(startvalue + "\t");
					kernel.weights[i][n] = startvalue;					
				}
				startvalue++;
				logger.debug(sb.toString());
				sb = new StringBuilder();
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported filter direction");
		}
		return kernel;
	}
	
	
	//----------------------------------------------------------
	// ACCESSORS 
	//----------------------------------------------------------
	
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

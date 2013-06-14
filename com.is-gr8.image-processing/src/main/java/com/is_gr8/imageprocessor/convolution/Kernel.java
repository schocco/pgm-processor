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
	/**
	 * 
	 */
	private static final double LOG_SIGMA = 0.8;
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
		int startvalue = -size/2; //FIXME: no weights for prewitt kernel, either -1 or 1
		
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
	
	
	/**
	 * 
	 * @param size
	 *            size of the kernel (only 5 is a valid size atm)
	 * @return
	 */
	public static Kernel getLaplaceOfGaussianKernel(int size) {
		Kernel kernel = new Kernel(size);

		switch (size) {
			case 3:
				double[][] weights3 = { { 0, 1, 0 }, { 1, -4, 1 }, { 0, 1, 0 } };
				kernel.weights = weights3;
				break;
			case 5:
				double[][] weights5 = { { 0.0, 0.0, -1.0, 0.0, 0.0 },
						{ 0.0, -1.0, -2.0, -1.0, 0.0 },
						{ -1.0, -2.0, 16.0, -2.0, -1.0 },
						{ 0.0, -1.0, -2.0, -1.0, 0.0 },
						{ 0.0, 0.0, -1.0, 0.0, 0.0 } };
				kernel.weights = weights5;
				break;
			default:
				throw new IllegalArgumentException("Unsupported kernel size.");
		}
		return kernel;
	}

	/**
	 * @deprecated Does not return satisfactory results. use hardcoded approximations instead.
	 * @param size size of the kernel (only 5 is a valid size atm)
	 * @return
	 */
	public static Kernel calcLaplaceOfGaussianKernel(int size){
		Kernel kernel = new Kernel(size);
		logger.debug("Creating Laplace of Gaussian kernel...");
		//make sure outer corner is 1 when rounded
		double[][] weights = new double[size][size];
		double sum = 0.0;
		double multiplier = 1.0/Kernel.laplaceOfGaussian(0, 0, LOG_SIGMA, size);
		for(int r = 0; r < size; r++){
			StringBuilder sb = new StringBuilder();
			for(int c = 0; c < size; c++){
				double val = Math.round(multiplier * Kernel.laplaceOfGaussian(r, c, LOG_SIGMA, size));
				weights[r][c] = val;
				sb.append(String.format("%.8f\t", val));
				sum += val;
			}
			//logger.debug(sb.toString());
		}
		
		//make sure sum is zero, increment fields around the center until
		// sum is zero
		logger.debug("Sum is: " + sum);
		if(sum != 0){
			//change every field by sum/n_fields to make sure sum is zero
			double fraction = sum/Math.pow(size, 2);
			sum = 0;
			logger.debug("subtracting " + fraction);
			for(int r = 0; r<size; r++){
				StringBuilder sb = new StringBuilder();
				for(int c = 0; c<size; c++){
					weights[r][c] -= fraction;
					sb.append(String.format("%.3f\t", weights[r][c]));
					sum += weights[r][c];
				}
				logger.debug(sb.toString());
			}
			logger.debug("New sum: " + sum);
		}
		kernel.weights = weights;
		return kernel;
	}
	
	/**
	 * Laplace of Gaussian function.
	 * See http://academic.mu.edu/phys/matthysd/web226/Lab02.htm
	 * 
	 * @param x 		x coordinate
	 * @param y 		y coordinate
	 * @param sigma 	1.4 should be fine as a value
	 * @param size		size of the kernel. Is used to determine the center/offset for the function
	 * @return The double value for the given coordinate
	 */
	public static double laplaceOfGaussian(int x, int y, double sigma, int size){
		// LoG(x,y) = -1 / pi*sigma^4 * [ 1 - (x^2+y^2)/(2sigma^2) ] e^(-(x^2+y^2)/(2sigma^2)
		// coefficient: (x^2+y^2)/(2sigma^2)
		// LoG(x,y) = -1 / pi*sigma^4 * [ 1 - coefficient) ] e^(- coefficient)
		final double SIGMA = sigma;
		x = x - size/2;
		y = y - size/2;
		final double coefficient = (Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(SIGMA, 2));
		double result = -1 / (Math.PI * Math.pow(SIGMA, 4)) * (1 - coefficient) * Math.pow(Math.E, -coefficient);
		
		return result;
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

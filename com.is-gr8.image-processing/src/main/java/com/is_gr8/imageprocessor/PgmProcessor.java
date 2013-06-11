/**
 * 
 */
package com.is_gr8.imageprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.is_gr8.imageprocessor.convolution.HoughThread;
import com.is_gr8.imageprocessor.convolution.Kernel;
import com.is_gr8.imageprocessor.convolution.Kernel.Direction;
import com.is_gr8.imageprocessor.convolution.PixelBucket;
import com.is_gr8.imageprocessor.convolution.PrewittThread;

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
	
	/**
	 * 
	 * @param img
	 * @param intensity
	 * @param kernel The kernel to use (e.g. Gaussian)
	 * @return
	 */
	public static PgmImage blur(final PgmImage img, final Kernel kernel){
		byte[][] pixels = img.getPixels();
		byte[][] blurred = new byte[pixels.length][pixels[0].length];
		
		//this loop could be a subtask, applied on a row basis. (4 rows for each task e.g.)
		//all rows are equally large, perfect for a workerpool framework
		outerloop:
		for(int row=0; row<pixels.length; row++){
			for(int col=0; col<pixels[row].length; col++){
				//buckets.add(getBucket(pixels, row, col, intensity));
				PixelBucket bucket = getBucket(pixels, row, col, kernel);
				try{
					blurred[row][col] = (byte) Math.round(bucket.getEndValue());
				} catch(ArithmeticException ex){
					// could occur when the kernel wasnt initialized properly.
					blurred = pixels;
					logger.error("Could not calculate new pixelvalue. Aborting.");
					break outerloop;
				}	
			}
		}
		img.setPixels(blurred);
		return img;
	}
	
	/**
	 * Runs the prewittEdgeDetection on the source image.
	 * <pre>
	 * A: source image
	 * G: gradient magnitude
	 * 
	 * G_x: A convergenced with horizontal kernel
	 * G_y: A convergenced with vertical kernel
	 * 
	 * G = sqrt(G_x^2 + G_y^2)
	 * </pre>
	 * 
	 * @param img 	the image to be transformed
	 * @param size	size of the kernels to be used
	 * @return image with changed body values. Pixel values are replaced with gradient magnitude values.
	 */
	public static PgmImage parallelPrewittEdgeDetection(final PgmImage img,
			final int size) {
		long start = System.currentTimeMillis();
		
		byte[][] pixels = img.getPixels();
		byte[][] edges = new byte[pixels.length][pixels[0].length];

		Kernel horizontal = Kernel.getPrewittFilter(size, Direction.HORIZONTAL);
		Kernel vertical = Kernel.getPrewittFilter(size, Direction.VERTICAL);

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for (int row = 0; row < pixels.length; row++) {
			Runnable worker = new PrewittThread(pixels, edges, row, horizontal,
					vertical);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		long stop = System.currentTimeMillis();
		long duration = stop -start;
		logger.debug("Edge detection took: " + duration + "ms.");
		img.setPixels(edges);
		return img;
	}
	
	/**
	 * @deprecated in favor of the multithreaded {@link #parallelPrewittEdgeDetection(PgmImage, int)}
	 * Runs the prewittEdgeDetection on the source image.
	 * <pre>
	 * A: source image
	 * G: gradient magnitude
	 * 
	 * G_x: A convergenced with horizontal kernel
	 * G_y: A convergenced with vertical kernel
	 * 
	 * G = sqrt(G_x^2 + G_y^2)
	 * </pre>
	 * 
	 * @param img 	the image to be transformed
	 * @param size	size of the kernels to be used
	 * @return image with changed body values. Pixel values are replaced with gradient magnitude values.
	 */
	public static PgmImage prewittEdgeDetection(final PgmImage img, final int size){
		long start = System.currentTimeMillis();
		byte[][] pixels = img.getPixels();
		byte[][] edges= new byte[pixels.length][pixels[0].length];
		
		Kernel horizontal = Kernel.getPrewittFilter(size, Direction.HORIZONTAL);
		Kernel vertical = Kernel.getPrewittFilter(size, Direction.VERTICAL);
		
		//this loop could be a subtask, applied on a row basis. (4 rows for each task e.g.)
		//all rows are equally large, perfect for a workerpool framework
		outerloop:
		for(int row=0; row<pixels.length; row++){
			for(int col=0; col<pixels[row].length; col++){
				//buckets.add(getBucket(pixels, row, col, intensity));
				PixelBucket hozBucket = getBucket(pixels, row, col, horizontal);
				PixelBucket vertBucket = getBucket(pixels, row, col, vertical);
				double gy = hozBucket.getEndValue();
				double gx = vertBucket.getEndValue();
				try{
					double gradient = Math.sqrt(gy * gy + gx * gx);
					edges[row][col] = (byte) Math.round(gradient);
				} catch(ArithmeticException ex){
					// could occur when the kernel wasnt initialized properly.
					logger.error("Could not calculate new pixelvalue. Aborting.");
					edges = pixels;
					break outerloop;
				}	
			}
		}
		long stop = System.currentTimeMillis();
		long duration = stop -start;
		logger.debug("Edge detection took: " + duration + "ms.");
		img.setPixels(edges);
		return img;
	}
	
	/**
	 * 
	 * @param img 		pgm image
	 * @param kernel	kernel to be used
	 * @return			pgm image
	 */
	public static PgmImage logEdgeDetection(final PgmImage img, final Kernel kernel){
		byte[][] pixels = img.getPixels();
		int[][] edges= new int[pixels.length][pixels[0].length];
		outerloop:
		for(int row=0; row<pixels.length; row++){
			for(int col=0; col<pixels[row].length; col++){
				PixelBucket bucket = getBucket(pixels, row, col, kernel);
				try{
					edges[row][col] = (int) Math.round(bucket.getEndValue());
				} catch(ArithmeticException ex){
					// could occur when the kernel wasnt initialized properly.
					logger.error("Could not calculate new pixelvalue. Aborting.");
					//edges = pixels;
					break outerloop;
				}	
			}
		}
		byte[][] bytes = normalize(edges, 3);
		img.setPixels(bytes);
		return img;
	}
	
	/**
	 * 
	 * @param pixels the array with the original image data
	 * @param row row position (index, zero-based)
	 * @param col column position (index, zero-based)
	 * @param intensity size of the bucket
	 * @return the bucket with the surrounding pixels
	 */
	public static PixelBucket getBucket(final byte[][] pixels, final int row, final int col, final Kernel kernel) {
		int intensity = kernel.getSize();
		//distance from the central pixel
		int distance = (intensity - 1) / 2;
		//iterate through the array to collect all surrounding pixels
		int rowcount = row + distance;
		int colcount = col + distance;
		//the bucket to be returned
		PixelBucket bucket = new PixelBucket(row, col, kernel);
		
		for(int r = row - distance; r <= rowcount; r++){
			for(int c = col - distance; c <= colcount; c++){
				try{
					bucket.addPixel((int) (pixels[r][c] & 0xff));
				} catch(ArrayIndexOutOfBoundsException e){
					// occurs for corner/border pixels only
					// use the mid-pixel 
					bucket.addPixel((int) (pixels[row][col] & 0xff));
				}
			}
		}
		return bucket;
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
	 * 
	 * @param pixels 	2d matrix with img pixels
	 * @param crop		percentage of pixels that should be ignored to eliminate outliers
	 * @return			byte array with normalized values (0-255)
	 */
	public static byte[][] normalize(final int[][] pixels, final int crop){
		int maxVal = 0;
		int minVal = 255;
		int[] flatPixels = new int[pixels.length * pixels[0].length];
		byte[][] normalized = new byte[pixels.length][pixels[0].length];

		if(crop < 0 || crop > 30){
			throw new IllegalArgumentException("crop argument must be in between 0 and 30");
		}
		
		//make 1d array
		int i = 0;
		for(int r = 0; r < pixels.length; r++){
			for(int c = 0; c< pixels[0].length; c++){
				flatPixels[i++] = pixels[r][c];
				if(pixels[r][c] < minVal){
					minVal = pixels[r][c];
				}
				if(pixels[r][c] > maxVal){
					maxVal = pixels[r][c];
				}
			}
		}
		logger.debug("minVal (uncropped): " + minVal);
		logger.debug("maxVal (uncropped): " + maxVal);
		
		if(crop > 0){
			Arrays.sort(flatPixels);
			// distance from border to get n %
			int distance = (int) Math.round(flatPixels.length*(crop/100.0));
			minVal = flatPixels[distance];
			maxVal = flatPixels[flatPixels.length - distance];
			
			logger.debug("minVal: " + minVal);
			logger.debug("maxVal: " + maxVal);
		}
		//normalize
		for(int r = 0; r < pixels.length; r++){
			for(int c = 0; c< pixels[0].length; c++){
				double pixel = pixels[r][c];
				if(pixel <= minVal){
					normalized[r][c] = 0;
				} else if(pixel >= maxVal){
					normalized[r][c] = (byte) 255;
				}
				normalized[r][c] = (byte) ((pixel + Math.abs(minVal)) / (maxVal - minVal) * 255);
			}
		}
		return normalized;
	}

	/**
	 * 
	 * @param pgm
	 * @return
	 */
	public static PgmImage houghTransform(PgmImage pgm){

		int[][] accumulator = houghTransformAccumulate(pgm.getPixels());
		
		PgmImage accumulatorPgm = getAccumulatorImage(accumulator);
		
		try {
			writeToDisk(accumulatorPgm, pgm.getFile().getName() + "accu.pgm");
		} catch (IOException e) {
			logger.debug("could not save accumulator image.");
			e.printStackTrace();
		}
		
		int threshold = 100;
		// get all accumulator fields that are higher than a threshold
		for(int i = 0; i< accumulator.length; i++){
			for(int p = 0; p < accumulator[i].length; p++){
				if(accumulator[i][p] >= threshold){
					//logger.debug(String.format("Exceeded threshold at (%d,%d): %d", i, p, accumulator[i][p]));
				}
			}
		}
		return accumulatorPgm; //FIXME: should return something that can be used to draw lines in swt
	}
	
	/**
	 * 
	 * @param pixels pixel 2d array of the image
	 * @return accumulator array with results of the hough transform
	 */
	private static int[][] houghTransformAccumulate(byte[][] pixels) {
		int r;
		int rows = pixels.length;
		int cols = pixels[0].length;
		int rMax = (int) (Math.sqrt(Math.pow(rows, 2) + Math.pow(cols, 2)));
		int thetaMax = 360;
		int[][] accumulator = new int[rMax+1][thetaMax+1];
		for(int i = 0; i< rMax; i++){
			for(int p = 0; p<thetaMax; p++){
				accumulator[i][p] = 0;
			}
		}
		
		//only calculate weights once to avoid divisions in the inner loop
		int[] weights = new int[255];
		for(int i = 0; i < 255; i++){
			weights[i] = 2550 / (10 * (1+i));
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		// for each pixel:
		for (int row = 0; row < rows; row++) {
			HoughThread t = new HoughThread(row, accumulator, weights, pixels);
			executor.execute(t);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

		return accumulator;
	}
	
	/**
	 * 
	 * @param acc
	 * @return
	 */
	private static PgmImage getAccumulatorImage(int[][] acc){
		byte[][] pixels = normalize(acc, 0);
		PgmImage pgm = new PgmImage();
		pgm.setPixels(pixels);
		pgm.setHeight(pixels.length);
		pgm.setWidth(pixels[0].length);
		pgm.setMagicNumber("P5");
		pgm.setMaxValue(255);
		return pgm;
	}
	
	
	/**
	 * Write image to disk.
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

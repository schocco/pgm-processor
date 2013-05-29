package com.is_gr8.imageprocessor.blur;

/**
 * created May 29, 2013
 * 
 * Collection of surrounding pixels that is used to blur the mid pixel.
 *
 * @author Rocco
 * 
 */
public class PixelBucket {

	/** Size of the bucket (NxN pixels). */
	private int size;
	/** row position of the pixel this bucket belongs to. */
	private int row;
	/** col position of the pixel this bucket belongs to. */
	private int col;
	/** 2d array containing the pixel and its
	 * surrounding pixels. The first element is the upper left pixel. */
	private int[][] pixels = null;
	
	
	
}

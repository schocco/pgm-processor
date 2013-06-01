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
	/** Sum of the pixels contained in the bucket. */
	private int sum = 0;
	/** row position of the pixel this bucket belongs to. */
	private int row;
	/** col position of the pixel this bucket belongs to. */
	private int col;
	/** 2d array containing the pixel and its
	 * surrounding pixels. The first element is the upper left pixel. */
	private int[][] pixels = null;
	/**pixel counter. incremented for each added pixel. */
	private int pixelcounter = 1;
	/** Kernel to use for blurring. */
	private Kernel kernel;
	
	/**
	 * 
	 * @param size The size of this bucket. Has to be the same size as the kernel being used.
	 */
	public PixelBucket(int size){
		this.size = size;
		pixels = new int[size][size];
	}
	
	/**
	 * 
	 * @param size The size of this bucket. Has to be the same size as the kernel being used.
	 * @param row row position of the pixel
	 * @param col col position of the pixel
	 */
	public PixelBucket(int row, int col, final Kernel kernel){
		this(kernel.getSize());
		this.row = row;
		this.col = col;
		this.kernel = kernel;
	}
	
	/**
	 * Stateful way of adding pixels. This method automatically increments an internal counter,
	 * starting from the top left corner of the bucket.
	 * The pixel value is stored in the buckets matrix ({@link #pixels})
	 * 
	 * @param pixel the pixel value
	 */
	public void addPixel(int pixel){
		if(pixelcounter > size*size){
			throw new IllegalStateException("The bucket is full already (and so am I)!");
		}
		int row = getRowFor(pixelcounter);
		int col = getColFor(pixelcounter);
		pixels[row][col] = pixel;
		pixelcounter++;
		sum += pixel * kernel.getWeights()[row][col];
	}
	
	/**
	 * 
	 * @param pixelcounter2
	 * @return column index
	 */
	private int getColFor(int pixelcounter2) {
		return (pixelcounter2 - 1) % size;
	}

	/**
	 * 
	 * @param pixelcounter2
	 * @return row index
	 */
	private int getRowFor(int pixelcounter2) {
		return (pixelcounter2 -1 ) / size;
	}

	// --------------------------
	// ACCESSORS
	// --------------------------

	/**
	 * @return the size
	 */
	public int getSum() {
		return sum;
	}
	
	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}
	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}
	/**
	 * @return the col
	 */
	public int getCol() {
		return col;
	}
	/**
	 * @param col the col to set
	 */
	public void setCol(int col) {
		this.col = col;
	}
	/**
	 * @return the pixels
	 */
	public int[][] getPixels() {
		return pixels;
	}
	/**
	 * @param pixels the pixels to set
	 */
	public void setPixels(int[][] pixels) {
		this.pixels = pixels;
	}
		
	
}

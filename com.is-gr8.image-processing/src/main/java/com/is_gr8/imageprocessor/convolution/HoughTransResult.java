/**
 * 
 */
package com.is_gr8.imageprocessor.convolution;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Point;

import com.is_gr8.imageprocessor.PgmImage;

/**
 * Class to store results of hough transform.
 * @author rocco
 *
 */
public class HoughTransResult {
	private Logger logger = Logger.getLogger(HoughTransResult.class);
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
	
	/**
	 * 
	 * @param count number of lines to be extracted
	 * @return array of coordinates for lines.
	 * 		the x field in the point objects represent rs, the y points represent thetas
	 */
	public HashMap<Point, Integer> getMaxima(int count){
		//sort map by size and pick maxima according to count
		//issue: neighbouring values might represent the same line and come before other high value thta represent different lines.
		// could be avoided by using less granularity when filling the accumulator array
		int[] high = new int[count+1]; //the additional field is for new values
		Arrays.fill(high, 0);
		HashMap<Point, Integer> map = new HashMap<Point, Integer>();
		
		for(int r=0; r<accumulator.length; r++){
			for(int t=0; t<accumulator[0].length; t++){
				if(accumulator[r][t] > high[1]){
					high[0] = accumulator[r][t];
					Point p = new Point(r,t);
					map.put(p, accumulator[r][t]);
					Arrays.sort(high);
				}
			}
		}
		logger.debug("Highest values: " + Arrays.toString(high));
		
		Iterator<Point> it = map.keySet().iterator();
		
		while(it.hasNext()){
			Point p = it.next();
			if(map.get(p) > high[0]){
				logger.debug("Coordinates: " + p.x + " " + p.y + "\tval: " + map.get(p));
				logger.debug("points:");
			} else{
				it.remove();
			}
		}
		
		return map;
	}

}

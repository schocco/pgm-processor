package com.is_gr8.imageprocessor;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.is_gr8.imageprocessor.convolution.Kernel;
import com.is_gr8.imageprocessor.convolution.Kernel.Direction;

/**
 * @author rocco
 *
 */
public class PgmProcessorTest {
	/** base path for storing manipulated files. */
	private String destination = "src/test/resources/processed/";
	/** image to be used for testing. */
	private PgmImage malbunSmall;
	/** large image to be used for performance testing. */
	private PgmImage malbunLarge;
	/** image to be used for testing. */
	private PgmImage fireSmall;
	/** large image to be used for performance testing. */
	private PgmImage fireLarge;
	/** list of test images. */
	private PgmImage[] testImages = new PgmImage[4];
	
	private boolean setUpIsDone = false;


	/**
	 * Re-read the original files before each manipulation test
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception{
	    if (setUpIsDone) {
	        return;
	    }
	    File f = new File(destination);
	    if(!f.exists()){
	    	f.mkdir();
	    }
		malbunSmall = new PgmImage(new File("src/test/resources/malbun.resized.pgm"));
		malbunLarge = new PgmImage(new File("src/test/resources/malbun.pgm"));
		fireSmall = new PgmImage(new File("src/test/resources/fire.resized.pgm"));
		fireLarge = new PgmImage(new File("src/test/resources/fire.pgm"));
		testImages[0] = malbunSmall;
		testImages[1] = fireSmall;
		testImages[2] = malbunLarge;
		testImages[3] = fireLarge;
		setUpIsDone = true;
	}
	
	/**
	 * Test method for {@link com.is_gr8.imageprocessor.PgmProcessor#invert(com.is_gr8.imageprocessor.PgmImage, boolean)}.
	 */
	@Test
	public void testInvert() {
		for(PgmImage pgm : testImages){
			pgm = PgmProcessor.invert(pgm, false);
			try {
				PgmProcessor.writeToDisk(pgm, destination + pgm.getFile().getName() + ".inverted.pgm");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Save was not successful.");
			}
		}
		
	}

	/**
	 * Test method for {@link com.is_gr8.imageprocessor.PgmProcessor#blur(com.is_gr8.imageprocessor.PgmImage, com.is_gr8.imageprocessor.convolution.Kernel)}.
	 */
	@Test
	public void testBlurSquare() {
		Kernel kernel = Kernel.getGaussianKernel(5);
		
		for(PgmImage pgm : testImages){
			pgm = PgmProcessor.blur(pgm, kernel);
			try {
				PgmProcessor.writeToDisk(pgm, destination + pgm.getFile().getName() + ".blurredSquare.pgm");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Save was not successful.");
			}
		}
	}
	
	/**
	 * Test method for {@link com.is_gr8.imageprocessor.PgmProcessor#blur(com.is_gr8.imageprocessor.PgmImage, com.is_gr8.imageprocessor.convolution.Kernel)}.
	 */
	@Test
	public void testBlurGaussian() {
		Kernel kernel = Kernel.getSquareKernel(5);
		
		for(PgmImage pgm : testImages){
			pgm = PgmProcessor.blur(pgm, kernel);
			try {
				PgmProcessor.writeToDisk(pgm, destination + pgm.getFile().getName() + ".blurredGaussian.pgm");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Save was not successful.");
			}
		}
	}

	/**
	 * Test method for {@link com.is_gr8.imageprocessor.PgmProcessor#prewittEdgeDetection(com.is_gr8.imageprocessor.PgmImage, com.is_gr8.imageprocessor.convolution.Kernel, com.is_gr8.imageprocessor.convolution.Kernel)}.
	 */
	@Test
	public void testPrewittEdgeDetection() {
		for(PgmImage pgm : testImages){
			pgm = PgmProcessor.prewittEdgeDetection(pgm, 5);
			try {
				PgmProcessor.writeToDisk(pgm, destination + pgm.getFile().getName() + ".edgePrewitt.pgm");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Save was not successful.");
			}
		}
	}
	
	@Test
	public void testParallelPrewittEdgeDetection() {
		for(PgmImage pgm : testImages){
			pgm = PgmProcessor.parallelPrewittEdgeDetection(pgm, 5);
			try {
				PgmProcessor.writeToDisk(pgm, destination + pgm.getFile().getName() + ".edgePrewitt2.pgm");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Save was not successful.");
			}
		}
	}

	/**
	 * Test method for {@link com.is_gr8.imageprocessor.PgmProcessor#logEdgeDetection(com.is_gr8.imageprocessor.PgmImage, com.is_gr8.imageprocessor.convolution.Kernel)}.
	 */
	@Test
	public void testLogEdgeDetection() {
		Kernel kernel = Kernel.getLaplaceOfGaussianKernel(5);
		
		for(PgmImage pgm : testImages){
			pgm = PgmProcessor.logEdgeDetection(pgm, kernel);
			try {
				PgmProcessor.writeToDisk(pgm, destination + pgm.getFile().getName() + ".edgeLOG.pgm");
			} catch (IOException e) {
				e.printStackTrace();
				fail("Save was not successful.");
			}
		}
	}

}

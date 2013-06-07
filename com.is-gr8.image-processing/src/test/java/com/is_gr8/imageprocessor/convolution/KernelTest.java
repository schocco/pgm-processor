/**
 * 
 */
package com.is_gr8.imageprocessor.convolution;

import static org.junit.Assert.*;

import org.junit.Test;

import com.is_gr8.imageprocessor.convolution.Kernel;
import com.is_gr8.imageprocessor.convolution.Kernel.Direction;

/**
 * @author rocco
 *
 */
public class KernelTest {

	@Test
	public void testPrewitt() {
		assertFalse(null == Kernel.getPrewittFilter(3, Direction.HORIZONTAL).getWeights());
		assertFalse(null == Kernel.getPrewittFilter(5, Direction.HORIZONTAL).getWeights());
		assertFalse(null == Kernel.getPrewittFilter(7, Direction.HORIZONTAL).getWeights());
		assertFalse(null == Kernel.getPrewittFilter(9, Direction.HORIZONTAL).getWeights());
		
		assertFalse(null == Kernel.getPrewittFilter(3, Direction.VERTICAL).getWeights());
		assertFalse(null == Kernel.getPrewittFilter(5, Direction.VERTICAL).getWeights());
		assertFalse(null == Kernel.getPrewittFilter(7, Direction.VERTICAL).getWeights());
		assertFalse(null == Kernel.getPrewittFilter(9, Direction.VERTICAL).getWeights());
	}

}

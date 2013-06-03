/**
 * 
 */
package com.is_gr8.imageprocessor.blur;

import static org.junit.Assert.*;

import org.junit.Test;

import com.is_gr8.imageprocessor.blur.Kernel.Direction;

/**
 * @author rocco
 *
 */
public class KernelTest {

	@Test
	public void testPrewitt() {
		assertFalse(null == Kernel.getPrewittFilter(3, Direction.HORIZONTAL));
		assertFalse(null == Kernel.getPrewittFilter(5, Direction.HORIZONTAL));
		assertFalse(null == Kernel.getPrewittFilter(7, Direction.HORIZONTAL));
		assertFalse(null == Kernel.getPrewittFilter(9, Direction.HORIZONTAL));
		
		assertFalse(null == Kernel.getPrewittFilter(3, Direction.VERTICAL));
		assertFalse(null == Kernel.getPrewittFilter(5, Direction.VERTICAL));
		assertFalse(null == Kernel.getPrewittFilter(7, Direction.VERTICAL));
		assertFalse(null == Kernel.getPrewittFilter(9, Direction.VERTICAL));
	}

}

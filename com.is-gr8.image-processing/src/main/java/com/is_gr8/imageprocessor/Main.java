package com.is_gr8.imageprocessor;

import org.eclipse.swt.widgets.Display;

import com.is_gr8.imageprocessor.ui.MainWindow;

/**
 * @author rocco
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		Display display = new Display();
		new MainWindow(display);
		display.dispose();
	}

}

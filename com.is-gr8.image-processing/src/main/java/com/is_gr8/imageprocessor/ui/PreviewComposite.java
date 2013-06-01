/**
 * 
 */
package com.is_gr8.imageprocessor.ui;


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.is_gr8.imageprocessor.ImageEvent;
import com.is_gr8.imageprocessor.ImageEventListener;
import com.is_gr8.imageprocessor.PgmImage;

/**
 * @author rocco
 *
 */
public class PreviewComposite extends Composite implements ImageEventListener{
	private ImageData idata;
	private Display display;
	Logger logger = Logger.getLogger(PreviewComposite.class);
	Label imgLabel;
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public PreviewComposite(Composite arg0, int arg1) {
		super(arg0, arg1);
		this.setLayout(new FillLayout());
		imgLabel = new Label(this, SWT.NONE);
	}
	
	public void drawImage(PgmImage pgm){
		//TODO: draw image from byte array stored in pgm object
		
		int depth = 8;
		PaletteData palette = new PaletteData(0, 0, 0);
		
		idata = new ImageData(pgm.getWidth(), pgm.getHeight(), depth, palette);
		
		byte[][] px = pgm.getPixels();
		
		for(int y = 0; y < px.length; y++){
			for(int x = 0; x < px[y].length; x++){
				idata.setPixel(x, y, px[y][x]);
			}
		}

		Image myImage = new Image( display, pgm.getFile().getAbsolutePath());
		ImageData a = myImage.getImageData();
		logger.debug("Opened pgm file with SWT...");
		logger.debug("Comparing image data");
		for(int i = 0; i < 60; i++){
			logger.debug("swt:\t" + (a.data[i*4+1] & 0xff) + "\town:\t " + (pgm.getPixels()[0][i] & 0xff));
		}
		imgLabel.setImage( myImage );
		
		this.layout();
	}
	
	/**
	 * @return the display
	 */
	public final Display getDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public final void setDisplay(Display display) {
		this.display = display;
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageOpened(com.is_gr8.imageprocessor.ImageEvent)
	 */
	@Override
	public void imageOpened(ImageEvent e) {
		drawImage(e.getImage());
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageSaved(com.is_gr8.imageprocessor.ImageEvent)
	 */
	@Override
	public void imageSaved(ImageEvent e) {
		drawImage(e.getImage());
		
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageHeaderChanged(com.is_gr8.imageprocessor.ImageEvent)
	 */
	@Override
	public void imageHeaderChanged(ImageEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageBodyChanged(com.is_gr8.imageprocessor.ImageEvent)
	 */
	@Override
	public void imageBodyChanged(ImageEvent e) {
		drawImage(e.getImage());
	}

}

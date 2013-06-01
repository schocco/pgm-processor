/**
 * 
 */
package com.is_gr8.imageprocessor.ui;


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
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
	/** image data for the creation of the swt image. */
	private ImageData idata;
	/** the display as used in the main window. */
	private Display display;
	/** logger. */
	private Logger logger = Logger.getLogger(PreviewComposite.class);
	/** label which wraps the img. */
	private Label imgLabel;
	
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
		//create image data from pgm object
		int depth = 8; //TODO: this should be stored in the pgm object
		PaletteData palette = new PaletteData(255, 255, 255);
		idata = new ImageData(pgm.getWidth(), pgm.getHeight(), depth, palette);
		
		//copy bytes into swt image data obj
		byte[][] px = pgm.getPixels();		
		int bytecounter = 0;
		for(int y = 0; y < px.length; y++){
			for(int x = 0; x < px[y].length; x++){
				//idata.setPixel(x, y, px[y][x]);
				idata.data[bytecounter++] = px[y][x];
			}
		}
		
		//create and display image
		Image img = new Image(display, idata);
		imgLabel.setImage(img);
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
		//can be ignored
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageHeaderChanged(com.is_gr8.imageprocessor.ImageEvent)
	 */
	@Override
	public void imageHeaderChanged(ImageEvent e) {
		//can be ignored		
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageBodyChanged(com.is_gr8.imageprocessor.ImageEvent)
	 */
	@Override
	public void imageBodyChanged(ImageEvent e) {
		drawImage(e.getImage());
	}

}

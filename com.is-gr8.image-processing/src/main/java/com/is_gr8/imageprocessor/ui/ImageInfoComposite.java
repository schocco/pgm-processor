package com.is_gr8.imageprocessor.ui;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.is_gr8.imageprocessor.ImageEvent;
import com.is_gr8.imageprocessor.ImageEventListener;

/**
 * created May 24, 2013
 *
 * @author Rocco Schulz <roccos@de.ibm.com>
 * 
 */
public class ImageInfoComposite extends Composite implements ImageEventListener {
	private HashMap<String,String> imageinfo;
	private static Logger logger = Logger.getLogger(ImageInfoComposite.class);
	
	public ImageInfoComposite(Composite parent, int style, HashMap<String,String> info) {
		super(parent, style);
		this.imageinfo = info;
		this.init();
		this.update();
	}
	
	public ImageInfoComposite(Composite parent, int style) {
		super(parent, style);
		this.init();
	}
	
	private void init(){
		this.setLayout(new GridLayout(2, false));
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	
	/**
	 * Clears the composite and adds labels for the keys and values of the provided map.
	 * @param imageinfo
	 */
	public void updateElements(HashMap<String,String> imageinfo){
		for (Control control : this.getChildren()) {
	        control.dispose();
	    }
		for(String label: imageinfo.keySet()){
			//griddata
			GridData data = new GridData();
			data.grabExcessHorizontalSpace = false;
			data.grabExcessVerticalSpace = false;
			data.horizontalAlignment = SWT.LEFT;
			//keys
			Label k = new Label(this, SWT.NONE);
			k.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			k.setText(label);
			//values
			Label v = new Label(this, SWT.NONE);
			v.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			String value = imageinfo.get(label);
			v.setText(value);
			logger.debug(value);
		}
		logger.debug("Updating tab contents.");
		this.layout();
		this.pack();
		this.getParent().layout();
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageOpened(com.is_gr8.imageprocessor.ImageEvent)
	 */
	public void imageOpened(ImageEvent e) {
		updateElements(e.getImage().getInfoMap());		
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageSaved(com.is_gr8.imageprocessor.ImageEvent)
	 */
	public void imageSaved(ImageEvent e) {
		// can be ignored.
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageHeaderChanged(com.is_gr8.imageprocessor.ImageEvent)
	 */
	public void imageHeaderChanged(ImageEvent e) {
		updateElements(e.getImage().getInfoMap());
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageBodyChanged(com.is_gr8.imageprocessor.ImageEvent)
	 */
	public void imageBodyChanged(ImageEvent e) {
		//can be ignored
	}

}

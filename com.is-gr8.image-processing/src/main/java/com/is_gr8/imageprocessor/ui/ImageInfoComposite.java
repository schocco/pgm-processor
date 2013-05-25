package com.is_gr8.imageprocessor.ui;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * created May 24, 2013
 *
 * @author Rocco Schulz <roccos@de.ibm.com>
 * 
 */
public class ImageInfoComposite extends Composite {
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
	
	public void updateElements(HashMap<String,String> imageinfo){
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
		this.getParent().layout();
	}

}

package com.is_gr8.imageprocessor.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

/**
 * created Jun 13, 2013
 *
 * @author Rocco Schulz <roccos@de.ibm.com>
 * 
 */
public class HoughLineDialog extends Dialog {
	
	/** tabfolder containing the lines and the accumulator image. **/
	private TabFolder tabFolder;
	/** preview composite for the lines. */
	//TODO:
	/** tab containing the rendered image. */
	private PreviewComposite previewComposite;

	
	//TODO: add constructor with results of hough transform
	// display accumulator in one tab and lines in second tab
	// add buttons to save or discard results
	
	/**
	 * @param arg0
	 */
	public HoughLineDialog(Shell arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public HoughLineDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	
	public boolean open(){
		Shell parent = getParent();
	    final Shell shell =
	  	      new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	  	    shell.setText("Blur Options");
	  	    shell.setLayout(new GridLayout(2, true));
	    shell.pack();
	    shell.open();
		init();

	    Display display = parent.getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }

	    return true;
	}
	
	private void init(){

		
	}

}

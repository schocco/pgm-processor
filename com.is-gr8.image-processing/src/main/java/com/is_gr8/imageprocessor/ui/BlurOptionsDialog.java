/**
 * Input Dialog to select blur options (size and convolution kernel)
 */
package com.is_gr8.imageprocessor.ui;

import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.is_gr8.imageprocessor.convolution.Kernel;

/**
 * @author rocco
 *
 */
public class BlurOptionsDialog extends Dialog {
	private Kernel kernel = null;
	private int size = 3;
	/** lookup map for size options. */
	private LinkedHashMap<String, Integer> sizeOptions = new LinkedHashMap<String, Integer>();
	private enum Type {SQUARE, GAUSSIAN};
	private Type type = Type.SQUARE;
			
	
	
	/**
	 * @param parent
	 */
	public BlurOptionsDialog(Shell parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public BlurOptionsDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	  /**
	   * Displays the dialog.
	   * 
	   * @return the kernel constructed from the netered values.
	   */
	  public final Kernel open() {
	    Shell parent = getParent();
	    final Shell shell =
	      new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
	    shell.setText("Blur Options");
	    shell.setLayout(new GridLayout(2, true));

	    // init size options
	    sizeOptions.put("3x3 px", 3);
	    sizeOptions.put("5x5 px", 5);
	    sizeOptions.put("7x7 px", 7);
	    sizeOptions.put("9x9 px", 9);
	    
	    // UI components
	    final Label sizeLabel = new Label(shell, SWT.NULL);
	    sizeLabel.setText("Intensity (kernel size):");

	    final Combo sizeCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
	    for(String option : sizeOptions.keySet()){
	    	sizeCombo.add(option);
	    }
	    sizeCombo.select(0);
	    
	    final Label typeLabel = new Label(shell, SWT.NULL);
	    typeLabel.setText("Blur type:");

	    final Combo typeCombo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
	    typeCombo.add("square");
	    typeCombo.add("gaussian");
	    typeCombo.select(0);
	    
	    final Button buttonOK = new Button(shell, SWT.PUSH);
	    buttonOK.setText("Ok");
	    buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    
	    final Button buttonCancel = new Button(shell, SWT.PUSH);
	    buttonCancel.setText("Cancel");
	    
	    // action listeneres
	    buttonOK.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	    	  if(type == Type.GAUSSIAN){
	    		  kernel = Kernel.getGaussianKernel(size);
	    	  } else {
	    		  kernel = Kernel.getSquareKernel(size);
	    	  }
	    	  shell.dispose();
	      }
	    });

	    buttonCancel.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	    	  kernel = null;
	    	  shell.dispose();
	      }
	    });
	    
	    sizeCombo.addListener(SWT.Modify, new Listener() {
		      public void handleEvent(Event event) {
		    	  String selection = sizeCombo.getItem(sizeCombo.getSelectionIndex());
		    	  size = sizeOptions.get(selection);
		      }
		    });
	    
	    typeCombo.addListener(SWT.Modify, new Listener() {
		      public void handleEvent(Event event) {
		    	  if(typeCombo.getSelectionIndex() == 0){
		    		  type = Type.SQUARE;
		    	  } else{
		    		  type = Type.GAUSSIAN;
		    	  }
		      }
		    });
	    
	    shell.addListener(SWT.Traverse, new Listener() {
	      public void handleEvent(Event event) {
	        if(event.detail == SWT.TRAVERSE_ESCAPE)
	          event.doit = false;
	      }
	    });

	    shell.pack();
	    shell.open();

	    Display display = parent.getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }

	    return kernel;
	  }

}

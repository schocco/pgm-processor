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
import com.is_gr8.imageprocessor.convolution.Kernel.Direction;

/**
 * @author rocco
 *
 */
public class EdgeOptionsDialog extends Dialog {
	private Kernel kernel = null;
	private int size = 3;
	/** lookup map for size options. */
	private LinkedHashMap<String, Integer> sizeOptions = new LinkedHashMap<String, Integer>();
	enum KernelType {LAPLACE_OF_GAUSSIAN, PREWITT};
	/** default type. */
	private KernelType type = KernelType.PREWITT;
			
	
	
	/**
	 * @param parent
	 */
	public EdgeOptionsDialog(Shell parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public EdgeOptionsDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	  /**
	   * Displays the dialog.
	   * 
	   * @return the kernel constructed from the netered values.
	   */
	  public final KernelType open() {
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
	    typeCombo.add("Laplace of Gaussian");
	    typeCombo.add("Prewitt");
	    typeCombo.select(1);
	    
	    final Button buttonOK = new Button(shell, SWT.PUSH);
	    buttonOK.setText("Ok");
	    buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
	    
	    final Button buttonCancel = new Button(shell, SWT.PUSH);
	    buttonCancel.setText("Cancel");
	    
	    // action listeneres
	    buttonOK.addListener(SWT.Selection, new Listener() {
	      public void handleEvent(Event event) {
	    	  if(type == KernelType.LAPLACE_OF_GAUSSIAN){
	    		  kernel = Kernel.getLaplaceOfGaussian(5); //TODO: add flexible size option
	    	  } else {
	    		  //FIXME: not designed to handle multiple kernels.
	    		  kernel = Kernel.getPrewittFilter(size, Direction.HORIZONTAL);
	    		  kernel = Kernel.getPrewittFilter(size, Direction.VERTICAL);
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
		    		  type = KernelType.LAPLACE_OF_GAUSSIAN;
		    	  } else{
		    		  type = KernelType.PREWITT;
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

	    return type;
	  }

	/**
	 * @return the kernel
	 */
	public final Kernel getLogKernel() {
		return kernel;
	}

	/**
	 * @return the size
	 */
	public final int getSize() {
		return size;
	}

}

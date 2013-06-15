package com.is_gr8.imageprocessor.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.is_gr8.imageprocessor.convolution.HoughTransResult;

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
	// TODO:
	/** tab containing the rendered image. */
	private PreviewComposite previewComposite;
	private Shell shell;
	private HoughTransResult result;

	// TODO: add constructor with results of hough transform
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

	public boolean open() {
		Shell parent = getParent();
		shell = new Shell(parent, SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL |  SWT.RESIZE);
		shell.setText("Hough Transform Result");
		shell.setLayout(new FillLayout());
		init();
		shell.setSize(result.getOriginalImage().getHeight(), result
				.getOriginalImage().getWidth());
		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return true;
	}

	private void init() {
		tabFolder = new TabFolder(shell, SWT.NONE);

		// the picture preview
		TabItem imgPreview = new TabItem(tabFolder, SWT.NULL);
		imgPreview.setText("Accumulator");

		previewComposite = new PreviewComposite(tabFolder, SWT.NONE);
		previewComposite.setDisplay(shell.getDisplay());
		previewComposite.drawImage(result.getAccumulatorImage());
		imgPreview.setControl(previewComposite);

		// tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
		// 1, 1));
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// the drawn lines
		TabItem houghLines = new TabItem(tabFolder, SWT.NULL);
		houghLines.setText("Extracted Lines");

		Image image1 = new Image(shell.getDisplay(), 300, 200);
		GC gc = new GC(image1);
		
		Canvas canvas = new Canvas(tabFolder, SWT.NONE);
//		canvas.setSize(result.getOriginalImage().getHeight(), result.getOriginalImage().getWidth());
		canvas.setBackground(getParent().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		houghLines.setControl(canvas);
		GridData data = new GridData();
		data.widthHint = 1000;
		data.heightHint = 1000;
		canvas.setLayoutData(data);
		// Add a handler to do the drawing
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// Get the canvas and its size
				Canvas canvas = (Canvas) e.widget;
				int maxX = canvas.getSize().x;
				int maxY = canvas.getSize().y;

				// Calculate the middle
				int halfX = (int) maxX / 2;
				int halfY = (int) maxY / 2;

		        Image image = new Image(shell.getDisplay(), 300, 200);
		        GC gc = new GC(image);
		        gc.drawLine(10,10,200,200);
		        gc.dispose();

		        e.gc.drawImage(image, 10, 10);
		        image.dispose();
			}
		});

	}

	/**
	 * @return the result
	 */
	public final HoughTransResult getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public final void setResult(HoughTransResult result) {
		this.result = result;
	}

}

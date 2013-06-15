package com.is_gr8.imageprocessor.ui;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
	private Logger logger = Logger.getLogger(HoughLineDialog.class);
	/** tabfolder containing the lines and the accumulator image. **/
	private TabFolder tabFolder;
	/** preview composite for the lines. */
	// TODO:
	/** tab containing the rendered image. */
	private PreviewComposite previewComposite;
	private Shell shell;
	private HoughTransResult result;

	/**
	 * @param arg0
	 */
	public HoughLineDialog(Shell arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public HoughLineDialog(Shell arg0, int arg1) {
		super(arg0, arg1);
	}

	/**
	 * Displays the dialog with the accumulator image and the lines extracted.
	 * @return true when file/s should be saved
	 */
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
			if (!display.readAndDispatch()){
				display.sleep();
			}
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

		
		Canvas canvas = new Canvas(tabFolder, SWT.NONE);
		canvas.setBackground(getParent().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		houghLines.setControl(canvas);
		// Add a handler to do the drawing
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				int width = result.getOriginalImage().getWidth();
				int height = result.getOriginalImage().getHeight();
				HashMap<Point, Integer> maxima = result.getMaxima(20);

		        Image image = new Image(shell.getDisplay(), width, height);
		        GC gc = new GC(image);
		        
				for(Point p : maxima.keySet()){
					Point[] ps = getLinePoints(p);
					if(ps[0] != null && ps[1] != null){
						gc.drawLine(ps[0].x, ps[0].y, ps[1].x, ps[1].y);
					} else {
						logger.error(String.format("Could not draw line for r=%d and t=%d", p.x, p.y));
					}
				}
				
		        
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
	
	/**
	 * 
	 * @param polarpoint in fact a line, not a point (r = x cos(theta)+y sin(theta))
	 * @return two points that can be used for drawing
	 */
	private Point[] getLinePoints(Point polarpoint){
		int width = result.getOriginalImage().getWidth();
		int height = result.getOriginalImage().getHeight();
		int r = polarpoint.x;
		int theta = polarpoint.y;
		//r = x cos(theta)+y sin(theta)
		//get start and end point on the border of the image
		Point p1 = new Point(0,0);
		Point p2 = new Point(0,0);
		Point p3 = new Point(0,0);
		Point p4 = new Point(0,0);
		
		Point[] validPoints = new Point[2];
		int indexCtr = 0;
		
		//try x = 0 with y = csc(theta) (r cos(theta))
		// y = r csc(theta)
		p1.x = 0;
		p1.y = (int) (r * 1 /Math.sin(theta));
		
		if(isValidPoint(p1)){
			logger.debug(String.format("P1(%d|%d)", p1.x, p1.y));
			validPoints[indexCtr++] = p1;
		}
		
		//try y = 0 -> x = r sec(theta)
		p2.x = (int) (r * 1 / Math.cos(theta));
		p2.y = 0;
		
		if(isValidPoint(p2)){
			logger.debug(String.format("P2(%d|%d)", p2.x, p2.y));
			validPoints[indexCtr++] = p2;
		}
		
		if(indexCtr == 2){
			return validPoints;
		}
		
		//try x = image.width
		// y = csc(theta) (r-image.width cos(theta))
		p3.x = width;
		p3.y = (int) (1 /Math.sin(theta) * (r - width * Math.cos(theta)));
		
		if(isValidPoint(p3)){
			logger.debug(String.format("P3(%d|%d)", p3.x, p3.y));
			validPoints[indexCtr++] = p3;
		}
		
		if(indexCtr == 2){
			return validPoints;
		}
		
		//try y = image.height
		//x = sec(theta) (r-100 sin(theta))
		
		p4.x = (int) (1 / Math.cos(theta) * (r - height * Math.sin(theta)));
		p4.y = height;
		
		
		if(isValidPoint(p4)){
			logger.debug(String.format("P4(%d|%d)", p4.x, p4.y));
			validPoints[indexCtr++] = p4;
		}
	
		return validPoints;
	}
	
	private boolean isValidPoint(Point p){
		final int tolerance = 10;
		int width = result.getOriginalImage().getWidth();
		int height = result.getOriginalImage().getHeight();
		return p.x >= 0 && p.x <= width + tolerance && p.y >= 0 && p.y <= height + tolerance;
	}

}

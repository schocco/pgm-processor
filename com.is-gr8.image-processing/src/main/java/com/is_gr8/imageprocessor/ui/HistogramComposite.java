/**
 * 
 */
package com.is_gr8.imageprocessor.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries.SeriesType;

import com.is_gr8.imageprocessor.ImageEvent;
import com.is_gr8.imageprocessor.ImageEventListener;
import com.is_gr8.imageprocessor.PgmImage;
import com.is_gr8.imageprocessor.PgmProcessor;

/**
 * @author rocco
 *
 */
public class HistogramComposite extends Composite implements ImageEventListener{
	private Logger logger = Logger.getLogger(HistogramComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public HistogramComposite(Composite parent, int style) {
		super(parent, SWT.None);
		this.setLayout(new FillLayout());
	}
	
	/**
	 * Draw a new Histogram for the image passed in as argument
	 * @param pgm new image
	 */
	public void update(PgmImage pgm){
		//clean up old contents
		for(Control x : this.getChildren()){
			x.dispose();
		}

		// create a chart
		Chart chart = new Chart(this, SWT.NONE);
		    
		// set titles
		chart.getTitle().setText("Histogram");
		chart.getAxisSet().getXAxis(0).getTitle().setText("Color value");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Occurences");

		// create bar series
		IBarSeries barSeries = (IBarSeries) chart.getSeriesSet()
		    .createSeries(SeriesType.BAR, pgm.getFile().getName());
		barSeries.setYSeries(PgmProcessor.getHistogram(pgm));

		// adjust the axis range
		chart.getAxisSet().adjustRange();
		
		chart.layout();
		this.layout();
	}
	

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageOpened()
	 */
	public void imageOpened(ImageEvent e) {
		update(e.getImage());
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageSaved()
	 */
	public void imageSaved(ImageEvent e) {
		// can be ignored		
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageHeaderChanged()
	 */
	public void imageHeaderChanged(ImageEvent e) {
		// can be ignored		
	}

	/* (non-Javadoc)
	 * @see com.is_gr8.imageprocessor.ImageEventListener#imageBodyChanged()
	 */
	public void imageBodyChanged(ImageEvent e) {
		update(e.getImage());	
	}

}

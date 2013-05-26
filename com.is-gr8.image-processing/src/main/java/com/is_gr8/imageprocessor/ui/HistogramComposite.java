/**
 * 
 */
package com.is_gr8.imageprocessor.ui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import com.is_gr8.imageprocessor.PgmImage;
import com.is_gr8.imageprocessor.PgmProcessor;

/**
 * @author rocco
 *
 */
public class HistogramComposite extends Composite {
	private HistogramDataset dataset;
	private Logger logger = Logger.getLogger(HistogramComposite.class);

	/**
	 * @param parent
	 * @param style
	 */
	public HistogramComposite(Composite parent, int style) {
		super(parent, SWT.EMBEDDED);
		this.setLayout(new FillLayout());
	}
	
	/** draws the histogram with the provided data.
	 * @param pgm the pgm image*/
	public void update(PgmImage pgm){
		dataset = new HistogramDataset();
		dataset.setType(HistogramType.RELATIVE_FREQUENCY);

		int[] values = PgmProcessor.getHistogram(pgm);
		double[] vals = new double[values.length];
		for(int i=0; i<values.length; i++){
			vals[i] = values[i];
		}
		dataset.addSeries("H1", vals, vals.length);
		
		logger.debug(vals);
		logger.debug(vals.length);
		
		JFreeChart chart = ChartFactory.createHistogram("Histogram", "Color", "occurences", dataset, PlotOrientation.VERTICAL, false, false, false);
		Frame awtFrame = SWT_AWT.new_Frame(this);
		ChartPanel chartpanel = new ChartPanel(chart);
		JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(chartpanel, BorderLayout.NORTH);
        awtFrame.add(jPanel);
		this.pack();
		this.getParent().layout();
	}

}

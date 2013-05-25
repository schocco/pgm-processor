/**
 * 
 */
package com.is_gr8.imageprocessor.ui;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;

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

/**
 * @author rocco
 *
 */
public class HistogramComposite extends Composite {
	private HistogramDataset dataset;

	/**
	 * @param parent
	 * @param style
	 */
	public HistogramComposite(Composite parent, int style) {
		super(parent, SWT.EMBEDDED);
		this.setLayout(new FillLayout());
	}
	
	/** draws the histogram with the provided data. */
	public void update(){
		dataset = new HistogramDataset();
		dataset.setType(HistogramType.FREQUENCY);

		double[] values = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
		dataset.addSeries("H1", values, 10, 0.0, 10.0);
		
		JFreeChart chart = ChartFactory.createHistogram("Histogram", "Color", "occurences", dataset, PlotOrientation.VERTICAL, false, false, false);
		Frame awtFrame = SWT_AWT.new_Frame(this);
		ChartPanel chartpanel = new ChartPanel(chart);
		JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.add(chartpanel, BorderLayout.NORTH);
        awtFrame.add(jPanel);
		//ChartComposite composite = new ChartComposite(this, SWT.NONE);
		this.pack();
		this.getParent().layout();
	}


	
	//TODO: Window or frame to display a histogram.
	// constructor should take distribution array.
	// use jfreecharts histogramdataset.

}

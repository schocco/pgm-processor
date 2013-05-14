package com.is_gr8.imageprocessor.ui;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.is_gr8.imageprocessor.PgmImage;
import com.is_gr8.imageprocessor.PgmProcessor;

/**
 * @author rocco
 * The application window.
 */
public class MainWindow {
	/** logger. */
	private Logger logger = Logger.getLogger(MainWindow.class);
	/** shell width. */
	private static final int XSIZE = 400;
	/** shell length. */
	private static final int YSIZE = 500;
	/** the shell. */
	private Shell shell;
	/** currently selected file. */
	File selectedFile = null;

	/**
	 * Create and display the shell.
	 * 
	 * @param display
	 *            display obj
	 */
	public MainWindow(final Display display) {
		shell = new Shell(display);
		shell.setText("PGM Processor");
		shell.setToolTipText("Assignment #1 by Rocco Schulz.");
		shell.setSize(XSIZE, YSIZE);
		// init shell elements
		this.init();
		this.center();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/** initializes the UI. */
	private void init() {
		shell.setLayout(new GridLayout(2, true));
		GridData data = new GridData(GridData.FILL_BOTH);

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Browse");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.NULL);
				String[] extensions = {"*.pgm"};
				dialog.setFilterExtensions(extensions);
				String path = dialog.open();
				if (path != null) {

					selectedFile = new File(path);
					if (selectedFile.isFile()) {
						logger.debug("is file:"
								+ selectedFile.getAbsolutePath());
						PgmImage img = new PgmImage(selectedFile);
						PgmImage inverted = PgmProcessor.invert(img);
						try {
							PgmProcessor.writeToDisk(inverted);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						logger.debug("multiple files or folder selected");
					}
				}
			}
		});
	}

	/**
	 * Centers the application window on the screen.
	 * 
	 * @param shell
	 *            shell obj
	 */
	private void center() {
		Rectangle bds = shell.getDisplay().getBounds();
		Point p = shell.getSize();
		int nLeft = (bds.width - p.x) / 2;
		int nTop = (bds.height - p.y) / 2;
		shell.setBounds(nLeft, nTop, p.x, p.y);
	}
}
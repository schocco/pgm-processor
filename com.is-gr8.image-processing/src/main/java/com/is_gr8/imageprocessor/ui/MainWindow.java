package com.is_gr8.imageprocessor.ui;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.is_gr8.imageprocessor.PgmImage;
import com.is_gr8.imageprocessor.PgmProcessor;

/**
 * @author rocco The application window.
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
	/** currently selected img. */
	private PgmImage currentImage = null;
	/** tab item which displays the image information. */
	private ImageInfoComposite imgInfoComposite;
	/** tab folder for the main contents. */
	private TabFolder tabFolder;
	/** scroller which holds the imginfocomposite. */
	private ScrolledComposite imginfoScroller;
	/** the composite which contains the histogram for the current image. */
	private HistogramComposite histoComposite;
	/** selection adapter for the open menu item. */
	private SelectionAdapter openSelectionAdapter = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(shell, SWT.NULL);
			String[] extensions = { "*.pgm" };
			dialog.setFilterExtensions(extensions);
			String path = dialog.open();
			if (path != null) {

				File selectedFile = new File(path);
				if (selectedFile.isFile()) {
					logger.debug("is file:" + selectedFile.getAbsolutePath());
					currentImage = new PgmImage(selectedFile);
					currentImage.addListener(imgInfoComposite);
					currentImage.addListener(histoComposite);
					
					currentImage.imgOpened();
					
					// update scroller size info for proper scroll bars
					Rectangle r = imginfoScroller.getClientArea();
					imginfoScroller.setMinHeight(tabFolder.computeSize(
							SWT.DEFAULT, r.height).y);
					imginfoScroller.setMinWidth(tabFolder.computeSize(
							SWT.DEFAULT, r.width).x);
					
				} else {
					logger.error("multiple files or folder selected");
					MessageBox errDialog = new MessageBox(shell, SWT.ICON_ERROR
							| SWT.OK);
					errDialog.setText("Invalid selection");
					errDialog
							.setMessage("The selected file could not be read.");
					errDialog.open();
				}
			}
		};
	};
	/** save selection adapter for the menu bar. */
	private SelectionAdapter saveSelectionAdapter = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			String[] extensions = { "*.pgm" };
			dialog.setFilterExtensions(extensions);
			dialog.setFileName("image.pgm");
			String path = dialog.open();
			if (path != null) {
				logger.debug("Save image to: " + path);
				File selectedFile = new File(path);
				selectedFile.exists();
				if (selectedFile.exists() && currentImage != null) {
					MessageBox errDialog = new MessageBox(shell,
							SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
					errDialog.setText("Replace");
					errDialog
							.setMessage("Do you want to overwrite the existing file?");
					try {
						int answer = errDialog.open();
						if (answer == SWT.OK) {
							PgmProcessor.writeToDisk(currentImage, path);
						}
					} catch (IOException e1) {
						logger.error("could not save file.", e1);
					} catch (SWTException e2) {
						logger.debug("dialog disposed.", e2);
					}
				} else if (currentImage != null) {
					try {
						PgmProcessor.writeToDisk(currentImage);
					} catch (IOException e1) {
						logger.error("could not save file.", e1);
					}

				} else {
					logger.error("Cannot write without having a current image.");
				}
			}
		};
	};

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
		shell.setLayout(new GridLayout(1, true));
		initMenuBar();
		initTabFolder();
	}

	/**
	 * Adds a menu to the window for opening and saving files.
	 */
	private void initMenuBar() {
		// add menu bar
		Menu appMenuBar = shell.getDisplay().getMenuBar();
		if (appMenuBar == null) {
			appMenuBar = new Menu(shell, SWT.BAR);
			shell.setMenuBar(appMenuBar);
		}
		Menu dropdown = new Menu(appMenuBar);
		Menu dropdown2 = new Menu(appMenuBar);

		// FILE TREE
		MenuItem file = new MenuItem(appMenuBar, SWT.CASCADE);
		file.setText("File");
		file.setMenu(dropdown);

		MenuItem open = new MenuItem(dropdown, SWT.PUSH);
		open.setText("Open...");
		open.addSelectionListener(openSelectionAdapter);

		MenuItem save = new MenuItem(dropdown, SWT.PUSH);
		save.setText("Save as...");
		save.addSelectionListener(saveSelectionAdapter);

		MenuItem exit = new MenuItem(dropdown, SWT.PUSH);
		exit.setText("Exit");
		exit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().dispose();
			};
		});

		// FUNCTIONS TREE
		MenuItem functions = new MenuItem(appMenuBar, SWT.CASCADE);
		functions.setText("Functions");
		functions.setMenu(dropdown2);

		MenuItem invert = new MenuItem(dropdown2, SWT.PUSH);
		invert.setText("invert");
		invert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentImage = PgmProcessor.invert(currentImage, false);
			};
		});
		
		MenuItem smooth = new MenuItem(dropdown2, SWT.PUSH);
		smooth.setText("smoothen");
		smooth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentImage = PgmProcessor.smooth(currentImage, 3);
			};
		});
	}

	/**
	 * Adds a tabbed folder with composites that contain image information,
	 * histogram etc.
	 */
	private void initTabFolder() {
		tabFolder = new TabFolder(shell, SWT.NONE);
		
		// image info tab
		TabItem imgInfoTab = new TabItem(tabFolder, SWT.NONE);
		imgInfoTab.setText("Image information");
		// we want the content to scroll
		imginfoScroller = new ScrolledComposite(tabFolder, SWT.H_SCROLL	| SWT.V_SCROLL);
		imginfoScroller.setLayout(new FillLayout());
		// the actual content
		imgInfoComposite = new ImageInfoComposite(imginfoScroller, SWT.NONE);
		// which goes as content to the scrolled composite
		imginfoScroller.setContent(imgInfoComposite);
		imginfoScroller.setExpandVertical(true);
		imginfoScroller.setExpandHorizontal(true);
		imginfoScroller.setMinHeight(tabFolder.computeSize(SWT.DEFAULT,	SWT.DEFAULT).y);
		imginfoScroller.setMinWidth(tabFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT).x);
		imginfoScroller.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				// recalculate height in case the resize makes texts
				// wrap or things happen that require it
				Rectangle r = imginfoScroller.getClientArea();
				imginfoScroller.setMinHeight(tabFolder.computeSize(SWT.DEFAULT,	r.height).y);
				imginfoScroller.setMinWidth(tabFolder.computeSize(SWT.DEFAULT, r.width).x);
			}
		});
		// the scroller gets the control of the tab item
		imgInfoTab.setControl(imginfoScroller);

		
		// histogram
		TabItem imgHisto = new TabItem(tabFolder, SWT.NULL);
		imgHisto.setText("Histogram");
		
		histoComposite = new HistogramComposite(tabFolder, SWT.NONE);
		histoComposite.update();
		imgHisto.setControl(histoComposite);
		// experimental scroll pane
		// set the minimum width and height of the scrolled content - method 2
		final ScrolledComposite sc2 = new ScrolledComposite(tabFolder,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		TabItem exp = new TabItem(tabFolder, SWT.NULL);
		exp.setControl(sc2);

		final Composite c2 = new Composite(sc2, SWT.NONE);
		sc2.setContent(c2);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		c2.setLayout(layout);

		// the picture preview
		TabItem imgPreview = new TabItem(tabFolder, SWT.NULL);
		imgPreview.setText("Preview");
		Text prevtext = new Text(tabFolder, SWT.BORDER | SWT.MULTI);
		prevtext.setText("This is the info tab ");
		imgPreview.setControl(prevtext);

		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
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
package com.is_gr8.imageprocessor.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.is_gr8.imageprocessor.Main;
import com.is_gr8.imageprocessor.PgmImage;
import com.is_gr8.imageprocessor.PgmProcessor;
import com.is_gr8.imageprocessor.convolution.HoughTransResult;
import com.is_gr8.imageprocessor.convolution.Kernel;
import com.is_gr8.imageprocessor.ui.EdgeOptionsDialog.KernelType;

/**
 * @author rocco The application window.
 */
public class MainWindow{
	/** logger. */
	private Logger logger = Logger.getLogger(MainWindow.class);
	/** shell width. */
	private static final int XSIZE = 600;
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
	/** tab containing the rendered image. */
	private PreviewComposite previewComposite;
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
					currentImage.addListener(previewComposite);
					
					currentImage.imgOpened();
					
					// update menu options
					saveMenuItem.setEnabled(true);
					smoothMenuItem.setEnabled(true);
					edgeMenuItem.setEnabled(true);
					invertMenuItem.setEnabled(true);
					houghMenuItem.setEnabled(true);
					
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
			dialog.setFileName(currentImage.getFile().getName());
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
						PgmProcessor.writeToDisk(currentImage, path);
					} catch (IOException e1) {
						logger.error("could not save file.", e1);
					}

				} else {
					logger.error("Cannot write without having a current image.");
				}
			}
		};
	};
	/** the windows menu bar. */
	private Menu appMenuBar;
	/** file menu. */
	private MenuItem fileMenu;
	/** open option in the menu. */
	private MenuItem openMenuItem;
	/** save option in the menu. */
	private MenuItem saveMenuItem;
	/** functions menu. */
	private MenuItem functionsMenu;
	/** invert function in the menu. */
	private MenuItem invertMenuItem;
	/** hough transform function in the menu. */
	private MenuItem houghMenuItem;
	/** smooth function in the menu. */
	private MenuItem smoothMenuItem;
	/** edge detection function in the menu. */
	private MenuItem edgeMenuItem;

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
		InputStream icon = Main.class.getResourceAsStream("/shell.gif");
		try{
			shell.setImage(new Image(shell.getDisplay(), icon));
		} catch(Exception ex){
			logger.error("Could not open file shell.gif");
		}
		
		initMenuBar();
		initTabFolder();
	}

	/**
	 * Adds a menu to the window for opening and saving files.
	 */
	private void initMenuBar() {
		appMenuBar = shell.getDisplay().getMenuBar();
		if (appMenuBar == null) {
			appMenuBar = new Menu(shell, SWT.BAR);
			shell.setMenuBar(appMenuBar);
		}
		Menu dropdown = new Menu(appMenuBar);
		Menu dropdown2 = new Menu(appMenuBar);

		fileMenu = new MenuItem(appMenuBar, SWT.CASCADE);
		fileMenu.setText("File");
		fileMenu.setMenu(dropdown);

		openMenuItem = new MenuItem(dropdown, SWT.PUSH);
		openMenuItem.setText("Open...");
		openMenuItem.addSelectionListener(openSelectionAdapter);

		saveMenuItem = new MenuItem(dropdown, SWT.PUSH);
		saveMenuItem.setEnabled(false);
		saveMenuItem.setText("Save...");
		saveMenuItem.addSelectionListener(saveSelectionAdapter);

		MenuItem exitMenuItem = new MenuItem(dropdown, SWT.PUSH);
		exitMenuItem.setText("Exit");
		exitMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.getDisplay().dispose();
			};
		});

		functionsMenu = new MenuItem(appMenuBar, SWT.CASCADE);
		functionsMenu.setText("Functions");
		functionsMenu.setMenu(dropdown2);

		invertMenuItem = new MenuItem(dropdown2, SWT.PUSH);
		invertMenuItem.setText("invert");
		invertMenuItem.setEnabled(false);
		invertMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentImage = PgmProcessor.invert(currentImage, false);
			};
		});
		
		houghMenuItem = new MenuItem(dropdown2, SWT.PUSH);
		houghMenuItem.setText("hough transform");
		houghMenuItem.setEnabled(false);
		houghMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				HoughTransResult result = PgmProcessor.houghTransform(currentImage);
				HoughLineDialog h = new HoughLineDialog(shell, SWT.NONE);
				h.setResult(result);
				boolean saveResult = h.open();
				if(saveResult){
					String path = ""; //TODO: open file chooser dialog to get path
					try {
						PgmProcessor.writeToDisk(result.getAccumulatorImage(), path);
						//TODO: save drawed lines instead of accumulator image.
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			};
		});
		
		smoothMenuItem = new MenuItem(dropdown2, SWT.PUSH);
		smoothMenuItem.setText("smoothen");
		smoothMenuItem.setEnabled(false);
		smoothMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Kernel k = new BlurOptionsDialog(shell, SWT.NONE).open();
				if(k != null){
					currentImage = PgmProcessor.blur(currentImage, k);
				}				
			};
		});
		
		edgeMenuItem = new MenuItem(dropdown2, SWT.PUSH);
		edgeMenuItem.setText("edge detection");
		edgeMenuItem.setEnabled(false);
		edgeMenuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				EdgeOptionsDialog d = new EdgeOptionsDialog(shell, SWT.NONE);
				KernelType k = d.open();
				if(k == KernelType.PREWITT){
					int size = d.getSize();
					currentImage = PgmProcessor.prewittEdgeDetection(currentImage, size);
				} else if(k == KernelType.LAPLACE_OF_GAUSSIAN){
					Kernel kernel = d.getLogKernel();
					currentImage = PgmProcessor.logEdgeDetection(currentImage, kernel);
				}
				
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

		// the picture preview
		TabItem imgPreview = new TabItem(tabFolder, SWT.NULL);
		imgPreview.setText("Preview");
		
		previewComposite = new PreviewComposite(tabFolder, SWT.NONE);
		previewComposite.setDisplay(shell.getDisplay());
		imgPreview.setControl(previewComposite);
		
		
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
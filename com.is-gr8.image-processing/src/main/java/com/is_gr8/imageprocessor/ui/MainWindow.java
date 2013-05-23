package com.is_gr8.imageprocessor.ui;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
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
	/** currently selected img. */
	private PgmImage currentImage = null;
	/** selection adapter for the open menu item. */
	private SelectionAdapter openSelectionAdapter = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(shell, SWT.NULL);
			String[] extensions = {"*.pgm"};
			dialog.setFilterExtensions(extensions);
			String path = dialog.open();
			if (path != null) {

				File selectedFile = new File(path);
				if (selectedFile.isFile()) {
					logger.debug("is file:"
							+ selectedFile.getAbsolutePath());
					currentImage = new PgmImage(selectedFile);
					
				} else {
					logger.error("multiple files or folder selected");
					MessageBox errDialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					errDialog.setText("Invalid selection");
					errDialog.setMessage("The selected file could not be read.");
					errDialog.open(); 
				}
			}
		};
	};
	/** save selection adapter for the menu bar. */
	private SelectionAdapter saveSelectionAdapter = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			String[] extensions = {"*.pgm"};
			dialog.setFilterExtensions(extensions);
			dialog.setFileName("image.pgm");
			String path = dialog.open();
			if (path != null) {
				logger.debug("Save image to: " + path);
				File selectedFile = new File(path);
				selectedFile.exists();
				if (selectedFile.exists() && currentImage != null) {
					MessageBox errDialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
					errDialog.setText("Replace");
					errDialog.setMessage("Do you want to overwrite the existing file?");
					try {
						int answer = errDialog.open();
						if(answer == SWT.OK){
							PgmProcessor.writeToDisk(currentImage);
						}												
					} catch (IOException e1) {
						logger.error("could not save file.", e1);
					} catch (SWTException e2) {
						logger.debug("dialog disposed.", e2);
					}
				} else if(currentImage != null) {
					try {
							PgmProcessor.writeToDisk(currentImage);						
					} catch (IOException e1) {
						logger.error("could not save file.", e1);
					}

				} else{
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

		
		//add invert button
//		Button button = new Button(shell, SWT.PUSH);
//		button.setText("Invert");
//		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		button.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				
//			}
//		});
		
		//add tabs for image information
		final TabFolder tabFolder = new TabFolder (shell, SWT.NONE);
		  for (int i=1; i<5; i++) {
		 TabItem item = new TabItem (tabFolder, SWT.NULL);
		 item.setText ("Tab" + i);
		 Text text = new Text(tabFolder, SWT.BORDER | SWT.MULTI);
		 text.setText("This is Tab "+i);
		 item.setControl(text);
		 }
		 tabFolder.setSize (250, 150);
		 tabFolder.addSelectionListener(new SelectionListener() {
		 public void widgetSelected(SelectionEvent e) {
		 System.out.println("You have selected:"+ tabFolder.getSelection()
		  [0].toString());
		 }
		 public void widgetDefaultSelected(SelectionEvent e) {
			  widgetSelected(e);
			  }
			  });
		
		//add menu bar
		Menu appMenuBar = shell.getDisplay().getMenuBar();
		if (appMenuBar == null) {
			appMenuBar = new Menu(shell, SWT.BAR);
			shell.setMenuBar(appMenuBar);
		}
		Menu dropdown = new Menu(appMenuBar);
		Menu dropdown2 = new Menu(appMenuBar);
		
		//FILE TREE
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
		
		//FUNCTIONS TREE
		MenuItem functions = new MenuItem(appMenuBar, SWT.CASCADE);
		functions.setText("Functions");
		functions.setMenu(dropdown2);
		
		MenuItem invert = new MenuItem(dropdown2, SWT.PUSH);
		invert.setText("invert");
		invert.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				currentImage = PgmProcessor.invert(currentImage);
			};
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
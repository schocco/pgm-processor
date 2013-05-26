/**
 * 
 */
package com.is_gr8.imageprocessor;

/**
 * @author rocco
 *
 */
public interface ImageEventListener {
	/** called whenever a new image is opened. */
	public void imageOpened(ImageEvent e);
	/** called when the image is saved to disc. */
	public void imageSaved(ImageEvent e);
	/** called when the image header info is changed. */
	public void imageHeaderChanged(ImageEvent e);
	/** called when the image body is changed. */
	public void imageBodyChanged(ImageEvent e);

}

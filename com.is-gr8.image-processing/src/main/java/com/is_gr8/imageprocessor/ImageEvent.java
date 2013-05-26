/**
 * 
 */
package com.is_gr8.imageprocessor;

/**
 * Event object.
 * 
 * @author rocco
 *
 */
public class ImageEvent {
	/** the image obj related to the event. */
	private PgmImage image;
	/** an event description. */
	private String description;
	
	/**
	 * Constructor
	 * @param img the pgm image that has been modified/opened/saved/...
	 */
	public ImageEvent(final PgmImage img) {
		// TODO Auto-generated constructor stub
		image = img;
	}
	
	/**
	 * @return the image
	 */
	public final PgmImage getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public final void setImage(PgmImage image) {
		this.image = image;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public final void setDescription(String description) {
		this.description = description;
	}

}

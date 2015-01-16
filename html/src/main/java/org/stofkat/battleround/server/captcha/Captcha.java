package org.stofkat.battleround.server.captcha;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * A 'Count the sheeps' captcha.
 * @author Leejjon
 */
public class Captcha {
	protected static final Logger logger = Logger.getLogger(Captcha.class);
	
	// Image paths.
	public static final String sheepImage = "sheep.png";
	public static final String backgroundImage = "greenbackground.png";
	public static final String mimeType = "image/png";
	private static final String fileType = "png";
	private BufferedImage bufferedCaptchaImage;
	private int numberOfSheeps;
	private final int tileSize = 66;
	
	/**
	 * To make sure the sheeps won't be painted on top of each other, The image has been split up
	 * in 9 tiles as below.
	 * 
	 * 1 |2 |3
	 * --+--+--
	 * 4 |5 |6
	 * --+--+--
	 * 7 |8 |9
	 * 
	 * The following array contains coordinates for each tile. 
	 */
	private Point[] basicPositions = new Point[] {
			new Point(0,0), 
			new Point(0,66),
			new Point(0,133),
			new Point(66,0),
			new Point(66,66),
			new Point(66,133),
			new Point(133,0),
			new Point(133,66),
			new Point(133,133)
	};
	
	/**
	 * Constructor that creates the Captcha.
	 */
	public Captcha(boolean isAllowed) {
		BufferedImage bufferedSheepImage = null;
		Graphics2D captchaGraphics = null;
		try {
			if (isAllowed) {
				// Get the images.
				URL sheepImagePath = Captcha.class.getResource(sheepImage);
				URL backgroundImagePath = Captcha.class.getResource(backgroundImage);
				
				// Set the background image as the base.
				bufferedCaptchaImage = ImageIO.read(backgroundImagePath);
				
				// Get a Graphics2D of the background image so we can draw on it.
				captchaGraphics = bufferedCaptchaImage.createGraphics();
			    bufferedSheepImage = ImageIO.read(sheepImagePath);
			    
			    // Get a random number from 0-8. Then add one to make sure it's 1-9.
			    numberOfSheeps = (int) Math.floor(Math.random() * 9) + 1;
			    
			    ArrayList<Point> positionsLeft = new ArrayList<Point>();
			    Collections.addAll(positionsLeft, basicPositions);
			    
			    int positionsLeftCount = positionsLeft.size();
			    
			    // The sheep are randomly placed in their own tile. 
			    for(int i = 0; i < numberOfSheeps; i++) {
			    	int flippedHorizontally = (int) Math.floor((Math.random() * 2));
		    		int tileNumber = (int) Math.floor(Math.random() * positionsLeftCount);
		    		int randomX = (int) Math.floor(Math.random() * (tileSize - bufferedSheepImage.getWidth()));
		    		int randomY = (int) Math.floor(Math.random() * (tileSize - bufferedSheepImage.getHeight()));
		    		Point positionToDraw = positionsLeft.get(tileNumber);
		    		positionsLeft.remove(tileNumber);
		    		
		    		if(flippedHorizontally == 0) { // Draw image normal.
		    			captchaGraphics.drawImage(bufferedSheepImage,
		    					// Position of the sheep in the image.
		    					positionToDraw.x + randomX, positionToDraw.y + randomY, 
		    					positionToDraw.x + bufferedSheepImage.getWidth() + randomX, positionToDraw.y + bufferedSheepImage.getHeight() + randomY,
		    					// The part of the image that is going to be shown.
		    					0, 0, 
		    					bufferedSheepImage.getWidth(), bufferedSheepImage.getHeight(), 
		    					null);
		    		} else { // Draw image flipped horizontally.
		    			captchaGraphics.drawImage(bufferedSheepImage, 
		    					// Position of the sheep in the image.
		    					positionToDraw.x + randomX, positionToDraw.y + randomY, 
		    					positionToDraw.x + bufferedSheepImage.getWidth() + randomX, positionToDraw.y + bufferedSheepImage.getHeight() + randomY,
		    					// The image is flipped horizontally, setting the end position at the start position and the other way around.
		    					bufferedSheepImage.getWidth(), 0, 
		    					0, bufferedSheepImage.getHeight(), 
		    					null);
		    		} 
		    		
		    		// One less possible option.
		    		positionsLeftCount--;
			    }
			} else {
				bufferedCaptchaImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
				captchaGraphics = bufferedCaptchaImage.createGraphics();
				captchaGraphics.setColor(Color.BLACK);
				String errorMessage = "You have failed three times, you can try again in 10 minutes.";
				int separatorPoint = errorMessage.indexOf(',') + 1;
				captchaGraphics.drawString(errorMessage.substring(0,separatorPoint), 5, 20);
				captchaGraphics.drawString(errorMessage.substring(separatorPoint,errorMessage.length()-1), 5, 40);
			}
		} catch (IOException e) {
			logger.error(e);
			
			// If for some reason we can't find the images
			bufferedCaptchaImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
			captchaGraphics = bufferedCaptchaImage.createGraphics();
			captchaGraphics.setColor(Color.BLACK);
			captchaGraphics.drawString("No sheep found!", 5, 20);
		} finally {
			if(captchaGraphics != null) {
				// Dispose the graphics object. (If you don't this object will stay in the memory)
				captchaGraphics.dispose();
			}
		}
	}
	
	/**
	 * @return The captcha image in png format.
	 * @throws IOException
	 */
	public void getCaptchaImageFile(OutputStream outputStream) throws IOException {
		// Put the buffered image into the file. 
		ImageIO.write(bufferedCaptchaImage, fileType, outputStream);
	}
	
	/**
	 * @return The number of sheeps in the image.
	 */
	public int getNumberOfSheeps() {
		return numberOfSheeps;
	}
}

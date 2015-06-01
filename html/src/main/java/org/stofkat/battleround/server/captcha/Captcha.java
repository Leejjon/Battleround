package org.stofkat.battleround.server.captcha;

//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Composite.Anchor;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

/**
 * A 'Count the sheeps' captcha.
 * @author Leejjon
 */
public class Captcha {
	protected static final Logger logger = Logger.getLogger(Captcha.class);
	
	// Image paths.
	public static final String sheepImage = "WEB-INF/sheep.png";
	public static final String backgroundImage = "WEB-INF/greenbackground.png";
	public static final String mimeType = "image/png";
	private Image captchaImage = null;
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
		// Get the images.
		File sheepImageFile = new File(sheepImage);
		File backgroundImageFile = new File(backgroundImage);
		try (FileInputStream sheepInputStream = new FileInputStream(sheepImageFile); 
				FileInputStream backgroundInputStream = new FileInputStream(backgroundImageFile)) {
			if (isAllowed) {
				ImagesService imagesService = ImagesServiceFactory.getImagesService();
				
				// Probably should replace this with some caching.
				byte sheepImageBytes[] = new byte[(int)sheepImageFile.length()];
				byte backgroundImageBytes[] = new byte[(int)backgroundImageFile.length()];
				
				sheepInputStream.read(sheepImageBytes);
				backgroundInputStream.read(backgroundImageBytes);
				
				// Put the bytes of the images in the Google App Engine images
				// class. I used to use java.awt classes for this but they are
				// not allowed on Google App Engine.
				Image sheepImage = ImagesServiceFactory.makeImage(sheepImageBytes);
				
				Transform invertTransformation = ImagesServiceFactory.makeHorizontalFlip();
				Image invertedSheepImage = imagesService.applyTransform(invertTransformation, ImagesServiceFactory.makeImage(sheepImageBytes));
				Image backgroundImage = ImagesServiceFactory.makeImage(backgroundImageBytes);
			    
			    // Get a random number from 0-8. Then add one to make sure it's 1-9.
			    numberOfSheeps = (int) Math.floor(Math.random() * 9) + 1;
			    
			    ArrayList<Point> positionsLeft = new ArrayList<Point>();
			    Collections.addAll(positionsLeft, basicPositions);
			    
			    int positionsLeftCount = positionsLeft.size();
			    
			    // 1f means fully visible.
			    float opacity = 1f;
			    
			    List<Composite> composites = new ArrayList<Composite>();
			    
			    // First add the background.
			    composites.add(ImagesServiceFactory.makeComposite(backgroundImage, 0, 0, opacity, Anchor.TOP_LEFT));
			    
			    // The sheep are randomly placed in their own tile. 
			    for(int i = 0; i < numberOfSheeps; i++) {
			    	int flippedHorizontally = (int) Math.floor((Math.random() * 2));
		    		int tileNumber = (int) Math.floor(Math.random() * positionsLeftCount);
		    		int randomX = (int) Math.floor(Math.random() * (tileSize - sheepImage.getWidth()));
		    		int randomY = (int) Math.floor(Math.random() * (tileSize - sheepImage.getHeight()));
		    		Point positionToDraw = positionsLeft.get(tileNumber);
		    		positionsLeft.remove(tileNumber);
		    		
		    		
		    		if(flippedHorizontally == 0) { // Draw image normal.
			    		composites.add(ImagesServiceFactory.makeComposite(sheepImage, positionToDraw.getX() + randomX, positionToDraw.getY() + randomY, opacity, Anchor.TOP_LEFT));
		    		} else { // Draw image flipped horizontally.
		    			composites.add(ImagesServiceFactory.makeComposite(invertedSheepImage, positionToDraw.getX() + randomX, positionToDraw.getY() + randomY, opacity, Anchor.TOP_LEFT));
		    		} 
		    		
		    		// One less possible option.
		    		positionsLeftCount--;
			    }
			    
			    // 0 means transparant???
			    captchaImage = imagesService.composite(composites, backgroundImage.getWidth(), backgroundImage.getHeight(), 0, OutputEncoding.PNG);
			} else {
//				bufferedCaptchaImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
//				captchaGraphics = bufferedCaptchaImage.createGraphics();
//				captchaGraphics.setColor(Color.BLACK);
//				String errorMessage = "You have failed three times, you can try again in 10 minutes.";
//				int separatorPoint = errorMessage.indexOf(',') + 1;
//				captchaGraphics.drawString(errorMessage.substring(0,separatorPoint), 5, 20);
//				captchaGraphics.drawString(errorMessage.substring(separatorPoint,errorMessage.length()-1), 5, 40);
			}
		} catch (IOException e) {
			logger.error(e);
			
//			// If for some reason we can't find the images
//			bufferedCaptchaImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
//			captchaGraphics = bufferedCaptchaImage.createGraphics();
//			captchaGraphics.setColor(Color.BLACK);
//			captchaGraphics.drawString("No sheep found!", 5, 20);
		} finally {
//			if(captchaGraphics != null) {
//				// Dispose the graphics object. (If you don't this object will stay in the memory)
//				captchaGraphics.dispose();
//			}
		}
	}
	
	/**
	 * @return The captcha image in png format.
	 * @throws IOException
	 */
	public void getCaptchaImageFile(OutputStream outputStream) throws IOException {
		// Put the buffered image into the file. 
//		ImageIO.write(bufferedCaptchaImage, fileType, outputStream);
		if (captchaImage != null) {
			outputStream.write(captchaImage.getImageData());
		}
	}
	
	/**
	 * @return The number of sheeps in the image.
	 */
	public int getNumberOfSheeps() {
		return numberOfSheeps;
	}
}

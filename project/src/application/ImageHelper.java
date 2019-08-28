package application;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * 
 * @author Mantas Rajackas
 *
 * This class contains various helper methods for editing / analysing JavaFX Images.
 */
public class ImageHelper {
	
	/**
	 * Gets the average brightness of an Image
	 * @param image - An Image object to get the average brightness of.
	 * @return A Color object, where the R,G,B values are all the average brightness.
	 */
	public static Color getAverageBrightness(Image image) {
		PixelReader pr = image.getPixelReader();
		
		double totalBrightness = 0.0;
		double imgHeight = image.getHeight();
		double imgWidth = image.getWidth();
		
		// Adds each pixel's average brightness to totalBrightness
		for (int y = 0; y < imgHeight; y++) {
			for (int x = 0; x < imgWidth; x++) {
				Color pixelColor = pr.getColor(x, y);
				double pixelRed = pixelColor.getRed();
				double pixelGreen = pixelColor.getGreen();
				double pixelBlue = pixelColor.getBlue();
				double pixelAverage = (pixelRed + pixelGreen + pixelBlue) / 3;
				
				totalBrightness += pixelAverage;
			}
		}
		
		// Gets the average brightness of the whole image
		double avgBrightness = totalBrightness / (imgHeight * imgWidth);
		
		return new Color(avgBrightness,avgBrightness,avgBrightness,1.0);
	}
	
	/**
	 * Converts an Image to greyscale based on 
	 * the average RGB values of every pixel
	 * 
	 * @param image - The Image to convert
	 * @return A greyscale version of image
	 */
	public static Image toGreyscale(Image image) {
		WritableImage processedImage = new WritableImage(
				(int) image.getWidth(), (int) image.getHeight());
		
		PixelReader pr = image.getPixelReader();
		PixelWriter pw = processedImage.getPixelWriter();
		
		// Iterates through every pixel in the image
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				
				// Gets the average red, green, and blue values of the pixel
				Color pixelColor = pr.getColor(x, y);
				double pixelRed = pixelColor.getRed();
				double pixelGreen = pixelColor.getGreen();
				double pixelBlue = pixelColor.getBlue();
				double pixelGrey = (pixelRed + pixelGreen + pixelBlue) / 3;
				
				// Sets the pixel's red, green, and blue values to the same average value.
				Color greyColor = new Color(pixelGrey,
											pixelGrey,
											pixelGrey,
											pixelColor.getOpacity());
				
				pw.setColor(x, y, greyColor);
			}
		}
		return processedImage;
	}
	
	/**
	 * Creates an Image from a piece of a larger Image object.
	 * 
	 * @param image - The image to create the sub image from
	 * @param x - The x coordinate for the origin of the sub image
	 * @param y - The y coordinate for the origin of the sub image
	 * @param width - The width of the image, in pixels.
	 * @param height - The height of the image, in pixels.
	 * @return An Image object identical to part of the image input.
	 */
	public static Image getSubImage(Image image, int x, int y, int width, int height) throws IllegalArgumentException {
		if (width < 1 || height < 1) {
			throw new IllegalArgumentException("Width and height must be greater than 0.");
		}
		if (x > image.getWidth() || 
			y > image.getHeight() || 
			x < 0  ||
			y < 0) {
			throw new IllegalArgumentException("x: " + x + ", y: " + y + " is outside of the image.");
		}
		if (x + width > image.getWidth() || y + height > image.getHeight()) {
			throw new IllegalArgumentException("The size of the sub image exceeds the bounds of the original image.");
		}
		
		PixelReader pr = image.getPixelReader();
		return new WritableImage(pr, x, y, width, height);
	}
	
}

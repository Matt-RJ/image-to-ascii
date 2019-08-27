package application;

import java.io.File;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * 
 * @author Mantas Rajackas
 * 
 * Converts images to ASCII art.
 *
 */
public class AsciiConverter {
	
	// Based on http://paulbourke.net/dataformats/asciiart/
	// Each character represents a different level of brightness per part of an ascii art image,
	// with the leftmost chars representing darker areas, increasing in brightness going to the right.
	private static final String asciiRamp = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
	// A smaller version of asciiRamp with fewer brightness levels.
	private static final String asciiRampMini = "@%#*+=-:. ";
	
	enum Ramp {
		FULL, // The converter will use the full 70 character asciiRamp for conversion
		MINI, // The converter will use the small 10 character asciiRampMini for conversion
	}
	
	// The number of columns the ASCII art will have, the rows are calculated with gridColumns
	private int tileColumns = 80; // TODO: Add GUI element to change this.
	
	private File loadedImageFile = null; // The file of the image to convert to ascii
	private Image loadedImage = null; // An Image version of loadedImageFile
	private Image greyscaleImage = null; // A greyscale version of loadedImage
	private Ramp rampLevel = Ramp.FULL; // Which character ramp will be used to convert
	
	// Getters / Setters
	public File getLoadedImageFile() {
		return loadedImageFile;
	}
	public void setLoadedImageFile(File loadedImageFile) {
		this.loadedImageFile = loadedImageFile;
	}
	public Image getLoadedImage() {
		return loadedImage;
	}
	public void setLoadedImage(Image loadedImage) {
		this.loadedImage = loadedImage;
	}
	public Image getGreyscaleImage() {
		return greyscaleImage;
	}
	public void setGreyscaleImage(Image greyscaleImage) {
		this.greyscaleImage = greyscaleImage;
	}
	public Ramp getRampLevel() {
		return rampLevel;
	}
	public void setRampLevel(Ramp rampLevel) {
		this.rampLevel = rampLevel;
	}
	
	public AsciiConverter() {
		
	}
	public AsciiConverter(Ramp ramp) {
		this.rampLevel = ramp;
	}
	
	/**
	 * Converts an Image file into a String of ASCII art.
	 * @param image - The image to convert
	 * @return A string of ASCII art based on the Image.
	 */
	public String toAsciiArt(Image image) {
		
		String asciiArt = "";
		
		// Which set of characters the converted image will have
		String ramp = null;
		if (this.rampLevel == Ramp.FULL) ramp = asciiRamp;
		else if (this.rampLevel == Ramp.MINI) ramp = asciiRampMini;
		
		double imageWidth = image.getWidth();
		double imageHeight = image.getHeight();
		
		// Gets tile width
		double tileWidth = imageWidth / tileColumns; // TODO: Take inputs from the user to change the size of the grid.
		double tileHeight = imageWidth / 12; // TODO: Use the font scale here instead of 12
		
		// The final ASCII image will be tileRows * tileColumns characters in size
		int tileRows = (int) (imageHeight / tileHeight);
		
		// TODO: Use getSubimage to analyze each tile
		double[][] tileSet = new double[tileRows][tileColumns];
		
		// TODO: Get the average brightness value for each tile in the grid
		
		// TODO: Map the brightnesses of each tile to a value in asciiRamp or asciiRampMini
		
		// TODO: Create and return a String that contains the converted image.
		
		return asciiArt;
	}
	
	/**
	 * Gets the average brightness of an Image
	 * @param image - An Image object to get the average brightness of.
	 * @return A Color object, where the R,G,B values are all the average brightness.
	 */
	public Color getAverageBrightness(Image image) {
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
	public Image toGreyscale(Image image) {
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
	
}

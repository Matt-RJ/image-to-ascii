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
	private static final String asciiRamp = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
	// A smaller version of asciiRamp with fewer brightness levels.
	private static final String asciiRampMini = "@%#*+=-:. ";
	// A custom ramp defined by the user in the GUI
	private static String asciiRampCustom = ""; // TODO: Add GUI element
	
	enum Ramp {
		FULL, // The converter will use the full 70 character asciiRamp for conversion
		MINI, // The converter will use the small 10 character asciiRampMini for conversion
		CUSTOM, // The converter will use custom a ramp defined by the user
	}
	
	// The number of columns the ASCII art will have, the rows are calculated with gridColumns
	private int tileColumns = 120; // TODO: Add GUI element to change this.
	private double scale = 0.5; // TODO: Add GUI element to change this.
	
	private File loadedImageFile = null; // The file of the image to convert to ascii
	private Image loadedImage = null; // An Image version of loadedImageFile
	private Image greyscaleImage = null; // A greyscale version of loadedImage
	private Ramp rampLevel = Ramp.FULL; // Which character ramp will be used to convert
	
	// Getters / Setters
	public String getAsciiRampCustom() {
		return asciiRampCustom;
	}
	public void setAsciiRampCustom(String asciiRampCustom) {
		AsciiConverter.asciiRampCustom = asciiRampCustom;
	}
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
	

	public int getTileColumns() {
		return tileColumns;
	}
	public void setTileColumns(int tileColumns) {
		this.tileColumns = tileColumns;
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
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
		
		int imageWidth = (int) image.getWidth();
		int imageHeight = (int) image.getHeight();
		
		double tileWidth = imageWidth / tileColumns; // TODO: Add GUI element to change this value
		double tileHeight = tileWidth / scale; // TODO: Add GUI element to change this value
		
		if (tileColumns > imageWidth) {
			throw new IllegalArgumentException("There are more tile columns than pixel columns in the image.");
		}
		if (tileHeight > imageHeight) {
			throw new IllegalArgumentException("The tile height is bigger than the height of the image.");
		}
		
		String asciiArt = "";
		
		// Which set of characters the converted image will have
		String ramp;
		if (this.rampLevel == Ramp.FULL) ramp = asciiRamp;
		else if (this.rampLevel == Ramp.CUSTOM) ramp = asciiRampCustom; 
		else ramp = asciiRampMini;

		// Iterates through each tile in the image
		for (int y = 0; y < imageHeight; y += tileHeight) {
			for (int x = 0; x < imageWidth; x += tileWidth) {

				Image tileImage;
				
				// The tile height can change for border tiles
				// if the tile width doesn't divide evenly into the width of the image
				double currentTileWidth = tileWidth;
				double currentTileHeight = tileHeight;
				
				// Makes the rightmost and/or bottommost tiles smaller if necessary
				if (x+currentTileWidth > imageWidth) currentTileWidth = imageWidth - x;
				if (y+currentTileHeight > imageHeight) currentTileHeight = imageHeight - y;
				
				// Gets the average brightness for the current tile
				tileImage = getSubImage(image, x, y, (int) (currentTileWidth), (int) (currentTileHeight));
				int avgBrightness = (int) (getAverageBrightness(tileImage).getRed() * 255);
				
				// Selects the char to use for the tile
				int charIndex = remap(avgBrightness, 0, 255, 0, ramp.length()-1);
				char tileChar = ramp.charAt(charIndex);
				asciiArt += tileChar;

				
			}
			asciiArt += "\n";
		}
		
		return asciiArt;
	}
	
	public Image getSubImage(Image image, int x, int y, int width, int height) {
		
		PixelReader pr = image.getPixelReader();
		WritableImage subImage = new WritableImage(pr, x, y, width, height);
		
		return subImage;
	}
	
	/**
	 * Remaps a value in a given integer range to its equivalent in another integer range.
	 * @param value - The value to convert
	 * @param min - The minimum value in the current range
	 * @param max - The maximum value in the current range
	 * @param newMin - The minimum value in the new range
	 * @param newMax - The maximum value in the new range
	 * @return
	 */
	public int remap(int value, int min, int max, int newMin, int newMax) {
		int range = max-min;
		int newRange = newMax - newMin;
		int newValue = (((value - min) * newRange) / range) + newMin;
		return newValue;
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

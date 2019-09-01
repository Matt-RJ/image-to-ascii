package application;

import java.io.File;

import javafx.scene.image.Image;

/**
 * 
 * Converts JavaFX Image objects to ASCII art.
 * Based on http://paulbourke.net/dataformats/asciiart/
 * 
 * @author Mantas Rajackas
 * 
 * @see <a href="http://paulbourke.net/dataformats/asciiart/">http://paulbourke.net/dataformats/asciiart/</a>
 *
 */
public class AsciiConverter {
	
	// Each character represents a different level of brightness per part of an ascii art image,
	// with the leftmost chars representing darker areas, increasing in brightness going to the right.
	private static final String asciiRamp = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
	
	// A smaller version of asciiRamp with fewer brightness levels.
	private static final String asciiRampMini = "@%#*+=-:. ";
	
	// A custom ramp defined by the user in the GUI
	private static String asciiRampCustom = "";
	
	private boolean rampInverted = false;
	
	enum Ramp {
		FULL, // The converter will use the full 70 character asciiRamp for conversion
		MINI, // The converter will use the small 10 character asciiRampMini for conversion
		CUSTOM, // The converter will use custom a ramp defined by the user
	}
	
	// The number of columns the ASCII art will have, the rows are calculated with gridColumns
	private int tileColumns = 80;
	
	// These values are used to compensate for different font sizes so the ASCII art doesn't appear stretched.
	private final double DEFAULT_SCALE = 0.46;
	private double scale = 0.46;
	
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
	public boolean getRampInverted() {
		return rampInverted;
	}
	public void setRampInverted(boolean rampInverted) {
		this.rampInverted = rampInverted;
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
	public double getDefaultScale() {
		return DEFAULT_SCALE;
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
	 * Resets the instance's scale value to default
	 */
	public void resetScale() {
		this.scale = DEFAULT_SCALE;
	}
	
	/**
	 * Converts a JavaFX Image into a String of ASCII art.
	 * @param image - The image to convert
	 * @return A string of ASCII art based on the Image.
	 */
	public String toAsciiArt(Image image) {
		
		int imageWidth = (int) image.getWidth();
		int imageHeight = (int) image.getHeight();
		
		double tileWidth = imageWidth / tileColumns;
		double tileHeight = tileWidth / scale;
		
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
		
		if (rampInverted) {
			StringBuilder reverseRamp = new StringBuilder();
			reverseRamp.append(ramp);
			reverseRamp = reverseRamp.reverse();
			ramp = reverseRamp.toString();
		}

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
				tileImage = ImageHelper.getSubImage(image, x, y, (int) (currentTileWidth), (int) (currentTileHeight));
				int avgBrightness = (int) (ImageHelper.getAverageBrightness(tileImage).getRed() * 255);
				
				// Selects the char to use for the tile
				int charIndex = remap(avgBrightness, 0, 255, 0, ramp.length()-1);
				char tileChar = ramp.charAt(charIndex);
				asciiArt += tileChar;

				
			}
			asciiArt += "\n";
		}
		
		return asciiArt;
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
	
}

package application;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author Mantas Rajackas
 * 
 * This class bridges the MainMenu.fxml GUI and the rest of the program.
 *
 */
public class MainMenuController {
	
	@FXML
	private MenuItem quitMenuItem;
	
	@FXML
	private TabPane tabs;
	@FXML
	private Tab aboutTab;
	@FXML
	private Tab originalImageTab;
	@FXML
	private Tab convertedImageTab;
	
	@FXML
	private Button openImageButton;
	@FXML
	private TextField imagePathTextField;
	@FXML
	private ImageView imagePanel;
	
	
	@SuppressWarnings("unused")
	private ToggleGroup rampSelection;
	@FXML
	private RadioButton fullRamp;
	@FXML
	private RadioButton miniRamp;
	@FXML
	private RadioButton customRamp;
	@FXML
	private TextField customRampText;
	
	@FXML
	private Button convertImageButton;
	
	@FXML
	private TextArea convertedImageTextArea;
	
	/**
	 * Opens a file browser for the user to select an image to convert.
	 * @param event
	 */
	@FXML
	public void openImage(ActionEvent event) {
		try {
			// Opens file browser for selecting image
			FileChooser fc = new FileChooser();
			fc.setTitle("Open Image");
			File imageFile = fc.showOpenDialog(new Stage());
			
			// Stores the image file and image itself in asciiConverter
			Main.asciiConverter.setLoadedImageFile(imageFile);
			Image loadedImage = new Image(imageFile.toURI().toString());
			Main.asciiConverter.setLoadedImage(loadedImage);
			
			// Displays the image that was just loaded in
			displayImage(loadedImage);
			
			// Updates the text field to show the image path
			String imagePath = imageFile.getPath();
			updateImagePath(imagePath);
			
			// Switches to the original image tab
			switchToOriginalImageTab();
		}
		catch (NullPointerException e) {
			System.out.println("Image loading cancelled.");
		}
	}
	
	/**
	 * Converts the loaded image into ASCII art.
	 * @param event
	 */
	@FXML
	public void convertImage(ActionEvent event) {
		// Does nothing if no image is loaded
		if (Main.asciiConverter.getLoadedImageFile() == null) return;
		
		// Updates the custom ramp
		Main.asciiConverter.setAsciiRampCustom(customRampText.getText());
		System.out.println(customRampText.getText());
		System.out.println(Main.asciiConverter.getRampLevel());
		
		// Generates ASCII art
		String asciiArt = Main.asciiConverter.toAsciiArt(Main.asciiConverter.getLoadedImage());
		System.out.println(asciiArt);
		
		// Switches to the ASCII art tab and displays the generated text
		displayAsciiArt(asciiArt);
		switchToConvertedImageTab();
	}
	
	
	/**
	 * Displays a JavaFX Image in the imagePanel.
	 * @param image
	 */
	public void displayImage(Image image) {
		imagePanel.setImage(image);
	}
	/**
	 * Updates the text of imagePathTextField
	 * @param path
	 */
	public void updateImagePath(String path) {
		imagePathTextField.setText(path);
	}
	
	public void displayAsciiArt(String asciiArt) {
		convertedImageTextArea.setText(asciiArt);
	}
	
	// Radio Buttons
	
	/**
	 * Sets the ramp for the converter to full
	 * @param event
	 */
	@FXML
	public void setRampToFull(ActionEvent event) {
		Main.asciiConverter.setRampLevel(AsciiConverter.Ramp.FULL);
	}
	/**
	 * Sets the ramp for the converter to mini
	 * @param event
	 */
	@FXML
	public void setRampToMini(ActionEvent event) {
		Main.asciiConverter.setRampLevel(AsciiConverter.Ramp.MINI);
	}
	/**
	 * Sets the ramp for the converter to custom
	 * @param event
	 */
	@FXML
	public void setRampToCustom(ActionEvent event) {
		Main.asciiConverter.setRampLevel(AsciiConverter.Ramp.CUSTOM);
	}
	
	// Tab Switching
	
	/**
	 * Switches focus to the About tab
	 */
	public void switchToAboutTab() {
		tabs.getSelectionModel().select(aboutTab);
	}
	/**
	 * Switches focus to the Original Image tab
	 */
	public void switchToOriginalImageTab() {
		tabs.getSelectionModel().select(originalImageTab);
	}
	/**
	 * Switches focus to the Converted Image tab
	 */
	public void switchToConvertedImageTab() {
		tabs.getSelectionModel().select(convertedImageTab);
	}
	
	/**
	 * Quits the program.
	 */
	public void quit() {
		System.exit(0);
	}

}

package application;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
	
	
	// Tabs and their content
	
	@FXML
	private TabPane tabs;
	@FXML
	private Tab aboutTab;
	@FXML
	private Tab originalImageTab;
	@FXML
	private ImageView imagePanel;
	@FXML
	private Tab convertedImageTab;
	@FXML
	private TextArea convertedImageTextArea;
	
	
	// Image loading
	
	@FXML
	private Button openImageButton;
	@FXML
	private TextField imageNameTextField;
	
	
	
	// ASCII Ramp settings
	
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
	private CheckBox invertCheckBox;
	
	
	// ASCII Art Display Settings
	
	@FXML
	private Spinner<Integer> tileColumnSpinner;
	@FXML
	private Label columnsSetLabel;
	
	@FXML
	private Button copyToClipboardButton;
	@FXML
	private Label copiedLabel;
	
	@FXML
	private Button convertImageButton;
	
	
	public void initialize() {
		
		// Sets up the spinner for choosing how many character columns the ASCII art will have
		SpinnerValueFactory<Integer> tileColumnValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 160);
		this.tileColumnSpinner.setValueFactory(tileColumnValueFactory);
		tileColumnSpinner.getValueFactory().setValue(80);
		
		// This handler resets the spinner to its minimum value if an invalid input is typed in.
		EventHandler<KeyEvent> spinnerValueEnteredHandler;
		spinnerValueEnteredHandler = new EventHandler<KeyEvent>() {
			
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					try {
						Integer.parseInt(tileColumnSpinner.getEditor().textProperty().get());
					}
					catch (NumberFormatException e) {
						// If a string or float is input into the spinner, the input is changed to -1.
						// -1 gets filtered by the spinner's factory into the minimum it allows.
						System.err.println("Invalid input - Only integers are allowed.");
						tileColumnSpinner.getEditor().textProperty().set(Integer.toString(-1));
					}
					
				};
			}
			
		};
		tileColumnSpinner.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, spinnerValueEnteredHandler);
		
		// Updates the number of columns in ASciiConverter when the spinner value changes
		tileColumnSpinner.valueProperty().addListener((v, oldValue, newValue) -> {
			Main.asciiConverter.setTileColumns(newValue);
			updateColumnsSetLabel(newValue);
		});
		
	}
	
	/**
	 * Updates the value factory for the column spinner to not allow more tile columns than pixels in the loaded image.
	 * @param min - The new min
	 * @param max - The new max
	 * @param setTo - The value to set the spinner to
	 */
	public void updateSpinnerValueFactory(int min, int max, int setTo) {
		int imageWidth = (int) Main.asciiConverter.getLoadedImage().getWidth();
		SpinnerValueFactory<Integer> tileColumnValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(2, imageWidth);
		this.tileColumnSpinner.setValueFactory(tileColumnValueFactory);
		tileColumnSpinner.getValueFactory().setValue((int) max/2);
	}
	
	/**
	 * Copies the text in the ASCII art TextArea into the system clipboard.
	 * @param event
	 */
	public void copyArtToClipboard(ActionEvent event) {
		String asciiArt = convertedImageTextArea.getText();
		
		final ClipboardContent content = new ClipboardContent();
		content.putString(asciiArt);
		Clipboard.getSystemClipboard().setContent(content);
		
		showCopiedLabel();
	}
	
	/**
	 * Shows the label notifying the user that they have copied the ascii art TextArea contents to their clipboard.
	 */
	public void showCopiedLabel() {
		copiedLabel.setVisible(true);
	}
	
	/**
	 * Hides the label notifying the user that they have copied the ASCII art TextArea contents to their clipboard.
	 * @param e
	 */
	public void hideCopiedLabel(MouseEvent e) {
		copiedLabel.setVisible(false);
	}
	
	/**
	 * Updates the label under the column number spinner.
	 * @param columnNumber
	 */
	public void updateColumnsSetLabel(int columnNumber) {
		columnsSetLabel.setText("Columns set: " + columnNumber);
	}
	
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
			
			// Updates the text field to show the image name
			String imageName = imageFile.getName();
			updateImageName(imageName);
			
			// Updates the column spinner to suit the new image
			updateSpinnerValueFactory(2, (int) loadedImage.getWidth(), (int) loadedImage.getWidth()/2);
			
			// Switches to the original image tab
			switchToOriginalImageTab();
			
		}
			
		catch (NullPointerException e) {
			System.err.println("Image loading cancelled.");
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
		
		// Updates whether the ASCII art is inverted or not
		if (invertCheckBox.isSelected()) { 
			Main.asciiConverter.setRampInverted(true);
		}
		else {
			Main.asciiConverter.setRampInverted(false);
		}
		
		// Generates ASCII art
		String asciiArt = Main.asciiConverter.toAsciiArt(Main.asciiConverter.getLoadedImage());
		
		// Switches to the ASCII art tab and displays the generated text
		displayAsciiArt(asciiArt);
		switchToConvertedImageTab();
	}
	
	
	/**
	 * Displays a JavaFX Image in the imagePanel.
	 * @param image - The Image object to display
	 */
	public void displayImage(Image image) {
		imagePanel.setImage(image);
	}
	/**
	 * Updates the text of imageNameTextField
	 * @param name - The new name
	 */
	public void updateImageName(String name) {
		imageNameTextField.setText(name);
	}
	
	/**
	 * Displays ASCII art in the converted image tab.
	 * @param asciiArt - The String of ASCII art to display.
	 */
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

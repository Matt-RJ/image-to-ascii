package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * 
 * This program converts images into ASCII art. This is done by splitting an image into a grid. Each grid tile is 
 * assigned an ASCII character based on the average brightness of all the pixels in it.
 * <br><br>
 * The project took inspiration from chapter 6 of 'Python Playground - Geeky Projects for the Curious Programmer' 
 * by Mahesh Venkitachalam. Its concepts on generating ASCII art from images has been transferred to Java with a
 * GUI approach, rather than a CLI one.
 * 
 * @author Mantas Rajackas
 * 
 * @see <a href="https://www.amazon.com/Python-Playground-Projects-Curious-Programmer-ebook/dp/B017AH8H7I">
 * 			'Python Playground - Geeky Projects for the Curious Programmer' on Amazon
 * 		</a>
 * @see <a href="http://paulbourke.net/dataformats/asciiart/">
 * 			Character representation of grey scale images
 * 		</a>
 */
public class Main extends Application {
	
	public static AsciiConverter asciiConverter = new AsciiConverter();
	
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setMinWidth(960);
			primaryStage.setMinHeight(555);
			Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			Scene scene = new Scene(root,1280,800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

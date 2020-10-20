package nz.ac.massey.cs.texteditor;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.stage.Stage;


public class App extends Application {
	public static String ROOT_DIR = System.getProperty("user.dir");
	public static String HOME_DIR = System.getProperty("user.home");
	
	private static ArrayList<GUI> GUIS = new ArrayList<GUI>();
	

	//List of stages for the exit function to be able to close them all
	//when a new stage is created it needs to get added to STAGES
	public static ArrayList<Stage> STAGES = new ArrayList<>();
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	public static ArrayList<GUI> getGUIs() {
		return GUIS;
	}
	
	
	public static GUI newGUI(Stage stage) {
		GUI gui = new GUI(stage);
		GUIS.add(gui);
		return gui;
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		newGUI(primaryStage);
	}
}

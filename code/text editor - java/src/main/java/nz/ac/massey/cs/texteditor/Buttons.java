package nz.ac.massey.cs.texteditor;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Buttons {
	//Creates a new window
	public static void news() {
		Stage stage = new Stage();
		GUI gui = App.newGUI(stage);
		
		stage.setScene(gui.getScene());
		stage.show();
	}
	
	
	//Opens a file and puts text into the text area
	public static void open(GUI gui) throws IOException {
		FileHandler.openFile(gui);
		
	}
	
	
	//Saves the text in text area into a file
	public static void save(GUI gui) throws IOException {
		FileHandler.saveFile(gui);
	}
	
	
	//Prints the textArea
	public static void print(GUI gui) throws PrinterException {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(gui.getTextArea());
		if (job.printDialog()) {
			try {
				job.print();
			}
			catch (PrinterException e) {
				//this needs to be blank
			}
		}
	}
	
	
	//Selects all text
	public static void select(RSyntaxTextArea textArea) {
		textArea.selectAll();
	}
	
	
	//Copy's selected text
	public static void copy(RSyntaxTextArea textArea) {
		textArea.copy();
	}
	
	
	//Paste's from the clipboard
	public static void paste(RSyntaxTextArea textArea) {
		textArea.paste();
	}
	
	
	//cut's the selected text
	public static void cut(RSyntaxTextArea textArea) {
		textArea.cut();
	}
	
	
	//inserts the time and date into the top line of the text area
	public static void time(RSyntaxTextArea textArea) {
		textArea.insert(new SimpleDateFormat("HH:mm dd/MM/yyyy").format(new Date()) + "\n", 0);
		textArea.setCaretPosition(0);
	}
	
	
	//opens the find window to find words in the text area
	public static void find(RSyntaxTextArea textArea, Window parent) {
		Stage stage = new Stage();
		
		Label enter = new Label("Enter word to find: ");
		TextField text = new TextField();
		Button next = new Button("Find next");
		Button cancel = new Button("Cancel");
		Label cantFind = new Label("Could not find in text");
		cantFind.setVisible(false);
		
		GridPane gridPane = new GridPane();
		gridPane.add(enter, 0, 0);
		gridPane.add(text, 1, 0);
		gridPane.add(next, 0, 1);
		gridPane.add(cancel, 1, 1);
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		GridPane.setHalignment(enter, HPos.CENTER);
		GridPane.setHalignment(text, HPos.CENTER);
		GridPane.setHalignment(next, HPos.CENTER);
		GridPane.setHalignment(cancel, HPos.CENTER);
		
		
		BorderPane borderPane = new BorderPane();
		borderPane.setTop(gridPane);
		borderPane.setBottom(cantFind);
		BorderPane.setAlignment(cantFind, Pos.CENTER);
		
		Scene scene = new Scene(borderPane, 300, 100);
		scene.getStylesheets().add(Buttons.class.getResource("find.css").toExternalForm());
		
		stage.initOwner(parent);
		stage.setScene(scene);
		stage.setTitle("Find");
		stage.getIcons().add(new Image(GUI.class.getResourceAsStream("icon.png")));
		stage.setResizable(false);
		stage.show();
		
		
		AtomicInteger index = new AtomicInteger(0);
		
		text.textProperty().addListener((obs, oldText, newText) -> {
			index.set(0);
		});
		
		
		next.setOnAction(e -> {
			findNext(index, text, textArea, cantFind);
		});
		
		
		cancel.setOnAction(e -> {
			closeStage(stage);
		});
	}
	
	
	//finds the next occurrence of the chosen word
	public static void findNext(AtomicInteger index, TextField text, RSyntaxTextArea textArea, Label cantFind) {
		String word = text.getText();
		int num = textArea.getText().indexOf(word, index.get());
		
		if (num == -1 && index.get() == 0) {
			cantFind.setText("Could not find '" + word + "'.");
			cantFind.setVisible(true);
		}
		else if (num == textArea.getText().lastIndexOf(word)) {
			cantFind.setVisible(false);
			textArea.select(num, num + word.length());
			index.set(0);
		}
		else {
			cantFind.setVisible(false);
			textArea.select(num, num + word.length());
			index.set(num + word.length());
		}
	}
	
	
	//exits all open windows
	public static void exit() {
		for (GUI gui : App.getGUIs()) {
			gui.getStage().close();
		}
		App.getGUIs().clear();
	}
	
	
	//opens the about window
	public static void about() {
		Stage stage = new Stage();
		
		Label created = new Label("Created by:");
		Label name = new Label("Sam Wood & Roman Jensen");
		Label message = new Label("This is the best text editor ever created!");
		Button ok = new Button("OK");
		VBox info = new VBox(created, name, message, ok);
		
		BorderPane sp = new BorderPane();
		sp.setCenter(info);
				
		Scene scene = new Scene(sp, 365, 300);
		scene.getStylesheets().add(Buttons.class.getResource("about.css").toExternalForm());
		
	
		stage.setScene(scene);
		stage.setTitle("About");
		stage.getIcons().add(new Image(GUI.class.getResourceAsStream("icon.png")));
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
		
		ok.setOnAction(f -> {
			closeStage(stage);
		});
	}
	
	
	//closes the given stage
	public static void closeStage(Stage stage) {
		stage.close();
	}
}

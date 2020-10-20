package nz.ac.massey.cs.texteditor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class TestSaveAndOpen extends ApplicationTest {

	@Test
	public void testOpen() throws IOException {
		new JFXPanel();
		//this is needed to get around a illgalStateException from trying to run in the wrong thread
		Platform.runLater(() -> {
			//create text area
			GUI gui = new GUI(new Stage());
			String text = "testing tesing 123";
			gui.getTextArea().setText(text);
			
			
			//creating file
			File file = new File("test.txt");
			
			try {
				//saving
				FileHandler.saveFile(gui, file);
				
				
				//open file
				FileHandler.openFile(gui, file);
			} catch (Exception e) {
				
			}
			
			System.out.println(file.getPath());
			
			
			//test
			assertEquals(text, gui.getTextArea().getText());
			
			
			file.delete();
		});
	}
}
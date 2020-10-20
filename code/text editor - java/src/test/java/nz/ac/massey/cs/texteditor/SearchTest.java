package nz.ac.massey.cs.texteditor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class SearchTest extends ApplicationTest {
	
    @Test
    public void testSearch() {
    	new JFXPanel();
    	RSyntaxTextArea textArea = new RSyntaxTextArea();
    	TextField text = new TextField();
    	AtomicInteger index = new AtomicInteger(0);
    	Label label = new Label("Label");
    	//set text
    	textArea.setText("one two three four");
    	
    	
    	//text to find
    	text.setText("three");
    	
    
    	//run the findNext method to select the word in textArea
    	Buttons.findNext(index, text, textArea, label);
    	
    	
    	assertEquals(text.getText(), textArea.getSelectedText());
		assertEquals(8, textArea.getSelectionStart());
		assertEquals(13, textArea.getSelectionEnd());
    }
}
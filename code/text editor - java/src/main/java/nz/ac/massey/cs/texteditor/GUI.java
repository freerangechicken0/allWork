package nz.ac.massey.cs.texteditor;

import java.awt.print.PrinterException;
import java.io.IOException;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GUI {
	
	private RSyntaxTextArea textArea = new RSyntaxTextArea();
	private Scene scene;
	private Stage stage;
	private Label status = new Label("Line: 1, Column: 1");
	private Label length = new Label("Length: 0, Lines: 1");
	private Label selection = new Label("Selection: 0|0 to 0|0");
	
	
	public GUI(Stage newStage) {
		//MENU GUI
		MenuBar menu = new MenuBar();
		menu.getStylesheets().add(GUI.class.getResource("menu.css").toExternalForm());
				
		MenuItem news = new MenuItem("New");
		MenuItem open = new MenuItem("Open");
		MenuItem save = new MenuItem("Save");
		MenuItem print = new MenuItem("Print");
		Menu file = new Menu("File", null, news, open, save, print);
						
		MenuItem select = new MenuItem("Select Text");
		MenuItem copy = new MenuItem("Copy");
		MenuItem paste = new MenuItem("Paste");
		MenuItem cut = new MenuItem("Cut");
		MenuItem time = new MenuItem("Time/Date");
		Menu edit = new Menu("Edit", null, select, copy, paste, cut, time);	
				
		MenuItem find = new MenuItem("Find");
		Menu search = new Menu("Search", null, find);	
				
		MenuItem exit = new MenuItem("Exit");
		Menu view = new Menu("View", null, exit);		
				
		MenuItem about = new MenuItem("About");
		Menu help = new Menu("Help", null, about);
		
		menu.getMenus().addAll(file, edit, search, view, help);
				
		
		//TEXTAREA
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		
		RTextScrollPane scrollPane = new RTextScrollPane(textArea);	
		
		SwingNode swingNode = new SwingNode();
		swingNode.setContent(scrollPane);
		
		
		//FOOTER
		textArea.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				Platform.runLater(
						() -> {
							updateFooter();
						}
				);
			}
		});
		
		HBox footer = new HBox(length, status, selection);
		footer.setPadding(new Insets(3, 10, 3, 40));
		footer.setSpacing(40);
		
		
		//SETTING SCENE
		BorderPane main = new BorderPane();
		main.setTop(menu);
		main.setCenter(swingNode);
		main.setBottom(footer);
				
		scene = new Scene(main, 1000, 600);
		
		
		//SETTING STAGE
		stage = newStage;
		stage.setScene(scene);
		stage.setTitle("New File");
		stage.getIcons().add(new Image(GUI.class.getResourceAsStream("icon.png")));
		stage.show();

		
		//GIVING BUTTONS FUNTIONALITY
		news.setOnAction(e -> {
			Buttons.news();
		});
		
		
		open.setOnAction(e -> {
			try {
				Buttons.open(this);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		
		save.setOnAction(e -> {
			try {
				Buttons.save(this);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		
		
		print.setOnAction(e -> {
			try {
				Buttons.print(this);
			} catch (PrinterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		select.setOnAction(e -> {
			Buttons.select(textArea);
		});
		
		
		copy.setOnAction(e -> {
			Buttons.copy(textArea);
		});
		
		
		paste.setOnAction(e -> {
			Buttons.paste(textArea);
		});
		
		
		cut.setOnAction(e -> {
			Buttons.cut(textArea);
		});
		
		
		time.setOnAction(e -> {
			Buttons.time(textArea);
		});
		
		
		find.setOnAction(e -> {
			Buttons.find(textArea, scene.getWindow());
		});
				
				
		exit.setOnAction(e -> {
			Buttons.exit();
		});
				
				
		about.setOnAction(e -> {
			Buttons.about();
		});
		
	}

	
	//sets the language syntax for the text area based on the given lang string
	public void setLanguage(String lang) {
		switch (lang) {
		case "c":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
			break;
		case "cpp":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
			break;
		case "cs":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
			break;
		case "css":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
			break;
		case "html":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
			break;
		case "ini":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_INI);
			break;
		case "java":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
			break;
		case "js":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
			break;
		case "json":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
			break;
		case "php":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
			break;
		case "py":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
			break;
		case "ruby":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
			break;
		case "scala":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA);
			break;
		case "sql":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
			break;
		case "xml":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
			break;
		case "yml":
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML);
			break;
		default:
			textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
		}
	}
	
	
	public void updateFooter() {
		length.setText("Length: " + textArea.getText().length() + ", Lines: " + textArea.getLineCount());
		
		
		int line = textArea.getCaretLineNumber();
		int col = 1;
		
		try {
			int caret = textArea.getCaretPosition();
			col = caret-textArea.getLineStartOffset(line) + 1;
			line ++;
		} 
		catch (BadLocationException e2) {}
		
		status.setText("Line: " + line + ", Column: " + col);
		
		
		int selStart = textArea.getSelectionStart();
		int selEnd = textArea.getSelectionEnd();
		
		int startLine = 0;
		int endLine = 0;
		
		int startCol = 0;
		int endCol = 0;
		
		if (selStart == selEnd) {
			selection.setText("Selection: 0|0 to 0|0");
		}
		else {
			try {
				startLine = textArea.getLineOfOffset(selStart);
				endLine = textArea.getLineOfOffset(selEnd);
				
				startCol = selStart - textArea.getLineStartOffset(startLine);
				endCol = selEnd - textArea.getLineStartOffset(endLine);
				
				startLine++;
				endLine++;
				
				startCol++;
				endCol++;
			} catch (BadLocationException e) {}
			
			selection.setText("Selection: " + startLine + "|" + startCol + " to " + endLine + "|" + endCol);
		}
		
	}



	public RSyntaxTextArea getTextArea() {
		return textArea;
	}


	public Scene getScene() {
		return scene;
	}
	
	
	public Stage getStage() {
		return stage;
	}
}

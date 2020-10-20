package nz.ac.massey.cs.texteditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

class FileHandler {
	private static FileChooser FILE_CHOOSER = new FileChooser();
	private static ExtensionFilter FILTER = new ExtensionFilter("Supported Files", "*.txt", "*.c",
			"*.cpp", "*.cs", "*.css", "*.html", "*.ini", "*.java", "*.js", "*.json", "*.php", "*.py",
			"*.ruby", "*.scala", "*.sql", "*.xml", "*.yml", "*.odt");
	private static ExtensionFilter ALL_FILTER = new ExtensionFilter("All Files", "*.*");
	private static ExtensionFilter PDF_FILTER = new ExtensionFilter("Save as PDF", "*.pdf");
	static {
		FILE_CHOOSER.setInitialDirectory(new File(App.HOME_DIR));
	}
	
	
	public static void openFile(GUI gui, File target) throws IOException {
		// Opens a file and loads into GUI
		if (gui == null || !gui.getTextArea().getText().equals("")) {
			gui = App.newGUI(new Stage());
		}
		
		BufferedReader in = new BufferedReader(new FileReader(target));
		gui.getTextArea().read(in, null);
		in.close();
		
		gui.updateFooter();
		gui.getStage().setTitle(target.getPath());
		gui.setLanguage(getFileExtension(target));
	}
	
	public static void openFileODT(GUI gui, File target) {
		try {
			com.aspose.words.Document doc = new com.aspose.words.Document(target.getPath());
			String text = doc.getText();
			text = text.substring(79, text.length()-142);
			String[] textArray = text.split(String.valueOf(text.charAt(0)));
			textArray = Arrays.copyOfRange(textArray, 1, textArray.length);
			
			if (gui == null || !gui.getTextArea().getText().equals("")) {
				gui = App.newGUI(new Stage());
			}
			
			for (String t : textArray) {
				gui.getTextArea().append(t+"\n");
			}
			
			gui.getStage().setTitle(target.getPath());
			gui.setLanguage(getFileExtension(target));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public static void openFile(GUI gui) throws IOException {
		// OVERLOAD: if no file is specified, choose with FILE_CHOOSER dialogue
		FILE_CHOOSER.setTitle("Load...");
		FILE_CHOOSER.getExtensionFilters().setAll(FILTER, ALL_FILTER);
		File target = FILE_CHOOSER.showOpenDialog(null);
		if (target != null) {
			if (getFileExtension(target).equals("odt")) {
				openFileODT(gui, target);
			} else {
				openFile(gui, target);
			}
		}
	}
	
	
	public static void saveFile(GUI gui, File f) throws IOException {
		// Saves data in the text editor to a file
		if (getFileExtension(f).contentEquals("pdf")) {
			String[] text = gui.getTextArea().getText().split("\n");
			Document doc = new Document();
			try {
				PdfWriter.getInstance(doc, new FileOutputStream(f));
				 
				doc.open();
				for (String line : text) {
					Paragraph p = new Paragraph(line);
					doc.add(p);
				}
				doc.close();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		} else {
			BufferedWriter out =  new BufferedWriter(new FileWriter(f));
			gui.getTextArea().write(out);
			out.close();
			
			gui.getStage().setTitle(f.getPath());
			gui.setLanguage(getFileExtension(f));
		}
	}
	
	public static void saveFile(GUI gui) throws IOException {
		// OVERLOAD: if no file is specified, choose with FILE_CHOOSER dialogue		
		FILE_CHOOSER.setTitle("Save as...");
		FILE_CHOOSER.getExtensionFilters().setAll(FILTER, ALL_FILTER, PDF_FILTER);
		File f = FILE_CHOOSER.showSaveDialog(null);
		if (f != null) {
			saveFile(gui, f);
		}
	}
	
	
	//gets the file extension from a file name
	public static String getFileExtension(File file) {
		return FilenameUtils.getExtension(file.getPath());
	}
	
}

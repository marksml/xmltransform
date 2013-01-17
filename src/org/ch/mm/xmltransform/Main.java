package org.ch.mm.xmltransform;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class Main {
	
//	final static String srcFolder ="/home/marksml/Dropbox/git/xmltransform/data";
	final static String outFileName="result.csv";
	final static String xslFileName="FragebogenTransformation.xsl";
	final static String columnNames ="Dateiname,Frage1,Frage2,Frage3,Frage4,Frage5,Frage6,Frage7,Frage8,Frage9,Frage10,Frage11"; 
			
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
		
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Please select the xml source folder");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File srcDir = fc.getSelectedFile();
			CsvBuilder builder = new CsvBuilder();
			
			StringBuffer log = new StringBuffer();
			
			builder.buildCsv(srcDir, outFileName, xslFileName, columnNames, log);
			
			//default title and icon
			JOptionPane.showMessageDialog(null, log);
			
			
		}

	}
	
	

}

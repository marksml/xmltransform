package org.ch.mm.xmltransform;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class Main implements ActionListener {
	
	final static String outFileName="result.csv";
	final static String xslFileName="FragebogenTransformation.xsl";
	final static String columnNames ="Dateiname,Frage1,Frage2,Frage3,Frage4,Frage5,Frage6,Frage7,Frage8,Frage9,Frage10,Frage11"; 
			
	int width = 800;
	int height = 600;

	final static String CMD_SELECT_SRC_FOLDER = "SELECT";
	final static String CMD_START = "START";
	final static String CMD_CLOSE = "CLOSE";
	
	File srcFolder = null;
	JTextArea log = null;
	JButton startButton = null;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Main main = new Main();
		main.start();
	}
	
	private File selectSourceFolder(){
		
		File result = null; 
				
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Bitte wähle das XML-Quellsverzeichnis");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			result = fc.getSelectedFile();
		}
		return result;
	}
	
	public void start(){
		
		JFrame frame = new JFrame("XmlTransform");
		frame.setSize(width, height);
		
		JPanel topPanel = new JPanel();
		topPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		topPanel.setSize(width, 60);
		
		JLabel dirLabel = new JLabel("Bitte wähle das XML-Quellsverzeichnis : ");
		JButton fileChooserButton = new JButton("Wählen");
		fileChooserButton.setActionCommand(CMD_SELECT_SRC_FOLDER);
		fileChooserButton.addActionListener(this);
		
		
		topPanel.add(dirLabel);
		topPanel.add(fileChooserButton);
		
		log = new JTextArea();
		JScrollPane centerPanel = new JScrollPane(log);
		
		JPanel footerPanel = new JPanel();
		footerPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
//		footerPanel.setLayout(new BoxLayout(footerPanel,BoxLayout.X_AXIS));
//		footerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		startButton = new JButton("Transformation starten");
		startButton.setActionCommand(CMD_START);
		startButton.addActionListener(this);
		startButton.setEnabled(false);
		footerPanel.add(startButton);
		
		JButton closeButton = new JButton("Programm beenden");
		closeButton.setActionCommand(CMD_CLOSE);
		closeButton.addActionListener(this);
		footerPanel.add(closeButton);


		frame.add(topPanel, BorderLayout.PAGE_START);
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.add(footerPanel, BorderLayout.PAGE_END);
		
		frame.setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		
		String cmd = event.getActionCommand();
		
		if(CMD_SELECT_SRC_FOLDER.equals(cmd)){
			srcFolder = selectSourceFolder();
			log("\nGewähltes Quellverzeichnis: "+ srcFolder.getAbsolutePath());
			startButton.setEnabled(true);
			
		}else if(CMD_START.equals(cmd)){
			buildCsv();
		}else if(CMD_CLOSE.equals(cmd)){
			System.exit(0);
		}
		
	}

	public void buildCsv(){

		log("\n");
		
		try{
			
			
			if(srcFolder != null){
				XmlFilter filter = new XmlFilter();
				
				File[] files = srcFolder.listFiles(filter);
				
				if(files.length > 0){
					File outFile = new File(srcFolder,outFileName);
					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
					writer.println(columnNames);
							
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					
					StreamSource xsl = new StreamSource(this.getClass().getResourceAsStream(xslFileName));
					Transformer xsltTransformer = transformerFactory.newTransformer(xsl);
					
					for(File file: files){
						writer.print(file.getName()+",");
						xsltTransformer.transform(new StreamSource(file),new StreamResult(writer));
						writer.println();
						log("Verarbeite Datei '"+file.getAbsolutePath()+"'");
					}
					writer.flush();
					writer.close();
					log("\n\n"+files.length+" Dateien verarbeitet.\n\n");
					log("Ergebnisdatei :  "+outFile.getAbsolutePath());
				}else{
					log("Keine XML Dateien im gewählten Quellverzeichnis gefunden.\n");
					
				}
				
				
			}
			
			
		}catch(Exception ex){
			log("Ein Fehler ist aufgetreten: ");
			log(ex.getMessage());
		}
		
		
	}

	
	private void log(String text){
		log.append(text+"\n");
		
	}

}

package org.ch.mm.xmltransform;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Main implements ActionListener {
	
	final static String outFileName="result.csv";
	final static String xslFileName="FragebogenTransformation.xsl";
	String columnNames = null; //"Dateiname,Frage1,Frage2,Frage3,Frage4,Frage5,Frage6,Frage7,Frage8,Frage9,Frage10,Frage11"; 
			
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
	
	public void setLookAndFeel(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
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
		setLookAndFeel();
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
			try{ 
				log("\nGewähltes Quellverzeichnis: "+ srcFolder.getAbsolutePath()); //This was throwing an error if you chose to cancel choosing a file directory
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			//resetting columnNames;
			columnNames = null;
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
							
					for(File file: files){
						log("Verarbeite Datei '"+file.getAbsolutePath()+"'");
						
						if(columnNames == null){
							columnNames = calculateHeader(file);
							writer.println(columnNames);
						}

						String textValues = calculateValues(file);						
						writer.println(textValues);
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
			ex.printStackTrace();
		}
		
		
	}
	
	private String calculateHeader(File xmlDoc) throws Exception{
		StringBuffer bf = new StringBuffer("Dateiname");

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document doc = parser.parse(xmlDoc);
		
		Node rootNode = doc.getFirstChild();
		
		NodeList children = rootNode.getChildNodes();

		if(children.getLength() > 0 ){
			for(int i=0; i< children.getLength();i++){
				Node childNode = children.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE){
					bf.append(",");
					bf.append(childNode.getNodeName());	
				}
			} 
		}
		
		String result = bf.toString();
		log("\nIdentifizierte Spaltennamen:");
		log(result+"\n");
		
	    return result;
	}

	private String calculateValues(File xmlDoc) throws Exception{
		StringBuffer bf = new StringBuffer();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document doc = parser.parse(xmlDoc);
		
		Node rootNode = doc.getFirstChild();
		
		NodeList children = rootNode.getChildNodes();
		
		if(children.getLength() > 0 ){
			
			bf.append(xmlDoc.getName());
			
			for(int i=0; i< children.getLength();i++){
				Node childNode = children.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE){
					bf.append(",");
					
					Node textNode = childNode.getFirstChild();
					if(textNode != null){
						bf.append(textNode.getNodeValue());		
					}else{
						bf.append("");
					}
					
				}
			} 
		}
		
		String result = bf.toString();
		
	    return result;
	}
	
	private void log(String text){
		log.append(text+"\n");
		System.out.println(text);
	}

}

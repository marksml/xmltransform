package org.ch.mm.xmltransform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



public class CsvBuilder {
	
	public void buildCsv(File srcDir, String outFileName, String xslFileName, String columnNames, StringBuffer log) throws Exception{
		
		XmlFilter filter = new XmlFilter();
		
		File outFile = new File(srcDir,outFileName);
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
		writer.println(columnNames);
				
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		StreamSource xsl = new StreamSource(this.getClass().getResourceAsStream(xslFileName));
		Transformer xsltTransformer = transformerFactory.newTransformer(xsl);
				
		if(srcDir.exists() && srcDir.isDirectory()){
			File[] files = srcDir.listFiles(filter);
			for(File file: files){
				writer.print(file.getName()+",");
				xsltTransformer.transform(new StreamSource(file),new StreamResult(writer));
				writer.println();
			}
			writer.flush();
			writer.close();
			log.append(files.length+" files transformed and writen to : "+outFile.getAbsolutePath()+"\n");
			
		}else{
			throw new Exception("Selected Source directory '"+srcDir+"' not existing or is no directory.");
		}
		
		log.append("Program finished.");
		
		
	}


}

package org.ch.mm.xmltransform;

import java.io.File;
import java.io.FilenameFilter;

public class XmlFilter implements FilenameFilter{

	@Override
	public boolean accept(File dir, String fileName) {
		
		if(fileName.toLowerCase().indexOf(".xml") > 0){
			return true;
		}else{
			return false;
		}
	}	

}

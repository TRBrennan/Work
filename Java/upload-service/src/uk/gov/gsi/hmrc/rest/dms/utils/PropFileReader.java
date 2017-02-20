package uk.gov.gsi.hmrc.rest.dms.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropFileReader {
	Properties prop = null;
	InputStream input = null;
	
	public PropFileReader(String filename) throws IOException{
		prop = new Properties();		
		input = new FileInputStream(filename);
		// load a properties file
		prop.load(input);
	}
	
	public String getValue(String valueName){			
		return prop.getProperty(valueName);
	}
}

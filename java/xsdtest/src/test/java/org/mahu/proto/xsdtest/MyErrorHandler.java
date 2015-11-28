package org.mahu.proto.xsdtest;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
 
public class MyErrorHandler implements ErrorHandler {
	
	public boolean invoked = false;
 
    public void warning(SAXParseException exception) throws SAXException {
    	invoked = true;
        System.out.println("\nWARNING");
        exception.printStackTrace();
    }
 
    public void error(SAXParseException exception) throws SAXException {
    	invoked = true;
        System.out.println("\nERROR");
        exception.printStackTrace();
    }
 
    public void fatalError(SAXParseException exception) throws SAXException {
    	invoked = true;
        System.out.println("\nFATAL ERROR");
        exception.printStackTrace();
    }
 
}

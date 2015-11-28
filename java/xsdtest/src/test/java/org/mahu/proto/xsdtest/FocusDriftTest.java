package org.mahu.proto.xsdtest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.mahu.proto.xsdtest.schema.FocusDriftList;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class FocusDriftTest {

	private final static String xmlFile = "FocusDriftExample.txt";

	@Test
	public void testTwoAlternativesToReadXmlFile() throws IOException,
			URISyntaxException, JAXBException, SAXException {
		// Two alternatives to get the load the text document
		String xmlDoc1 = readFileFromResource1();
		String xmlDoc2 = readFileFromResource2();
		assertTrue(xmlDoc1.equals(xmlDoc2));
	}

	/**
	 * This test cases checks an XML document against an XSD and next creates a Java Object.
	 * With the test case I wanted to verify if the XML document is required to have a reference to the XSD.
	 * The conclusion is no.   
	 */
	@Test
	public void testReadBaseXmlDocument() throws IOException, URISyntaxException, JAXBException, SAXException { 
		// Including the class name is the newInstance is required to make the testcase pass
		JAXBContext jaxbContext = JAXBContext.newInstance(FocusDriftList.class);
		//
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new File(getFolderTargetClasses(),
				"xsd/FocusDrift.xsd"));
		//
		MyValidationEventHandler handler = new MyValidationEventHandler();
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		jaxbUnmarshaller.setSchema(schema);
		jaxbUnmarshaller.setEventHandler(handler);
		// 
		FocusDriftList focusDriftList = (FocusDriftList) jaxbUnmarshaller
				.unmarshal(new StringReader(readFileFromResource2()));		
		//
		// Check that no faults were reported
		assertTrue(handler.invoked == false);
		//
		assertTrue(focusDriftList.getFocCorList().size()==3);
	}

	private String readFileFromResource1() throws IOException {
		URL url = Resources.getResource(xmlFile);
		String text = Resources.toString(url, Charsets.UTF_8);
		return text;
	}

	private String readFileFromResource2() throws IOException,
			URISyntaxException {
		File folder = getFolderTargetTestClasses();
		System.out.println("Folder: " + folder);
		String content = Files.toString(new File(folder, xmlFile),
				Charsets.UTF_8);
		return content;
	}

	private File getFolderTargetClasses() throws URISyntaxException {
		File folder = getFolderTargetTestClasses();
		return new File(folder, "../classes");
	}

	private File getFolderTargetTestClasses() throws URISyntaxException {
		URL url = ClassLoader.getSystemResource("");
		File folder = new File(url.toURI());
		return folder;
	}

}

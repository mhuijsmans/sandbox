package org.mahu.proto.xsdtest;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

class XmlUtil {

	public static String marshallToString(final Object xmlObject,
			final String[] pathsToXsd) throws SAXException, JAXBException,
			IOException {
		Source[] sources = new Source[pathsToXsd.length];
		int i = 0;
		for (String pathToXsd : pathsToXsd) {
			sources[i++] = new StreamSource(IOUtils.getResourceAsStream(
					xmlObject.getClass(), pathToXsd));
		}
		return marshallToString(xmlObject, sources);
	}

	public static String marshallToString(final Object xmlObject,
			final Source[] sources) throws SAXException, JAXBException,
			IOException {
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// sf.setResourceResolver(new XsdResourceResolver());
		// URL xsdFile = IOUtils.getResourceUrl(xmlObject.getClass(),
		// pathsToXsd[0]);
		// Schema schema = sf.newSchema(xsdFile);
		Schema schema = sf.newSchema(sources);
		JAXBContext jaxbContext = JAXBContext.newInstance(xmlObject.getClass());
		//
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setSchema(schema);
//		MyValidationEventHandler handler = new MyValidationEventHandler();
//		jaxbMarshaller.setEventHandler(handler);
		//
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//
		return marshalToString(xmlObject, jaxbMarshaller);
	}
	
	public static String marshallToStringNoValidation(final Object xmlObject) throws JAXBException {
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(xmlObject.getClass());            
		Marshaller jaxbMarshaller = context.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);		
		jaxbMarshaller.marshal(xmlObject, writer);
		return writer.toString();
	}

	public static void writeToFile(final Object xmlObject,
			final File outputFile, final String pathToXsd) throws SAXException,
			JAXBException, IOException {
		final String xmlDocAsString = marshallToString(xmlObject,
				new String[] { pathToXsd });
		IOUtils.writeStringToFile(outputFile, xmlDocAsString);
	}

	public static void writeToFile(final Object xmlObject,
			final File outputFile, final String[] pathsToXsd)
			throws SAXException, JAXBException, IOException {
		final String xmlDocAsString = marshallToString(xmlObject, pathsToXsd);
		IOUtils.writeStringToFile(outputFile, xmlDocAsString);
	}

	private static String marshalToString(final Object xmlObj,
			final Marshaller jaxbMarshaller) throws JAXBException,
			UnsupportedEncodingException {
		StringWriter writer = new StringWriter();
		jaxbMarshaller.marshal(xmlObj, writer);
		String out =  writer.toString();
		return out;
	}

}

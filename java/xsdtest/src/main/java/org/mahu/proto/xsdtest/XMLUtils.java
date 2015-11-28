package org.mahu.proto.xsdtest;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class XMLUtils {
	
	public static String marshalToString(final Object xmlObject, Marshaller jaxbMarshaller)
			throws JAXBException, UnsupportedEncodingException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		jaxbMarshaller.marshal(xmlObject, os);
		String out = new String(os.toByteArray(), "UTF-8");
		return out;
	}

}

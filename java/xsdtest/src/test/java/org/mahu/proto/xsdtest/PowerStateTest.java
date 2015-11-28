package org.mahu.proto.xsdtest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.mahu.proto.xsdtest.schema.PowerState;

public class PowerStateTest {

	// For simple elements no Root classes are generated
	// http://learningviacode.blogspot.nl/2013/05/jaxb-and-simple-elements.html

	@Test
	public void test() throws JAXBException, UnsupportedEncodingException {
		PowerState ps = PowerState.ON;
		String out = "";
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(PowerState.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// Set properties: encoding & output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			jaxbMarshaller.marshal(new JAXBElement(new QName("", "PowerState"), PowerState.class, ps), os);
			out = new String(os.toByteArray(), "UTF-8");
			//
			// marshaling to String requires some own plumbing
			System.out.println(out);
		}

		{
			JAXBContext jaxbContext = JAXBContext.newInstance();
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			InputStream stream = new ByteArrayInputStream(out.getBytes(StandardCharsets.UTF_8));
			StreamSource streamSource = new StreamSource(stream);
			JAXBElement<String> ele = unmarshaller.unmarshal(streamSource, String.class);
			System.out.println(ele.getName());
			System.out.println(ele.getValue());
		}

	}

}

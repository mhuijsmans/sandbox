package org.mahu.proto.xsdtest;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.mahu.proto.xsdtest.schema1.ImageType1;
import org.mahu.proto.xsdtest.schema3.ImageType3;
import org.xml.sax.SAXException;

public class MarshalingTest {

	@Test
	public void javaToString_dataType_embedded_Test() throws SAXException, JAXBException,
			IOException {
		String[] pathsToXsd = new String[] { "xsd/image1.xsd" };
		ImageType1 image = createImage1();
		System.out.println(XmlUtil.marshallToString(image, pathsToXsd));
	}
	
	@Test
	public void javaToString_dataType_include_Test() throws SAXException, JAXBException,
			IOException {
		// Lesson: order matters !!
		String[] pathsToXsd = new String[] { "xsd/data-types3.xsd", "xsd/image3.xsd" };
		ImageType3 image = new ImageType3();
		image.setPixel("a");
		System.out.println(XmlUtil.marshallToString(image, pathsToXsd));
	}	

	@Test
	public void javaToStringNoValidationTest() throws JAXBException {
		ImageType1 image = createImage1();
		System.out.println(XmlUtil.marshallToStringNoValidation(image));
	}

	protected ImageType1 createImage1() {
		String pixel = "pixel";
		ImageType1 image = new ImageType1();
		image.setPixel(pixel);
		// image.setColor("blue");
		return image;
	}

}

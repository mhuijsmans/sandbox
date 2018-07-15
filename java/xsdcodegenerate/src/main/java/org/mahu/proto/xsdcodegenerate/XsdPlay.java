package org.mahu.proto.xsdcodegenerate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.mahu.proto.xsdcodegenerate.xml.ObjectFactory;
import org.mahu.proto.xsdcodegenerate.xml.Option2;

public class XsdPlay {

	public static void main(final String[] args) throws JAXBException {
		test1();

		System.out.println();

		test2();
	}

	private static void test1() throws JAXBException, PropertyException {
		final ObjectFactory of = new ObjectFactory();
		final JAXBElement<Integer> v = of.createOption1(3);

		final JAXBContext jaxbContext = JAXBContext.newInstance();
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(v, System.out);
	}

	private static void test2() throws JAXBException, PropertyException {
		final Option2 tdi2 = new Option2();
		tdi2.setOptionValue2(5);

		final JAXBContext jaxbContext = JAXBContext.newInstance(Option2.class);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		// output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		jaxbMarshaller.marshal(tdi2, System.out);
	}

}

/*
 * Generated: 
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?> 
 * <Option2>
 *   <OptionValue2>5</OptionValue2>
 * </Option2>
 *
 * SPD_SW: <?xml version="1.0" encoding="UTF-8" ?> <Option>64</Option>
 */

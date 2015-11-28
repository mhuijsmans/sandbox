package org.mahu.proto.xsdtest;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.mahu.proto.xsdtest.schema.AddressType;
import org.mahu.proto.xsdtest.schema.CustomerType;
import org.mahu.proto.xsdtest.schema.OrderType;
import org.mahu.proto.xsdtest.schema.Root;
import org.mahu.proto.xsdtest.schema.Root.Customers;
import org.mahu.proto.xsdtest.schema.ShipInfoType;
import org.xml.sax.SAXException;

/**
 * Test to play with validation, marshalling, unmarshalling and comparing XML Objects.
 */
public class AppTest {

	/**
	 * Explicit Schema validation.
	 */

	@Test
	public void validateJavaObjectExplicitly()
			throws DatatypeConfigurationException, JAXBException, SAXException,
			IOException {
		//
		// Create java representation of document.
		Root root1 = createRootObjectWithCustomersAndOrders();
		//
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Using a URL ensures that the xsd can be found when resource is
		// file on file system or file packaged in a jar.
		Schema schema = sf.newSchema(IOUtils.getResourceUrl(IOUtils.class,
				"xsd/CustomerOrders.xsd"));
		//
		// Note: you MUST specific the Root.class
		JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
		//
		// ANOTHER validation option
		// Validate the Object model
		JAXBSource source = new JAXBSource(jaxbContext, root1);
		Validator validator = schema.newValidator();
		MyErrorHandler myErrorHandler = new MyErrorHandler();
		validator.setErrorHandler(myErrorHandler);
		validator.validate(source);
		assertTrue(myErrorHandler.invoked == false);
	}

	/**
	 * Implicit Schema validation: as part of marshalling
	 */

	@Test
	public void validateJavaObjectDuringMarshalling()
			throws DatatypeConfigurationException, JAXBException, SAXException,
			IOException {
		//
		// Create java representation of document.
		Root root1 = createRootObjectWithCustomersAndOrders();
		//
		SchemaFactory sf = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// Using a URL ensures that the xsd can be found when resource is
		// file on file system or file packaged in a jar.
		Schema schema = sf.newSchema(IOUtils.getResourceUrl(IOUtils.class,
				"xsd/CustomerOrders.xsd"));
		//
		// Note: you MUST specific the Root.class
		JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		//
		// Set Schema validation, used when marshal(..) is called.
		// Note: no exception will be throw. Instead, the application
		// EventHandler
		jaxbMarshaller.setSchema(schema);
		MyValidationEventHandler handler = new MyValidationEventHandler();
		jaxbMarshaller.setEventHandler(handler);
		// handler will be invoked if during marshalling something is wrong
		jaxbMarshaller.marshal(root1, System.out);
		assertTrue(handler.invoked == false);
		//
		// ANOTHER validation option
		// Validate the Object model
		JAXBSource source = new JAXBSource(jaxbContext, root1);
		Validator validator = schema.newValidator();
		MyErrorHandler myErrorHandler = new MyErrorHandler();
		validator.setErrorHandler(myErrorHandler);
		validator.validate(source);
		assertTrue(myErrorHandler.invoked == false);
	}

	/**
	 * Marshall a JavaObject. No schema validation
	 */
	@Test
	public void marshallJavaObject() throws DatatypeConfigurationException,
			JAXBException, SAXException, IOException {
		//
		// Create java representation of document.
		Root root1 = createRootObjectWithCustomersAndOrders();
		//
		// Note: you MUST specific the Root.class
		JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		//
		// Set properties: encoding & output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//
		// marshall to file is possible
		File targetDir = getOutputDir();
		File file = new File(targetDir, "file.xml");
		file.delete();
		jaxbMarshaller.marshal(root1, file);
		assertTrue(file.exists());
		//
		// Or marshaling to System.out
		jaxbMarshaller.marshal(root1, System.out);
	}

	/**
	 * Comparing different XML objects
	 */
	@Test
	public void comparingJavaXmlObjects()
			throws DatatypeConfigurationException, JAXBException, SAXException,
			IOException {
		//
		// Create java representation of document.
		Root root1 = createRootObjectWithCustomersAndOrders();
		//
		// Convert from Object to XML representation
		JAXBContext jaxbContext = JAXBContext.newInstance(Root.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		//
		// Set properties: encoding & output pretty printed
		jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		//
		// marshaling to String requires some own plumbing
		String out1 = XMLUtils.marshalToString(root1, jaxbMarshaller);
		//
		// unmarshaling the above XML string and next marshaling it again.
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		//
		Root root2 = (Root) jaxbUnmarshaller
				.unmarshal(new ByteArrayInputStream(out1.getBytes()));
		//
		String out2 = XMLUtils.marshalToString(root2, jaxbMarshaller);
		System.out.println(out2);
		//
		// Next line fails.
		// assertTrue(root1.equals(root2));
		// So string comparison
		assertTrue(out1.equals(out2));
	}

	private Root createRootObjectWithCustomersAndOrders()
			throws DatatypeConfigurationException {
		//
		// There are some constraints that must be matched, customerId in
		// Customer and Order must match.
		String customerId = "customerId";
		String orderCustomerId = customerId; // +"1";
		//
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory
				.newInstance().newXMLGregorianCalendar(c);
		//
		BigDecimal bigDecimal = new BigDecimal("3.14159265358979323846"); // pi
		BigInteger bigInteger = new BigInteger("3");
		//
		Root root = new Root();
		Customers customers = new Root.Customers();
		root.setCustomers(customers);
		//
		CustomerType ct = createCustomerType(customerId);
		customers.getCustomer().add(ct);
		//
		Root.Orders orders = new Root.Orders();
		root.setOrders(orders);
		//
		OrderType ot = createOrderType(orderCustomerId, xmlGregorianCalendar,
				bigDecimal, bigInteger);
		orders.getOrder().add(ot);
		return root;
	}

	protected OrderType createOrderType(String customerId,
			XMLGregorianCalendar xmlGregorianCalendar, BigDecimal bigDecimal,
			BigInteger bigInteger) {
		OrderType ot = new OrderType();
		//
		ot.setCustomerID(customerId);
		ot.setEmployeeID("employeeId");
		ot.setOrderDate(xmlGregorianCalendar);
		ot.setRequiredDate(xmlGregorianCalendar);
		ShipInfoType sit = new ShipInfoType();
		sit.setFreight(bigDecimal);
		sit.setShipAddress("shipAddress");
		sit.setShipCity("city");
		sit.setShipCountry("shipCountry");
		sit.setShipName("ship");
		sit.setShippedDate(xmlGregorianCalendar);
		sit.setShipPostalCode("shipPostalCode");
		sit.setShipRegion("shipRegion");
		sit.setShipVia(bigInteger);
		ot.setShipInfo(sit);
		return ot;
	}

	protected CustomerType createCustomerType(String customerId) {
		CustomerType ct = new CustomerType();
		ct.setCompanyName("company");
		ct.setContactName("contactName");
		ct.setContactTitle("contactTitle");
		ct.setPhone("phone");
		ct.setFax("fax");
		AddressType addressType = new AddressType();
		addressType.setAddress("adress");
		addressType.setCity("city");
		addressType.setCountry("country");
		addressType.setPostalCode("postalcode");
		addressType.setRegion("region");
		ct.setFullAddress(addressType);
		ct.setCustomerID(customerId);
		return ct;
	}

	private File getMavenTargetDir() {
		File test_classes = IOUtils.getResourceFile(AppTest.class, ".");
		File target = test_classes.getParentFile();
		return target;
	}

	private File getOutputDir() {
		File targetDir = getMavenTargetDir();
		File outputDir = new File(targetDir, "outputdata");
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
		return outputDir;
	}
}

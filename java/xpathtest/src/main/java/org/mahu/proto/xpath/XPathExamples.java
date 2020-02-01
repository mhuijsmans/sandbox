package org.mahu.proto.xpath;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XPathExamples {
	
	// Read all commands
	static void readAllCommands(final Document doc, final XPath xpath) throws XPathExpressionException {
		XPathExpression expr = xpath.compile("/ScanSlideSessionReport/ExecutedCommands/ExecutedCommand");
		NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		System.out.println("length= " + nodeList.getLength());
	}

	// Read all commands with a certain name
	static void readCommandWithCertainName(final Document doc, final XPath xpath)
			throws XPathExpressionException {
		XPathExpression expr = xpath.compile(
				"/ScanSlideSessionReport/ExecutedCommands/ExecutedCommand[Name='SgseSlideQualification[SINGLE]']");
		NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		System.out.println("length= " + nodeList.getLength());
	}

}

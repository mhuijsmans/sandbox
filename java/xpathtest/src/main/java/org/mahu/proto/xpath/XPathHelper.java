package org.mahu.proto.xpath;

import java.nio.file.Path;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

final class XPathHelper {

	// Create XPathFactory object
	private final XPathFactory xpathFactory = XPathFactory.newInstance();
	// Create XPath object
	private final XPath xpath = xpathFactory.newXPath();
	private final Document doc;

	XPathHelper(Document doc) {
		this.doc = doc;
	}

	public XPathHelper(Path xmlDocument) {
		this(XmlUtils.readXmlDocument(xmlDocument));
	}

	String readText(final String path) {
		XPathExpression exprEndTime;
		try {
			exprEndTime = xpath.compile(path);
			final String text = (String) exprEndTime.evaluate(doc, XPathConstants.STRING);
			return text;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	NodeList readNodes(final String path) {		
		XPathExpression exprEndTime;
		try {
			exprEndTime = xpath.compile(path);
			NodeList nodeList = (NodeList) exprEndTime.evaluate(doc, XPathConstants.NODESET);
			return nodeList;			
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

}

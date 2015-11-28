package org.mahu.proto.xsdcodegenerate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mahu.proto.commons.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParseXsdApp {

	final static String INDENT = ".";
	final static List<String> ignoreList = Arrays.asList("#text", "#comment");

	public static void main(String args[]) {
		try {
			// parse the document
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(IOUtils.getResourceFile(
					ParseXsdApp.class, "xsd/settings.xsd"));

			// The following 2 line represent interesting methods to iterate through the xsd.  
			NodeList list = doc.getElementsByTagName("xs:element");
			printNodes(list);
			list = doc.getChildNodes();
			printNodes(list);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException ed) {
			ed.printStackTrace();
		}
	}

	protected static void printNodes(NodeList list) {
		// loop to print data
		for (int i = 0; i < list.getLength(); i++) {
			Element first = (Element) list.item(i);
			printElementAttributes("", first);
			inspectChildren(INDENT, first);
		}
	}

	protected static void printElementAttributes(final String indent,
			Element first) {
		StringBuilder sb = new StringBuilder();
		sb.append(indent);
		sb.append("[").append(first.getTagName()).append("]");
		appendAttribute(first, sb, "base");
		appendAttribute(first, sb, "name");
		appendAttribute(first, sb, "type");
		appendAttribute(first, sb, "minOccurs");
		appendAttribute(first, sb, "maxOccurs");
		System.out.println(sb);
	}

	protected static void inspectChildren(final String indent,
			final Element element) {
		NodeList children = element.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node child = children.item(j);
			if (child instanceof Element) {
				Element childElement = (Element) child;
				printElementAttributes(indent, childElement);
				inspectChildren(indent + INDENT, childElement);
			} else {
				String nodeName = child.getNodeName();
				if (!ignoreList.contains(nodeName)) {
					StringBuilder sb = new StringBuilder();
					sb.append(indent);
					sb.append("[").append(nodeName).append("]*");
					System.out.println(sb);
				}
			}
		}
	}

	protected static void appendAttribute(Element first, StringBuilder sb,
			String attributeName) {
		String value = first.getAttribute(attributeName);
		if (value != null && value.length() > 0) {
			sb.append("[").append(attributeName).append("=").append(value)
					.append("]");
		}
	}
}

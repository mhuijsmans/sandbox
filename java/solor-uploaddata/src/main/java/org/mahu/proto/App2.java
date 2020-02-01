package org.mahu.proto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Used source: https://lucene.apache.org/solr/guide/8_3/using-solrj.html
 */
public class App2 {

	private static final String SOLR_URL = "http://130.144.225.242:8983/solr";
	private static final String SOLR_CORE = "sgse";

	private static final String ERROR_CODE = "ErrorCode";
	private static final String ID = "id";
	private static final String ERROR_ID = "ErrorId";
	private static final String CAUSE = "Cause";
	private static final String ACTION = "Action";

	public static void main(String[] args)
			throws SolrServerException, IOException, ParserConfigurationException, SAXException {
		
		SolrUtils.deleteAllRecordsInSgseCore();
		
		// addDocumentUsingMap();
		String filename = "C:/Users/310160231/OneDrive - Philips/desktop/backlog/124_troubleshooting_guide/ErrorCauseActionList.xml";
		readXmlDoc(filename, new SolrDocumentObserver());
	}
	
	static class SolrDocumentObserver implements IDocumentObserver {
		
		private final SolrInputDocument doc = new SolrInputDocument();

		public void add(String key, Object value) {
			doc.addField(key, value);
			System.out.println(key+": " + value);			
		}

		public void done() {
			try {
				SolrUtils.addDocumentUsingMap(doc);
			} catch (SolrServerException | IOException e) {
				throw new RuntimeException(e);
			}
			doc.clear();
		}
		
	}

	interface IDocumentObserver {
		void add(String key, Object value);
		void done();
	}

	static void readXmlDoc(final String filename, IDocumentObserver observer) throws ParserConfigurationException, SAXException, IOException {
		Document doc = readDocument(filename);

		System.out.println("root of xml file: " + doc.getDocumentElement().getNodeName());

		NodeList nodes = doc.getElementsByTagName(ERROR_CODE);
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			
			final String errorId = getAttribute(node, ID);
			observer.add(ERROR_ID, errorId);
			
			final String cause = getValue(CAUSE, node);
			observer.add(CAUSE, cause);
			
			final List<String> actions = getValues(ACTION, node);
			observer.add(ACTION, actions);
			
			observer.done();
		}
	}

	private static String getAttribute(Node node, final String key) {
		return node.getAttributes().getNamedItem(key).getTextContent();
	}

	private static Document readDocument(final String filename)
			throws ParserConfigurationException, SAXException, IOException {
		File stocks = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(stocks);
		doc.getDocumentElement().normalize();
		return doc;
	}

	private static String getValue(String tag, Node node) {
		Element element = castToElement(node);
		NodeList nodes = element.getElementsByTagName(tag);
		return getValueFromNode(nodes, 0);
	}

	private static List<String> getValues(String tag, Node node) {
		Element element = castToElement(node);
		List<String> values = new ArrayList<String>();
		NodeList nodes = element.getElementsByTagName(tag);
		for (int i = 0; i < nodes.getLength(); i++) {
			values.add(getValueFromNode(nodes, i));
		}
		return values;
	}

	private static String getValueFromNode(NodeList nodes, int idx) {
		NodeList childNodes = nodes.item(idx).getChildNodes();
		return ((Node) childNodes.item(0)).getNodeValue();
	}

	private static Element castToElement(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return (Element) node;
		}
		throw new RuntimeException("Node is not an element node: " + node.getNodeName());
	}

}

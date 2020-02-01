package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class QprMostRecentReader {

	private final XPathHelper xPath;

	QprMostRecentReader(Path file) {
		this.xPath = new XPathHelper(file);
	}

	SgseQualificationState readSgseQualifiedState() {
		try {
			return readQualificationState(xPath);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	void calculateQprStatistcs() {
		try {
			calculateQprStatistcs(xPath);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private static void calculateQprStatistcs(XPathHelper xPath) throws XPathExpressionException {
		NodeList qpr = xPath.readNodes("/QprMostRecent/Qualification/Qpr");
		getNodeText(qpr);
	}

	private static List<QprRecord> getNodeText(NodeList qpr) {
		List<QprRecord> list = new ArrayList<>();
		for (int i = 0; i < qpr.getLength(); i++) {
			Node node = qpr.item(i);
			NodeList qprProperties = node.getChildNodes();
			QprRecord qprRecord = new QprRecord();
			for (int j = 0; j < qprProperties.getLength(); j++) {
				Node qprProperty = qprProperties.item(j);
				qprRecord.add(qprProperty.getNodeName(), qprProperty.getNodeValue());
			}
		}
		return list;
	}

	private static SgseQualificationState readQualificationState(XPathHelper xPath) throws XPathExpressionException {
		final String qualificatonState = xPath.readText("/QprMostRecent/SgseQualificationState/text()");
		return qualificatonState.equalsIgnoreCase("qualified") ? SgseQualificationState.QUALIFIED
				: SgseQualificationState.NOT_QUALIFIED;
	}

}

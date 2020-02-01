package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.time.Duration;

import javax.xml.xpath.XPathExpressionException;

final class ScanReportReader {

	private final XPathHelper xPath;

	ScanReportReader(Path file) {
		this.xPath = new XPathHelper(file);
	}

	Duration readSqpElapsedTime() {
		try {
			return readElpasedTime1(xPath);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	// Read elapsedTime
	private static Duration readElpasedTime1(XPathHelper xPath) throws XPathExpressionException {
		final String nodeToFind = "/ScanSlideSessionReport/ExecutedCommands/ExecutedCommand[Name='SgseSlideQualification[SINGLE]']";
		final String startTimeString = xPath.readText(nodeToFind + "/StartTime/text()");
		final String endTimeString = xPath.readText(nodeToFind + "/EndTime/text()");

		// System.out.println("startTime=" + startTimeString + " endTime=" +
		// endTimeString);

		Duration between = TimeUtils.calculateDuration(startTimeString, endTimeString);
		return between;
	}

}

package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;

import javax.xml.xpath.XPathExpressionException;

public class ScanReportApp {
	public static void main(String[] args) throws XPathExpressionException {
		final Path dir = Paths.get(
				// "C:/Users/310160231/OneDrive -
				// Philips/testing/20181011_endurance_run_quake/EnduranceLogs_20181016.1");
				"C:\\Users\\310160231\\OneDrive - Philips\\testing\\20181011_endurance_run_quake\\EnduranceLogs_20181018\\endurancelog");

		IOUtils.getFiles(dir, "scanReport-run-*.xml").stream().sorted(Comparator.comparing(FoundFile::getId))
				.forEach(foundFile -> {
					ScanReportReader reader = new ScanReportReader(foundFile.getFile());
					Duration between = reader.readSqpElapsedTime();
					System.out.println(foundFile.id + "," + TimeUtils.calculateDurationInSecondsRounded(between));
				});
	}

}

package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.mahu.proto.xpath.LogFileReader.Counter;

public final class EnduranceAnalysisApp {
	
	static int  total =0;

	public static void main(String[] args) {
		// Directory holding the run logs
		final Path dir = Paths.get(
				"C:\\Users\\310160231\\OneDrive - Philips\\testing\\20181011_endurance_run_quake\\EnduranceLogs_20181024\\EnduranceLogs");
		final String dirName = "run-*";
		final Consumer<FoundDir> foundDirProcessor = foundDir -> {
			process(foundDir);
		};
		
		System.out.println("runId, sqpExecutionTimeInSec, sgseQualificationState, executionDetails");		
		
		IOUtils.processEachFoundDir(dir, dirName, foundDirProcessor);
		
		System.out.println("Average SQP.time=" + total / 67);
	}

	private static void process(FoundDir foundDir) {
		final int runId = foundDir.getId();

		Path scanReport = resolve(foundDir, "scanReport-run-" + runId + ".xml");
		ScanReportReader reader1 = new ScanReportReader(scanReport);
		Duration between = reader1.readSqpElapsedTime();

		Path qprReport = resolve(foundDir,"qpr-most-recent-run-" + runId + ".xml");
		QprMostRecentReader reader2 = new QprMostRecentReader(qprReport);
		SgseQualificationState sgseQualificationState = reader2.readSgseQualifiedState();
		reader2.calculateQprStatistcs();

		Path logfile = resolve(foundDir,"messages-run-" + runId + ".gz");
		LogFileReader reader3 = new LogFileReader(logfile);
		Map<String, Counter> stat = reader3.read().getCounters();

		total += TimeUtils.calculateDurationInSecondsRounded(between);
		System.out.println(runId + ", " + TimeUtils.calculateDurationInSecondsRounded(between) + ", "
				+ sgseQualificationState + ", " + statisticsToString(stat));
	}

	private static Path resolve(FoundDir foundDir, String string) {
		return foundDir.getFile().resolve(string);
	}

	private static String statisticsToString(Map<String, Counter> stat) {
		return stat.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue().getValue())
				.collect(Collectors.joining(", "));
	}

}

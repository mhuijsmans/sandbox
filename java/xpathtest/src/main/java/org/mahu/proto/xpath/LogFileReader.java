package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LogFileReader {

	private final Path logFile;
	private final SqpProcessor sqpProcessor = new SqpProcessor();

	LogFileReader(final Path logFile) {
		this.logFile = logFile;
	}

	public LogFileReader read() {
		final String filename = logFile.toFile().getName();
		if (filename.endsWith(".gz")) {
			IOUtils.readGzFile(logFile, sqpProcessor);
		} else if (filename.endsWith(".txt")) {
			IOUtils.readFile(logFile, sqpProcessor);
		} else {
			throw new RuntimeException("logfile extension (must be .txt/.gz) not supported: "+logFile.toString()) ;
		}
		return this;
	}

	public Map<String, Counter> getCounters() {
		return sqpProcessor.statistics.counters;
	}

	static class SqpProcessor implements Consumer<String> {

		enum State {
			OUTSIDE_SQP, INSIDE_SQP
		}

		private static final String SQP = "SQP";

		private State state = State.OUTSIDE_SQP;
		private Statistics statistics = new Statistics();

		@Override
		public void accept(String x) {
			if (x.contains("SQP")) {
				switch (state) {
				case OUTSIDE_SQP:
					if (x.contains(
							"SQP.SgseQualificationWorkFlow SgseQualification=SLIDESCAN QprLabelScope=IQ_SPECIFICATION, TriggerType=INTERNAL")) {
						state = State.INSIDE_SQP;
						print(x);
						statistics.step(SQP);
					}
					break;
				case INSIDE_SQP:
					if (x.contains(
							"SQP.QprStateManager: updateSgseQualificationStateBasedOnQprMostRecent SgseQualificationState")) {
						state = State.OUTSIDE_SQP;
						print(x);
					} else if (x.contains("SQP.CorrectiveProceduresLocator: sortedList=")) {
						print(x + " *");
						getCpNames(x).stream().forEach(cpName -> statistics.step(cpName));
					} else {
						print(x);
					}
				}
			}
		}

		private List<String> getCpNames(String x) {
			final String SORTED_LIST = "sortedList=[";
			// Example: SQP.CorrectiveProceduresLocator: sortedList=[HrimFocus, CAC2D] *
			final int idx1 = x.indexOf(SORTED_LIST);
			final int idx2 = x.indexOf(']', idx1);
			final String cpNames = x.substring(idx1 + SORTED_LIST.length(), idx2);
			return Arrays.asList(cpNames.split(", "));
		}

		private void print(String x) {
			// System.out.println(x);
		}

	}

	static class Statistics {
		private final Map<String, Counter> counters = new HashMap<>();

		private void step(final String name) {
			if (!counters.containsKey(name)) {
				counters.put(name, new Counter());
			}
			counters.get(name).value++;
		}

	}

	static class Counter {
		private int value;

		public int getValue() {
			return value;
		}
	}

}

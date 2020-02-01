package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class LogFileApp {

	public static void main(String[] args) {
		final Path dir = Paths
				.get("C:/Users/310160231/OneDrive - Philips/testing/20181011_endurance_run_quake/EnduranceLogs");
		final Path logFile = dir.resolve("messages.txt");

		final SqpProcessor sqpProcessor = new SqpProcessor();
		IOUtils.readFile(logFile, sqpProcessor);
		sqpProcessor.printStatistics();
	}

	static class SqpProcessor implements Consumer<String> {

		enum State {
			OUTSIDE_SQP, INSIDE_SQP
		}

		private static final String SQP = "SQP";

		private static final String NOT_QUALIFIED = "SgseIsNotQualified";

		private static final String QUALIFIED = "SgseIsQualified";

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
						boolean isQualified = determineIfSgseIsQualified(x);
						statistics.step(isQualified ? QUALIFIED : NOT_QUALIFIED);
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

		private boolean determineIfSgseIsQualified(String x) {
			return x.contains("SgseQualificationState=QUALIFIED");
		}

		private void printStatistics() {
			statistics.counters.forEach((name, counter) -> System.out.println(name + "=" + counter.value));
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
	}

}

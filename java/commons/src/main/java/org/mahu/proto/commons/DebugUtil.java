package org.mahu.proto.commons;

/**
 * This class creates printout's of StackTraces.
 * This is useful, because often Exception are wrapped into another Exception.
 * The stacktrace and the root cause provide useful insights.
 */
public class DebugUtil {

	private final static String NL = "\n";

	/**
	 * @param e
	 *            , exception for which to create report
	 * @return generated report
	 */
	public static StringBuilder createReport(final Throwable e) {
		StringBuilder sb = new StringBuilder();
		createReport(e, sb);
		return sb;
	}

	/**
	 * @param t
	 *            the thread for which to create report
	 * @param e
	 *            the exception for which to create report *
	 * @return generated report
	 */
	public static StringBuilder createReport(final Thread t, final Throwable e) {
		StringBuilder sb = new StringBuilder();
		createReport(t, sb);
		createReport(e, sb);
		return sb;
	}

	public static void createReport(final Thread t, final StringBuilder sb) {
		Thread.State state = t.getState();
		String name = t.getName();
		int priority = t.getPriority();
		long id = t.getId();
		// Threadgroup can be null if thread has died.
		ThreadGroup threadgroup = t.getThreadGroup();
		// todo: may other interesting artifacts
		sb.append("Thread").append(NL);
		sb.append(".name: ").append(name).append(NL);
		sb.append(".id: ").append(id).append(NL);
		sb.append(".priority: ").append(priority).append(NL);
		sb.append(".state: ").append(state).append(NL);
		String threadGroupName = (threadgroup != null) ? threadgroup.getName()
				: "<unknown, thread has died>";
		sb.append(".threadGroup.name: ").append(threadGroupName).append(NL);
	}

	/**
	 * @param e
	 *            the exception for which to create report
	 * @param sb
	 *            the will contain the generated report on completion
	 */
	public static void createReport(final Throwable e, final StringBuilder sb) {
		Throwable cause = e.getCause();
		Throwable causecause = (cause != null) ? cause.getCause() : null;
		Throwable[] throwables = e.getSuppressed();
		//
		sb.append("Uncaught Exception: ").append(NL);
		sb.append(".class: ").append(e.getClass().getName()).append(NL);
		sb.append(".message: ").append(e.getMessage()).append(NL);
		sb.append(".stacktrace: ").append(NL);
		appendStackTrace(sb, e);
		appendCause(sb, getRootcause(e), ".rootcause");
		appendCause(sb, cause, "..cause");
		appendCause(sb, causecause, "...causecause");
		if (throwables != null) {
			for (Throwable t : throwables) {
				sb.append(".supressed: ").append(t).append(NL);
				sb.append(".supressed.exception: ").append(t.getClass())
						.append(NL);
				sb.append(".supressed.message: ").append(t.getMessage())
						.append(NL);
				sb.append(".supressed.stacktrace: ").append(NL);
				sb.append("..<not printed>").append(NL);
			}
		}
	}

	private static Throwable getRootcause(final Throwable e) {
		return (e.getCause() != null) ? getRootcause(e.getCause()) : e;
	}

	private static void appendCause(final StringBuilder sb, Throwable cause,
			String prefix) {
		if (cause != null) {
			sb.append(prefix).append(": ").append(cause).append(NL);
			sb.append(prefix).append(".exception: ").append(cause.getClass())
					.append(NL);
			sb.append(prefix).append(".message: ").append(cause.getMessage())
					.append(NL);
			sb.append(prefix).append(".stacktrace: ").append(NL);
			appendStackTrace(sb, cause);
		}
	}

	private static void appendStackTrace(final StringBuilder sb,
			final Throwable t) {

		StackTraceElement[] list = t.getStackTrace();
		if (list != null) {
			for (StackTraceElement ste : list) {
				sb.append("..").append(ste.toString()).append(NL);
			}
		} else {
			sb.append("..").append("<No stack Trace>").append(NL);
		}
	}

}

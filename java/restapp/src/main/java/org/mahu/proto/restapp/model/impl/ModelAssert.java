package org.mahu.proto.restapp.model.impl;


final class ModelAssert {

	public static void checkState(final boolean b)
			throws ProcessBuilderException {
		if (!b) {
			throw new ProcessBuilderException("Invalid state");
		}
	}

	public static void checkState(final boolean b, final String msg)
			throws ProcessBuilderException {
		if (!b) {
			throw new ProcessBuilderException(msg);
		}
	}

	public static void checkArgument(final boolean b)
			throws ProcessBuilderException {
		if (!b) {
			throw new ProcessBuilderException("Invalid argument");
		}
	}

	public static void checkArgument(final boolean b, final String errorMsg)
			throws ProcessBuilderException {
		if (!b) {
			throw new ProcessBuilderException(errorMsg);
		}
	}

	public static void checkArgumentNotNull(final Object o)
			throws ProcessBuilderException {
		if (o == null) {
			throw new ProcessBuilderException("Null argument");
		}
	}
	
	public static void checkArgumentNotNull(final Object o, final String msg)
			throws ProcessBuilderException {
		if (o == null) {
			throw new ProcessBuilderException(msg);
		}
	}	

	public static void checkArgumentLengtGtZero(final String o)
			throws ProcessBuilderException {
		if (o == null) {
			throw new ProcessBuilderException("String argument is null");
		}
		if (o.length() == 0) {
			throw new ProcessBuilderException("String argument length is zero");
		}
	}

	public static void checkNotNull(final Object o, final String msg)
			throws ProcessBuilderException {
		if (o == null) {
			throw new ProcessBuilderException(msg);
		}
	}

}

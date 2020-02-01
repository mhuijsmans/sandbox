package org.mahu.proto.xpath;

import java.nio.file.Path;
import java.util.regex.Pattern;

final class FoundDir {
	private final int id;
	private final Path file;

	// regexp shall match: name-number
	private static final String formatSpecifier = "^(\\w+)-(\\d+)";
	private static final Pattern formatToken = Pattern.compile(formatSpecifier);

	FoundDir(final Path file) {
		this.file = file;
		id = extractId(file);
	}

	// Assumed format is like: name-*
	private static int extractId(Path scanReport) {
		final String dirName = scanReport.toFile().getName();
		if (formatToken.matcher(dirName).matches()) {
			final int idx = dirName.lastIndexOf('-');
			return Integer.parseInt(dirName.substring(idx + 1));
		} else {
			throw new RuntimeException("Name does not match requires pattern: " + dirName);
		}
	}

	int getId() {
		return id;
	}

	Path getFile() {
		return file;
	}

}

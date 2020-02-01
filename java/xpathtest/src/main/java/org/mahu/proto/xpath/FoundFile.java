package org.mahu.proto.xpath;

import java.nio.file.Path;

final class FoundFile {
	final int id;
	final Path file;
	
	FoundFile(final Path file) {
		this.file = file;
		id = extractId(file);
	}
	
	// Assumed format is like: name-*.extension, where * is a integer
	private static int extractId(Path scanReport) {
		final String fileName = scanReport.toFile().getName();
		final int idx1 = fileName.lastIndexOf('-');
		final int idx2 = fileName.lastIndexOf('.');
		return Integer.parseInt(fileName.substring(idx1+1, idx2));
	}
	
	int getId() {
		return id;
	}
	
	Path getFile() {
		return file;
	}

}

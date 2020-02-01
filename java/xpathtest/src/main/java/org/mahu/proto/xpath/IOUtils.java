package org.mahu.proto.xpath;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

final class IOUtils {
	
	static void processEachFoundDir(final Path dir, final String dirName,
			final Consumer<FoundDir> foundDirProcessor) {
		IOUtils.getDirectories(dir, dirName).stream().sorted(Comparator.comparing(FoundDir::getId))
				.forEach(foundDirProcessor);
	}	
	
	static List<FoundFile> getFiles(final Path dir, final String pattern) {
		List<FoundFile> files = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
			for (Path entry : stream) {
				files.add(new FoundFile(entry));
			}
		} catch (IOException x) {
			throw new RuntimeException(String.format("error reading folder %s: %s", dir, x.getMessage()), x);
		}
		return files;		
	}
	
	static List<FoundDir> getDirectories(final Path dir, final String pattern) {
		List<FoundDir> files = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					files.add(new FoundDir(entry));
				}
			}
		} catch (IOException x) {
			throw new RuntimeException(String.format("error reading folder %s: %s", dir, x.getMessage()), x);
		}
		return files;		
	}	
	
	static void readFile(final Path path, Consumer<String> consumer) {
		try {
			FileInputStream inputStream = new FileInputStream(path.toFile());
			readInputStream(inputStream, consumer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	static void readGzFile(final Path path, Consumer<String> consumer) {
		try {
			FileInputStream inputStream = new FileInputStream(path.toFile());
			readInputStream(new GZIPInputStream(inputStream), consumer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}	
	
	static void readInputStream(final InputStream inputStream, Consumer<String> consumer) {
		Scanner sc = null;
		try {
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				consumer.accept(line);
			}
			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw new RuntimeException(sc.ioException());
			}
		} finally {
			if (inputStream != null) {
				close(inputStream);
			}
			if (sc != null) {
				sc.close();
			}
		}
	}	

	private static void close(InputStream inputStream) {
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	

}

package org.mahu.proto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	private Path writableDirectoryPath;
	private Path writableFilePath;
	private Path notWritableDirectoryPath;
	private Path notWritableFilePath;

	@BeforeEach
	public void setUpFiles() throws IOException {
		String targetDirectory = ProjectUtils.getTargetDirectory(this.getClass());
		this.writableDirectoryPath = Paths.get(targetDirectory, "test-classes/temp");
		this.writableFilePath = this.writableDirectoryPath.resolve("output.xml");
	}

	@Test
	public void testApp0() throws IOException, InterruptedException {
		for (int maxHeapSize = 50; maxHeapSize > 0; maxHeapSize--) {
			
			System.out.println("maxHeapSize=" + maxHeapSize);
			
			List<String> jvmArgs = new ArrayList<>();
			jvmArgs.add(String.format("-Xmx%dm", maxHeapSize)); // max heap size
			jvmArgs.add("-XX:MaxMetaspaceSize=0m"); // max size metaspace to prevent
			// instability
			List<String> args = new ArrayList<>();
			args.add(new Integer(1000 * 10).toString());
			args.add(new Integer(1000).toString());
			int exitValue = JavaProcess.exec(FileWriterExternalTestMain.class, jvmArgs, args);

			assertEquals(0, exitValue, "maxHeapSize="+maxHeapSize);
		}
	}

//	@Test
//	public void testApp1() throws IOException {
//		Path destination = this.writableFilePath;
//		Path tempPath = this.writableDirectoryPath.resolve("temp");
//
//		URL[] urls = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
//		String classpath = Arrays.stream(urls).map(URL::getPath).collect(Collectors.joining(File.pathSeparator));
//
//		final int size = 1000;
//		String command = String.join(" ", "#!/bin/bash\n", "strace", "-fewrite,rename,fsync,open,close",
//				String.format("java -cp %s %s \"%s\"", classpath, FileWriterExternalTestMain.class.getName(), size));
//
//		System.out.println("Command:\n" + command);
//
//		Path script = Files.createTempFile(this.writableDirectoryPath, "testScript", ".sh");
//		Files.write(script, command.getBytes());
//		script.toFile().setExecutable(true);
//
//		Process p = new ProcessBuilder().command(script.toString()).start();
//		p.exitValue();
//
//		String output = IOUtils.toString(new InputStreamReader(p.getErrorStream()));
//
////        String[] lines = Arrays.stream(output.split(System.lineSeparator()))
////                .filter(l -> l.contains(writableFilePath.getFileName().toString()) || l.contains(fileContent))
////                .toArray(String[]::new);
//	}
}

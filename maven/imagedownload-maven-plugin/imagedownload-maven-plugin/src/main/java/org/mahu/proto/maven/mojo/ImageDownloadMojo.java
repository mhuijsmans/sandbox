package org.mahu.proto.maven.mojo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mahu.proto.maven.mojo.HttpClient.HttpClientException;

@Mojo(name = "imagedownload", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ImageDownloadMojo extends AbstractMojo {

	// Unit test uses a version of maven-plugin-testing-harness that doesn't
	// support annotations. It is also junit 3.x.
	// This Mojo is designed to be tested on Maven that does understand /
	// support annotations.
	// So for test to work, the used variable names (e.g. targetDirectory) needs
	// to match the values in the test pom.xml.
	// This is not what I want, but it is what works right now.
	// When used with maven 3.x (target), the annotations do work.

	// basedir is the directory of the project that includes this MOJO. The
	// basedir holds the pom.
	// For the Mojo test, basedir is null;
	@Parameter(property = "project.build.directory")
	private File targetDirectory;

	@Parameter(property = "imageBaseurl")
	private String imageBaseurl;

	@Parameter(property = "imageDirectory")
	private String imageDirectory;

	@Parameter(property = "imageNames")
	private List<String> imageNames;

	public void execute() throws MojoExecutionException {
		getLog().info("ImageDownloadMojo ENTER");
		Chrono chrono = new Chrono();
		try {
			executeUncaught();
		} finally {
			getLog().info(
					"ImageDownloadMojo LEAVE, completed in "
							+ chrono.elapsedTimeInMs() + " ms");
		}
	}

	private void executeUncaught() throws MojoExecutionException {
		checkInputParameters();
		File imageDir = getAndCreateIfNeededImageDirectory();
		for (String imageName : imageNames) {
			final String url = imageBaseurl + "/" + imageName;
			final File imageFile = new File(imageDir, imageName);
			if (imageFile.exists()) {
				writeLogMessageThatImageAlreadyExists(imageFile);
				continue;
			}
			HttpClient httpClient = new HttpClient(getLog());
			sendGetToImageServer(url, httpClient);
			byte[] imageBytes = httpClient.getBytes();
			writeImageToDisk(imageFile, imageBytes);
		}
	}

	protected void writeLogMessageThatImageAlreadyExists(File imageFile) {
		getLog().info(
				"Skipping download, image already exist: "
						+ imageFile.getAbsolutePath());
	}

	protected void checkInputParameters() throws MojoExecutionException {
		if (imageNames == null) {
			throw new MojoExecutionException("Parameter not set: imagenames");
		}
		if (imageBaseurl == null) {
			throw new MojoExecutionException("Parameter not set: imagebaseurl");
		}
		if (imageDirectory == null) {
			throw new MojoExecutionException(
					"Parameter not set: imageDirectory");
		}
		if (targetDirectory == null) {
			throw new MojoExecutionException(
					"Parameter not set: targetDirectory");
		}
	}

	protected File getAndCreateIfNeededImageDirectory()
			throws MojoExecutionException {
		getLog().info(
				"Project targetDirectory: " + targetDirectory.getAbsolutePath());
		File imageDir = new File(targetDirectory, imageDirectory);
		if (!imageDir.exists()) {
			getLog().info(
					"Directory to store images doesn't exist; creating it: "
							+ imageDir);
			if (!imageDir.mkdirs()) {
				throw new MojoExecutionException(
						"Failed to create directory to store images: "
								+ imageDir.getAbsolutePath());
			}
		}
		return imageDir;
	}

	protected void sendGetToImageServer(final String url, HttpClient httpClient)
			throws MojoExecutionException {
		getLog().info("Downloading: " + url);
		try {
			httpClient.doGet(url);
		} catch (HttpClientException e) {
			throw new MojoExecutionException("Downloading image failed, url: "
					+ url, e);
		}
		if (httpClient.getResponseCode() != 200) {
			throw new MojoExecutionException(
					"Downloading image failed, responseCode: "
							+ httpClient.getResponseCode()
							+ ",responseMessage: "
							+ httpClient.getReponseMessage() + ", url: " + url);
		}
	}

	protected void writeImageToDisk(File imageFile, byte[] imageBytes)
			throws MojoExecutionException {
		getLog().info("Writing image to disk: " + imageFile.getAbsolutePath());
		try {
			Files.write(imageFile.toPath(), imageBytes,
					StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to create image file: "
					+ imageFile.getAbsolutePath(), e);
		}
	}
	
	static class Chrono {
		private long now = System.currentTimeMillis();
		
		public long elapsedTimeInMs() {
			return (System.currentTimeMillis() - now);
		}
	}
}

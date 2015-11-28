package org.apache.maven.plugin.my;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "touch", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class MyMojo extends AbstractMojo {

	public final static String TOUCHED_FILE = "touch.txt";

	// Unit test uses a version of maven-plugin-testing-harness that doesn't
	// support annotations. It is also junit 3.x.
	// MyMojo is designed to work on Maven that does understand annotations.
	// So for test to work, the used variable names (e.g. outputDirectory) NEED
	// to match the values in the test pom.xml.
	// This is not what I want, but it is what works right now.
	// When used with maven 3.x (target), the annotations do work.
	@Parameter(property = "project.build.directory")
	private File outputDirectory;

	@Parameter(property = "basedir")
	private File basedir;

	@Parameter(property = "param1")
	private File param1;

	public void execute() throws MojoExecutionException {
		System.out.println("param1: " + param1);
		System.out.println("outputDirectory: "
				+ outputDirectory.getAbsolutePath());
		if (basedir != null) {
			System.out.println("basedir: " + basedir.getAbsolutePath());
		}

		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		}

		File touch = new File(f, TOUCHED_FILE);
		System.out.println("Creating file: " + touch.getAbsolutePath());
		FileWriter w = null;
		try {
			w = new FileWriter(touch);
			w.write("touch.txt");
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file " + touch, e);
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
}

package org.mahu.proto.junit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * This rule defined a temporary folder that is deleted at end of the etst case.
 */
public class TemporaryFolderRuleTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testUsingTempFolder() throws IOException {
		File createdFolder = folder.newFolder("newfolder");
		assertTrue(createdFolder.exists());
		//
		File createdFile = folder.newFile("myfilefile.txt");
		assertTrue(createdFile.exists());
	}
}
import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.my.MyMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class MyMojoTest extends AbstractMojoTestCase {
	/** {@inheritDoc} */
	protected void setUp() throws Exception {
		// required
		super.setUp();
	}

	/** {@inheritDoc} */
	protected void tearDown() throws Exception {
		// required
		super.tearDown();
	}

	public void testSomething() throws Exception {
		// the test pom.xml also contains the name of the mojo artifactId
		File pom = getTestFile("src/test/resources/unit/project-to-test/pom.xml");
		assertNotNull(pom);
		assertTrue(pom.exists());

		MyMojo myMojo = (MyMojo) lookupMojo("touch", pom);
		assertNotNull(myMojo);
		myMojo.execute();

		File testCassesDir = getResourceFile(MyMojoTest.class,".");
		File targetDir = testCassesDir.getParentFile();
		// relativePath in next line is defined in test pom.xml
		// I also observed that annotation parameters in myMojo do not get substituted.
		File targetDirTestProject = new File(targetDir, "test-harness/project-to-test");
		assertTrue((new File(targetDirTestProject, MyMojo.TOUCHED_FILE)).exists());
	}
	
	private static File getResourceFile(final Class<?> cls,
			final String resourceName) {
		URL url = getResourceUrl(cls, resourceName);
		File f = new File(url.getFile());
		return f;
	}
	
	private static URL getResourceUrl(final Class<?> cls,
			final String resourceName) {
		return cls.getClassLoader().getResource(resourceName);
	}
}
package org.mahu.proto.mavenplugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.mahu.proto.lib.Aclass;
import org.apache.maven.plugins.annotations.LifecyclePhase;

/**
 * @description generate the java source code
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GreetingMojo extends AbstractMojo {
	
	@Parameter(defaultValue = "${project}")
	private MavenProject project;
	
	@Parameter(defaultValue = "${project.build.directory}/generated-sources/greetings")	
    private File directoryGeneratedCode;	
	
	public void execute() throws MojoExecutionException {
		final Log log = getLog();
		
		new Aclass().hello(s -> log.info(s));

        if (isParametersAreNotNull(log)) {
    			
    		GenerateCode GenerateCode = new GenerateCode(directoryGeneratedCode, log);
    		GenerateCode.generateCode();
    		
            project.addCompileSourceRoot(directoryGeneratedCode.getAbsolutePath());    		
        } 
	}

	private boolean isParametersAreNotNull(final Log log) {
    	if (project == null) {
    		log.error("Project not set by Maven");
    		return false;
    	}
    	
    	if (project == null) {
    		log.error("Directory generate-sources not set by Maven");
    		return false;
    	}
    	
    	return true;
	}
}
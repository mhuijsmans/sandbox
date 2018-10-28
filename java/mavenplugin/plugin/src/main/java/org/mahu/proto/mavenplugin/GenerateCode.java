package org.mahu.proto.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.maven.plugin.logging.Log;

final class GenerateCode {
	
	private static final String CLASS_PACKAGE = "org\\mahu\\proto\\generated\\test";
	private static final String CLASS_NAME = "GeneratedClass";
	private final File generatedCodeDirectory;
	private final Log log;
	
	private String code = "package org.mahu.proto.generated.test;\n" + 
			"\n" + 
			"public class GeneratedClass {\n" + 
			"	\n" + 
			"	public String ping() {\n" + 
			"		return \"pong\";\n" + 
			"	}\n" + 
			"\n" + 
			"}";
	
	GenerateCode(final File generatedCodeDirectory, final Log log) {
		this.generatedCodeDirectory = generatedCodeDirectory;
		this.log = log;
		logInfo("Directory generated code="+generatedCodeDirectory.toString());		
	}
	
	public void generateCode() {
		Path generateCodeDir = generatedCodeDirectory.toPath();
		Path classDir = generateCodeDir.resolve(CLASS_PACKAGE);
		try {			
			logInfo("Creating dir="+classDir.toString());
			Files.createDirectories(classDir);
		} catch (IOException e) {
			logError("Failed to create dir, exception message="+e.getMessage());
			return;
		}
		
		Path classFileName = classDir.resolve(CLASS_NAME+".java");
		try {
			logInfo("Creating file="+classFileName.toString());			
			Files.write(classFileName, code.getBytes());
		} catch (IOException e) {
			logError("Failed to create file, exception message="+e.getMessage());
			return;
		}		
	}

	private void logError(String string) {
		log.error("##### "+string);		
	}

	private void logInfo(String string) {
		log.info("##### "+string);
	}
	
}

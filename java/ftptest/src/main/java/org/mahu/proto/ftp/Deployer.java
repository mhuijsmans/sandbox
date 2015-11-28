package org.mahu.proto.ftp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mahu.proto.ftp.IOUtil.FileUtilException;

import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;

public class Deployer {

	private final static Logger LOGGER = Logger.getLogger(Deployer.class
			.getName());
	
	private String ftpRundir;
	private String ftpHomedir;
	
	public Deployer(final String aFtpRundir, final String aFtpHomedir) {
		ftpRundir = aFtpRundir;
		ftpHomedir = aFtpHomedir;
	}

	@Subscribe
	public void recordCustomerChange(FtpUploadEvent event) {
		LOGGER.info("FtpUploadEvent: " + event);
		File runDir = new File(ftpRundir);
		File srcFile = new File(new File(ftpHomedir),
				event.getUpload());
		File dstFile = new File(runDir, event.getUpload());
		//
		LOGGER.info("rundir: " + runDir.getAbsolutePath());
		LOGGER.info("srcFile: " + srcFile.getAbsolutePath());
		LOGGER.info("dstFile: " + dstFile.getAbsolutePath());
		//
		try {
			createRunDir();
		} catch (FileUtilException e) {
			LOGGER.log(Level.SEVERE,
					"Failed to create dir: " + runDir.getAbsolutePath(), e);
			return;
		}
		//
		try {
			if (srcFile.getAbsolutePath().endsWith(".zip")) {
				IOUtil.unzip(srcFile, runDir);
			}
			Files.copy(srcFile, dstFile);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,
					"Failed to copy file: " + srcFile.getAbsolutePath()
							+ " to " + dstFile.getAbsolutePath(), e);
			return;
		}
		srcFile.delete();
	}

	private void createRunDir() throws FileUtilException {
		IOUtil.createDir(ftpRundir);
	}

}

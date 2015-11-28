package org.mahu.proto.ftp;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mahu.proto.ftp.IOUtil.FileUtilException;

public class FtpServiceTest {

	private final static Logger LOGGER = Logger.getLogger(FtpServiceTest.class
			.getName());
	
	
	@BeforeClass
	public static void init() {
		App.ftpHomedir = "target/ftptest/homedir";
		App.ftpRundir = "target/ftptest/rundir";
	}

	@Test
	public void uploadNormalFile() throws Exception {
		App app = new App();
		try {
			app.start();
			String file = "test_doc.txt";
			uploadFileToFtpServer("", file);
			assertTrue(IOUtil.fileExists(App.ftpRundir, file));
			//
		} finally {
			app.stop();
		}
	}

	@Test
	public void uploadZipFile() throws Exception {
		App app = new App();
		try {
			app.start();
			//
			String file = "testfolder.zip";
			uploadFileToFtpServer("",file);
			assertTrue(IOUtil.fileExists(App.ftpRundir, "testfolder/test_doc.txt"));
			//
		} finally {
			app.stop();
		}
	}

	private void uploadFileToFtpServer(final String path, final String fileName) throws FtpException,
			FileUtilException {
		//
		FTPClient ftp = new FTPClient();
		ftp.addProtocolCommandListener(new PrintCommandListener(
				new PrintWriter(System.out)));
		FTPClientConfig config = new FTPClientConfig();
		// config.setXXX(YYY); // change required options
		ftp.configure(config);
		//
		boolean error = false;
		InputStream is = null;
		try {
			int reply;
			ftp.connect(App.ftpServerHost, App.ftpServerPort);
			LOGGER.info("Connected to " + App.ftpServerHost+ ":"
					+ App.ftpServerPort + ".");
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new IOException("Exception in connecting to FTP Server");
			}
			LOGGER.info("FTP reply string: " + ftp.getReplyString());
			//
			boolean result = ftp.login(App.user, App.password);
			assertTrue("Failed to login", result);
			//
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			// transfer
			is = IOUtil.getInputStreamFromResource(this.getClass(), path +fileName);
			if (is != null) {
				LOGGER.info("sending data");
				result = ftp.storeFile(fileName, is);
				assertTrue("Failed to ftp", result);
			} else {
				LOGGER.info("Failed to open InputStream");
			}
			ftp.logout();
		} catch (IOException e) {
			error = true;
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
			IOUtil.closeStream(is);
		}
	}

}

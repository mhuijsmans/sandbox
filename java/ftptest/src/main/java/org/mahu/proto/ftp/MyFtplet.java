package org.mahu.proto.ftp;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

import com.google.common.eventbus.EventBus;
public class MyFtplet extends DefaultFtplet {

	private final static Logger LOGGER = Logger.getLogger(FtpService.class
			.getName());
	
	private EventBus eventBus;	
	
	@Override
	public FtpletResult onUploadStart(FtpSession session, FtpRequest request)
			throws FtpException, IOException {
		LOGGER.info("Request: " + request.getArgument());
		return null;
	}	

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request)
			throws FtpException, IOException {
		LOGGER.info("Request: " + request.getArgument());
		if (eventBus!=null) {
			eventBus.post(new FtpUploadEvent(request.getArgument()));
		}
		return null;
	}	
	
	public void setEventBus(final EventBus anEventBus) {
		eventBus = anEventBus;
	}
	
}
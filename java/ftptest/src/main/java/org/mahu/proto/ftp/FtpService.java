package org.mahu.proto.ftp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.UserFactory;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.mahu.proto.ftp.IOUtil.FileUtilException;

import com.google.common.eventbus.EventBus;

// home used component: http://mina.apache.org/ftpserver-project/index.html
public class FtpService {

	private final static Logger LOGGER = Logger.getLogger(FtpService.class
			.getName());

	private final FtpServer server;
	private final int port;

	private MyFtplet myFtplet = new MyFtplet();

	private String user;
	private String password;

	public FtpService(final int aPort, final String homedir, final String anUser, final String aPassword)
			throws FtpException, FileUtilException {
		checkArgument(aPort>0 && aPort <65535);
		checkNotNull(homedir);
		checkNotNull(anUser);
		checkNotNull(aPassword);
		//
		user = anUser;
		password = aPassword;
		port = aPort;
		FtpServerFactory serverFactory = new FtpServerFactory();
		//
		Map<String, Ftplet> ftplets = new HashMap<String, Ftplet>();
		ftplets.put("miaFtplet", myFtplet);
		serverFactory.setFtplets(ftplets);
		//
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(aPort);
		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		//
		// User Management
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		UserManager userManagement = userManagerFactory.createUserManager();
		//
		UserFactory userFact = new UserFactory();
		userFact.setName(user);
		userFact.setPassword(password);
		userFact.setHomeDirectory(homedir);
		//
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(new WritePermission());
		//
		userFact.setAuthorities(authorities);
		//
		User user = userFact.createUser();

		userManagement.save(user);
		//
		IOUtil.createDir(homedir);
		//
		serverFactory.setUserManager(userManagement);
		// create the server
		server = serverFactory.createServer();
	}

	public void setEventBus(final EventBus anEventBus) {
		myFtplet.setEventBus(anEventBus);
	}

	public void start() throws FtpException {
		LOGGER.info("Starting FTP Server on port: " + port);
		server.start();
	}

	public void stop() throws FtpException {
		LOGGER.info("Stopping FTP Server");
		server.stop();
	}

}

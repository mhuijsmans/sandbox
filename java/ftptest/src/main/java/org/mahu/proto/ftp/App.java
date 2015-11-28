package org.mahu.proto.ftp;

import org.apache.ftpserver.ftplet.FtpException;
import org.mahu.proto.ftp.IOUtil.FileUtilException;

import com.google.common.eventbus.EventBus;

public class App 
{	
	public static String ftpServerHost = "127.0.0.1";
	public static int ftpServerPort = 8765;
	
	public static String ftpRundir = "rundir";
	public static String ftpHomedir = "homedir";
	
	public static String user = "user";
	public static String password = "password";
	
	private FtpService ftpService;
	private EventBus eventBus;	
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
    
    public App() throws FtpException, FileUtilException {
    	eventBus = new EventBus();
		ftpService = new FtpService(ftpServerPort, ftpHomedir,user, password);
		//
		ftpService.setEventBus(eventBus);
		eventBus.register(new Deployer(ftpRundir, ftpHomedir));
    }
    
	public void start() throws Exception {
		ftpService.start();
	}
	
	public void stop() throws Exception {
		ftpService.stop();
	}	
    
}


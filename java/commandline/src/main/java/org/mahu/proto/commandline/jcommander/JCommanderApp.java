package org.mahu.proto.commandline.jcommander;

import com.beust.jcommander.JCommander;

public class JCommanderApp {
	
	public  Settings settings = new Settings();
	
    public void main( String[] args )
    {
        new JCommander(settings, args); // simple one-liner
    }	

}

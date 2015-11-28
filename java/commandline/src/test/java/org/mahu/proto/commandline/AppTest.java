package org.mahu.proto.commandline;

import org.junit.Assert;
import org.junit.Test;
import org.mahu.proto.commandline.jcommander.JCommanderApp;

public class AppTest 
{

    @Test
    public void testApp()
    {
    	String someUrl = "http://something";
    	JCommanderApp app = new JCommanderApp();
    	app.main(new String[] {"-url",someUrl, "-month","1"} );
        Assert.assertTrue( app.settings.getUrl().equals(someUrl) );
    }
}

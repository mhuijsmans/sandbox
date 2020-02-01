package org.mahu.proto.jersey.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Ignore;
import org.junit.Test;
import org.mahu.proto.jersey.proxy.service.Const;
import org.mahu.proto.jersey.proxy.service.IRestService;
import org.mahu.proto.jersey.proxy.service.ProxyFactory;
import org.mahu.proto.jersey.proxy.service.RestService;

public class JerseyProxyTestSave extends JerseyTest {
	
    @Override
    protected Application configure() {
        return new ResourceConfig(RestService.class);
    }
    
    @Test
    @Ignore
    public void givenGetHiGreeting_whenCorrectRequest_thenResponseIsOkAndContainsHi() {
        Response response = target("/test/info").request().get();
     
        assertEquals("Http Response should be 200: ", Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Http Content-Type should be: ", MediaType.TEXT_PLAIN, response.getHeaderString(HttpHeaders.CONTENT_TYPE));
     
        String content = response.readEntity(String.class);
        assertEquals("Content of ressponse is: ", Const.HELLO, content);
    }
    
    @Test
    @Ignore    
    public void createProxy() {
    	IRestService restService = ProxyFactory.createProxy();
    	
    	String result = restService.getInfo();
    	
    	assertEquals(Const.HELLO, result);
    }
    
    @Test
    @Ignore    
    public void createProxyViaClass_instanceOfReturnsTrue() throws InstantiationException, IllegalAccessException {
    	IRestService restService = ProxyFactory.createProxy();
    	
    	assertTrue(restService instanceof  IRestService);
    }
    
    // The following test shows that a Proxy does expose the methods that it proxies, but not the annotations associated with the interface.
    @Test
    @Ignore    
    public void createProxyViaClass_hasInterfaceMethods() throws InstantiationException, IllegalAccessException {
    	IRestService restService = ProxyFactory.createProxy();
    	
    	Method[] methods = restService.getClass().getDeclaredMethods();
    	System.out.println("### Declared methods - start");
    	for(Method method: methods) {
    		System.out.println(method.toGenericString());
    		for(Annotation annotation : method.getAnnotations()) {
    			System.out.println("  " + annotation.toString());
    		}
    	}
    	System.out.println("### Declared methods - end");    	
    }    
    
    // @Test
    @Ignore   
    public void createProxyViaClass() throws InstantiationException, IllegalAccessException {
    	IRestService restService = ProxyFactory.createProxy().getClass().newInstance();
    	
    	String result = restService.getInfo();
    	
    	assertEquals(Const.HELLO, result);
    }

}

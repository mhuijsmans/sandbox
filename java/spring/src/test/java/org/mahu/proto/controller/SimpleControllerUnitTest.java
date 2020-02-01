package org.mahu.proto.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mahu.proto.service.ISimpleService;
import org.mahu.proto.service.ISimpleServiceRequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(SimpleController.class)
// @WebMvcTest also auto-configures MockMvc which offers a powerful way of easy testing MVC controllers without starting a full HTTP server.
// But this test takes > 3.5 sec on laptop. So that is not unit test (which should be milli-seconds).
public class SimpleControllerUnitTest {
	
	public static final String SIMPLE_CONTROLLER_TEXT = "simple-controller-text";
	public static final String SIMPLE_CONTROLLER_XML = "simple-controller-xml";	
	
    @Autowired
    private MockMvc mvc;
    
    // Because this is UnitTest and Controller has a dependency, I have to provide a Mock. 
    @MockBean
    ISimpleService simpleService;
    
    @MockBean
    ISimpleServiceRequestScope simpleServiceRequestScope;     
    
	@Test
	public void getXml() throws Exception {
		when(simpleService.getValueXml()).thenReturn(SIMPLE_CONTROLLER_XML);
		
		mvc.perform(get("/rest/simple-controller/xml")
			      .contentType(MediaType.APPLICATION_XML))
			      .andExpect(status().isOk())
			      .andExpect(content().string(SIMPLE_CONTROLLER_XML));
	}
	
	@Test
	public void getText() throws Exception {
		when(simpleService.getValueText()).thenReturn(SIMPLE_CONTROLLER_TEXT);		
		
		mvc.perform(get("/rest/simple-controller/text")
			      .contentType(MediaType.TEXT_PLAIN))
			      .andExpect(status().isOk())
			      .andExpect(content().string(SIMPLE_CONTROLLER_TEXT));			      
	}	

}

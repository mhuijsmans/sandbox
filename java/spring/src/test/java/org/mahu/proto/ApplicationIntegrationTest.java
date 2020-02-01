package org.mahu.proto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureMockMvc
public class ApplicationIntegrationTest {

	private static final String EMPTY_STRING = "";
	private static final String SETTING = "SettingValue";
	@Autowired
	private MockMvc mvc;

	// The SimpleController and SimpleService are created once, i.e. singletons.
	@Test
	public void test_SimpleServiceIsASingleton() throws Exception {
		MvcResult result1 = mvc.perform(get("/rest/simple-controller/xml").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk()).andReturn();
		final String firstResponse = result1.getResponse().getContentAsString();

		ResultActions result2 = mvc.perform(get("/rest/simple-controller/xml").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
		final String secondResponse = result2.andReturn().getResponse().getContentAsString();

		assertEquals(firstResponse, secondResponse);
	}

	// The SimpleController is created once, whereas SimpleServiceRequestScoped is
	// created for every call
	@Test
	public void test_SimpleServiceIsRequestScope() throws Exception {
		MvcResult result1 = mvc
				.perform(get("/rest/simple-controller-request-scope/xml").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk()).andReturn();
		final String firstResponse = result1.getResponse().getContentAsString();

		ResultActions result2 = mvc
				.perform(get("/rest/simple-controller-request-scope/xml").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
		final String secondResponse = result2.andReturn().getResponse().getContentAsString();

		assertNotEquals(firstResponse, secondResponse);
	}

	// The ConfigController is created once. It uses beans, which are created once.
	@Test
	public void test_configurationBeans() throws Exception {
		MvcResult result1 = mvc.perform(get("/rest/config").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk()).andReturn();
		final String firstResponse = result1.getResponse().getContentAsString();

		ResultActions result2 = mvc.perform(get("/rest/config").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
		final String secondResponse = result2.andReturn().getResponse().getContentAsString();

		assertEquals(firstResponse, secondResponse);
	}

	// The ConfigController is created once. It uses request-scoped beans.
	// The assert sometimes indicated equals. I do not understand how that is
	// possible.
	@Test
	public void test_configurationBeansRequestScoped() throws Exception {
		MvcResult result1 = mvc.perform(get("/rest/config-request-scoped").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk()).andReturn();
		final String firstResponse = result1.getResponse().getContentAsString();

		ResultActions result2 = mvc.perform(get("/rest/config-request-scoped").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isOk());
		final String secondResponse = result2.andReturn().getResponse().getContentAsString();

		assertNotEquals(firstResponse, secondResponse);
	}

	// The ConfigController is created once. Request reloading of data.
	@Test
	public void test_reloadConfigurationData() throws Exception {
		mvc.perform(post("/rest/config-reload")).andExpect(status().isOk());
	}

	// The ConfigController is created once. The external configuration settings are 
	@Test
	public void test_externalSettings() throws Exception {
		mvc.perform(delete("/rest/external-settings").contentType(MediaType.APPLICATION_XML))
				.andExpect(status().isNoContent());

		mvc.perform(get("/rest/external-settings").contentType(MediaType.APPLICATION_XML)).andExpect(status().isOk())
				.andExpect(content().string(EMPTY_STRING));

		mvc.perform(post("/rest/external-settings").content(SETTING).contentType(MediaType.TEXT_PLAIN))
				.andExpect(status().isOk()).andExpect(content().string(SETTING));

		mvc.perform(get("/rest/external-settings").contentType(MediaType.APPLICATION_XML)).andExpect(status().isOk())
				.andExpect(content().string(SETTING));
	}
	
	// This test is incomplete. It intended to play with the start-up logic
	@Test
	public void test_startUpLogic() throws Exception {
		mvc.perform(post("/rest/startup-value").content(SETTING).contentType(MediaType.TEXT_PLAIN))
				.andExpect(status().isOk());
	}	

	// On laptop, next test took 19 sec, which is about 190 NS / request (assuming
	// it is really over a socket).
	// 190 NS is fast. I need to get confirmation with external tool.
	@Test
	@Ignore
	public void test_enduranceRun() throws Exception {
		for (int i = 0; i < 1000 * 100; i++) {
			test_configurationBeansRequestScoped();
		}
	}

}

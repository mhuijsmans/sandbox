package org.mahu.proto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

// @ActiveProfiles("test")      // If profiles are used
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
	
    @Test
    public void contextLoads() {
    	// ask Arjen what the goal of this test case is.
    }

    @Test
    public void configureShouldReturnSpringApplicationBuilder() {
    	Application app = new Application();
        SpringApplicationBuilder builder = app.configure(new SpringApplicationBuilder());
        assertThat(builder).isNotNull();
    }	

}

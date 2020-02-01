package org.mahu.proto;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(
		scanBasePackages = {"org.mahu.proto.config", "org.mahu.proto.controller", 
				"org.mahu.proto.service", "org.mahu.proto.startup", "org.mahu.proto.stm"}
		)
// Note: https://dzone.com/articles/spring-boot-with-external-tomcat defines that
// o.a. SpringBootServletInitializer shall be added to make it deployable on Tomcat
public class Application extends SpringBootServletInitializer {
    public static void main(String[] args) {
    	// next line what what Spring demo-appl provided
        // SpringApplication.run(Application.class, args);
    	
    	// Next line is what gateway uses (and more). Less is more, I like above better.
    	SpringApplicationBuilder SpringApplicationBuilder = new SpringApplicationBuilder();
    	Application app = new Application();
    	app.configure(SpringApplicationBuilder).run(args);
    }
    
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }    
}

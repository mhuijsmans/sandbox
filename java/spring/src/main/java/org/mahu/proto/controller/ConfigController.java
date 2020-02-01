package org.mahu.proto.controller;

import java.util.Optional;

import org.mahu.proto.config.IAppConfiguration;
import org.mahu.proto.config.IAppSettings;
import org.mahu.proto.config.IAppSettingsRequestScoped;
import org.mahu.proto.config.IExternalSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class ConfigController {
	
	private static final String UNKNOWN = "";
	
	private final IAppSettings appSettings;
	private final IAppSettingsRequestScoped appSettingsRequestScoped;
	private final IAppConfiguration appConfiguration;
	private final IExternalSettings externalSettings;
	
	@Autowired
	ConfigController(final IAppSettings appSettings, final IAppSettingsRequestScoped appSettingsRequestScoped, 
			final IAppConfiguration appConfiguration, final IExternalSettings externalSettings) {
		this.appSettings = appSettings;
		this.appSettingsRequestScoped = appSettingsRequestScoped;
		this.appConfiguration = appConfiguration;
		this.externalSettings = externalSettings;
	}
	
	@GetMapping(value="/config", produces = "application/xml")
	public String getValue() {
		return appSettings.getInfo();
	}
	
	@GetMapping(value="/config-request-scoped", produces = "application/xml")
	public String getValueRequestScoped() {
		return appSettingsRequestScoped.getInfo();
	}
	
	@PostMapping(value="/config-reload")
	public void reloadConfigData() {
		appConfiguration.reloadData();
	}
	
	@DeleteMapping(value="/external-settings")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteExecutionSettings() {
		appConfiguration.setSettings(UNKNOWN);
	}	
	
	@GetMapping(value="/external-settings")
	public String getExecutionSettings() {
		return readSettinsValueFromRequestScopedBean();
	}
	
	@PostMapping(value="/external-settings", consumes = "text/plain", produces = "text/plain")
	public String useExecutionSettings(@RequestBody String text) {
		appConfiguration.setSettings(text);
		
		return readSettinsValueFromRequestScopedBean();
	}	
	
	// The next function reads the settings value from the requestScopedBean.
	// Spring performs lazy initialization of the bean, i.e. 
	private String readSettinsValueFromRequestScopedBean() {
		Optional<String> value = externalSettings.getText();
		return value.isPresent() ? value.get() : UNKNOWN;
	}		

}

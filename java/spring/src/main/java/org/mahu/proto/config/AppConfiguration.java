package org.mahu.proto.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
// Note: can not be final, because Spring will complain.
public class AppConfiguration implements IAppConfiguration {

	private final Object lock = new Object();
	// Next parameter mimics the case where external settings are loaded
	// asynchronously.
	private Optional<String> externalSettingsValue = Optional.empty();

	@Bean
	IAppSettings getAppSettings() {
		return new AppSettings();
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	IAppSettingsRequestScoped getAppSettingsRequestScoped() {
		return new AppSettingsRequestScoped();
	}

	@Override
	public void reloadData() {
		// Implement in threadsafe way
	}

	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	IExternalSettings getExternalSettings() {
		synchronized (lock) {
			return new ExternalSettings(externalSettingsValue);
		}
	}

	@Override
	public void setSettings(String text) {
		synchronized (lock) {
			externalSettingsValue = Optional.ofNullable(text);
		}
	}

}

package org.mahu.proto.config;

import org.mahu.proto.util.Utils;

final class AppSettingsRequestScoped implements IAppSettingsRequestScoped {
	
	private final String APP_SETTINGS = "app-settings."+Utils.nextId();

	@Override
	public String getInfo() {
		return APP_SETTINGS;
	}

}

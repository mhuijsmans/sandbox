package org.mahu.proto.config;

import java.util.Optional;

final class ExternalSettings implements IExternalSettings {
	
	private final Optional<String> text;
	
	ExternalSettings(final Optional<String> text) {
		this.text = text;
	}

	@Override
	public Optional<String> getText() {
		return text;
	}

}

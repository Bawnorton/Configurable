package com.bawnorton.configurable.io;

public class IllegalConfigException extends RuntimeException {
	public IllegalConfigException(String configName, String message) {
		super("Illegal configuration for '%s': %s".formatted(configName, message));
	}
}

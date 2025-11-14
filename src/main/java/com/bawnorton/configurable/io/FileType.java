package com.bawnorton.configurable.io;

public enum FileType {
	JSON("json5"),
	TOML("toml");

	private final String extension;

	FileType(String extension) {
		this.extension = extension;
	}

	public String getExtension() {
		return extension;
	}
}

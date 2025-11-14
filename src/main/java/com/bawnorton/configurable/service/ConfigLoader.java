package com.bawnorton.configurable.service;

import com.bawnorton.configurable.io.FileType;
import com.bawnorton.configurable.io.SaveLoader;
import com.bawnorton.configurable.reference.FieldReference;

import java.util.List;

public interface ConfigLoader {
	String getName();

	FileType getFileType();

	void load(SaveLoader saveLoader);

	void save(SaveLoader saveLoader);

	List<FieldReference<?>> getFields();
}

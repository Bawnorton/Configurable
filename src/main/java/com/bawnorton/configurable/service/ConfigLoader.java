package com.bawnorton.configurable.service;

import com.bawnorton.configurable.io.FileType;

public interface ConfigLoader {
    String getName();
    void load();
    void save();
    FileType getFileType();
}

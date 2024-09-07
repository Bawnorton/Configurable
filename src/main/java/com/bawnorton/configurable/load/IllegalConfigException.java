package com.bawnorton.configurable.load;

public class IllegalConfigException extends RuntimeException {
    public IllegalConfigException(String message) {
        super(message);
    }

    public IllegalConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}

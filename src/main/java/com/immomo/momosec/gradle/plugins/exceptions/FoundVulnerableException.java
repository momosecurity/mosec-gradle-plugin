package com.immomo.momosec.gradle.plugins.exceptions;

public class FoundVulnerableException extends RuntimeException {

    public FoundVulnerableException(String message) {
        super(message);
    }
}

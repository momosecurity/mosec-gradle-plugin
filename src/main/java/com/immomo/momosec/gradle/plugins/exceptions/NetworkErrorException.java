package com.immomo.momosec.gradle.plugins.exceptions;

public class NetworkErrorException extends RuntimeException {

    public NetworkErrorException(String message) {
        super(message);
    }
}

package com.nweiser94.springbootfritzdyndns.exception;

/**
 * Custom exception that is thrown in case the communication with the netcup api fails.
 */
public class APICommunicationException extends RuntimeException{

    public APICommunicationException(final String message) {
        super(message);
    }

    public APICommunicationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

package com.afollestad.bridge;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class RequestException extends Exception {

    protected RequestException(Exception wrap) {
        super(wrap);
    }

    protected RequestException(Request cancelledRequest) {
        super(String.format("%s request to %s was cancelled.",
                cancelledRequest.method().name(), cancelledRequest.url()));
    }
}

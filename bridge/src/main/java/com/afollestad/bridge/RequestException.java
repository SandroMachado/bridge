package com.afollestad.bridge;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class RequestException extends Exception {

    protected RequestException(Request request, Exception wrap) {
        super(String.format("%s %s error: %s", request.method().name(), request.url(), wrap.getMessage()), wrap);
    }

    protected RequestException(Request cancelledRequest) {
        super(String.format("%s request to %s was cancelled.",
                cancelledRequest.method().name(), cancelledRequest.url()));
    }
}

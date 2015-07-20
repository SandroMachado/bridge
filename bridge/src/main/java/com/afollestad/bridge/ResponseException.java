package com.afollestad.bridge;

import android.support.annotation.NonNull;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ResponseException extends Exception {

    private Response mResponse;

    protected ResponseException(@NonNull String message) {
        super(message);
    }

    protected ResponseException(@NonNull Response response) {
        super(response.toString());
        mResponse = response;
    }

    protected ResponseException(@NonNull Response response, @NonNull Exception e) {
        super(String.format("%s: %s", response.toString(), e.getLocalizedMessage()), e);
        mResponse = response;
    }

    public Response getResponse() {
        return mResponse;
    }
}
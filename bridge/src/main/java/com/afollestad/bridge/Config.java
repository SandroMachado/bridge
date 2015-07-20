package com.afollestad.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Config {

    protected Config() {
        mDefaultHeaders = new HashMap<>();
        mDefaultHeaders.put("User-Agent", "afollestad/Bridge");
        mDefaultHeaders.put("Content-Type", "text/plain");
        BUFFER_SIZE = 1024 * 4;
    }

    protected String mHost;
    protected Map<String, Object> mDefaultHeaders;
    protected int BUFFER_SIZE;

    public Config host(@Nullable String host) {
        mHost = host;
        return this;
    }

    public Config defaultHeader(@NonNull String name, @Nullable Object value) {
        if (value == null)
            mDefaultHeaders.remove(name);
        else mDefaultHeaders.put(name, value);
        return this;
    }

    public Config bufferSize(int size) {
        if (size <= 0)
            throw new IllegalArgumentException("The buffer size must be greater than 0.");
        BUFFER_SIZE = size;
        return this;
    }

    protected void destroy() {
        mHost = null;
        mDefaultHeaders.clear();
        mDefaultHeaders = null;
        BUFFER_SIZE = 0;
    }
}

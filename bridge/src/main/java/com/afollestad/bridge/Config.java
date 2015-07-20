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
    }

    protected String mHost;
    protected Map<String, Object> mDefaultHeaders;
    protected int mConnectTimeout = 10000;
    protected int mReadTimeout = 15000;
    protected int mBufferSize = 1024 * 4;

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

    public Config connectTimeout(int timeout) {
        if (timeout <= 0)
            throw new IllegalArgumentException("Connect timeout must be greater than 0.");
        mConnectTimeout = timeout;
        return this;
    }

    public Config readTimeout(int timeout) {
        if (timeout <= 0)
            throw new IllegalArgumentException("Read timeout must be greater than 0.");
        mReadTimeout = timeout;
        return this;
    }

    public Config bufferSize(int size) {
        if (size <= 0)
            throw new IllegalArgumentException("The buffer size must be greater than 0.");
        mBufferSize = size;
        return this;
    }

    protected void destroy() {
        mHost = null;
        mDefaultHeaders.clear();
        mDefaultHeaders = null;
        mBufferSize = 0;
    }
}

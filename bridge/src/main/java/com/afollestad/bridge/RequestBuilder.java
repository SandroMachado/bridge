package com.afollestad.bridge;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class RequestBuilder implements AsResultsExceptions {

    protected final Bridge mContext;
    protected final String mUrl;
    protected final Method mMethod;
    protected Map<String, Object> mHeaders;
    protected byte[] mBody;
    protected Pipe mPipe;
    protected int mConnectTimeout;
    protected int mReadTimeout;
    protected int mBufferSize;
    private Request mRequest;
    protected boolean mCancellable = true;
    protected Object mTag;

    protected RequestBuilder(String url, Method method, Bridge context) {
        mContext = context;
        if (!url.startsWith("http") && Bridge.client().config().mHost != null)
            url = Bridge.client().config().mHost + url;
        Log.d(this, "%s %s", method.name(), url);
        mUrl = url;
        mMethod = method;

        Config cf = Bridge.client().config();
        mHeaders = cf.mDefaultHeaders;
        mConnectTimeout = cf.mConnectTimeout;
        mReadTimeout = cf.mReadTimeout;
        mBufferSize = cf.mBufferSize;
    }

    public RequestBuilder header(@NonNull String name, @NonNull Object value) {
        mHeaders.put(name, value);
        return this;
    }

    public RequestBuilder connectTimeout(int timeout) {
        if (timeout <= 0)
            throw new IllegalArgumentException("Connect timeout must be greater than 0.");
        mConnectTimeout = timeout;
        return this;
    }

    public RequestBuilder readTimeout(int timeout) {
        if (timeout <= 0)
            throw new IllegalArgumentException("Read timeout must be greater than 0.");
        mReadTimeout = timeout;
        return this;
    }

    public RequestBuilder bufferSize(int size) {
        if (size <= 0)
            throw new IllegalArgumentException("Buffer size must be greater than 0.");
        mBufferSize = size;
        return this;
    }

    public RequestBuilder body(@Nullable byte[] rawBody) {
        if (rawBody == null) {
            mBody = null;
            return this;
        }
        mBody = rawBody;
        return this;
    }

    public RequestBuilder body(@Nullable String textBody) {
        Log.d(this, "Body: %s", textBody);
        if (textBody == null) {
            mBody = null;
            return this;
        }
        header("Content-Type", "text/plain");
        try {
            mBody = textBody.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
        return this;
    }

    public RequestBuilder body(@Nullable JSONObject json) {
        if (json == null) {
            mBody = null;
            return this;
        }
        body(json.toString());
        header("Content-Type", "application/json");
        return this;
    }

    public RequestBuilder body(@Nullable JSONArray json) {
        if (json == null) {
            mBody = null;
            return this;
        }
        body(json.toString());
        header("Content-Type", "application/json");
        return this;
    }

    public RequestBuilder body(@Nullable Form form) {
        if (form == null) {
            mBody = null;
            return this;
        }
        body(form.toString());
        header("Content-Type", "application/x-www-form-urlencoded");
        return this;
    }

    public RequestBuilder body(@Nullable MultipartForm form) throws Exception {
        if (form == null) {
            mBody = null;
            return this;
        }
        body(form.data());
        header("Content-Type", String.format("multipart/form-data; boundary=%s", form.BOUNDARY));
        return this;
    }

    public RequestBuilder body(@NonNull Pipe pipe) {
        mPipe = pipe;
        header("Content-Type", pipe.contentType());
        return this;
    }

    public RequestBuilder body(@NonNull File file) {
        return body(Pipe.forFile(file));
    }

    public RequestBuilder cancellable(boolean cancelable) {
        mCancellable = cancelable;
        return this;
    }

    public RequestBuilder tag(@Nullable Object tag) {
        mTag = tag;
        return this;
    }

    public Request request() throws RequestException {
        return new Request(this).makeRequest();
    }

    public Request request(Callback callback) {
        mRequest = new Request(this);
        if (mContext.pushCallback(mRequest, callback)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mRequest.makeRequest();
                        if (mRequest.mCancelCallbackFired) return;
                        mContext.fireCallbacks(mRequest, mRequest.response(), null);
                    } catch (final RequestException e) {
                        if (mRequest.mCancelCallbackFired) return;
                        mContext.fireCallbacks(mRequest, null, e);
                    }
                }
            }).start();
        }
        return mRequest;
    }

    // Shortcut methods

    public Response response() throws RequestException {
        return request().response();
    }

    public byte[] asBytes() throws Exception {
        return response().throwIfNotSuccess().asBytes();
    }

    public String asString() throws Exception {
        return response().throwIfNotSuccess().asString();
    }

    @Override
    public Spanned asHtml() throws Exception {
        return response().throwIfNotSuccess().asHtml();
    }

    @Override
    public Bitmap asBitmap() throws Exception {
        return response().throwIfNotSuccess().asBitmap();
    }

    public JSONObject asJsonObject() throws Exception {
        return response().throwIfNotSuccess().asJsonObject();
    }

    public JSONArray asJsonArray() throws Exception {
        return response().throwIfNotSuccess().asJsonArray();
    }

    public void asFile(File destination) throws Exception {
        response().throwIfNotSuccess().asFile(destination);
    }
}
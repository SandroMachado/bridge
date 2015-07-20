package com.afollestad.bridge;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Response implements AsResults {

    private final byte[] mData;
    private final int mCode;
    private final String mMessage;
    private Bitmap mBitmapCache;

    protected Response(byte[] data, int code, String message) throws IOException {
        mData = data;
        mCode = code;
        mMessage = message;
    }

    public int code() {
        return mCode;
    }

    public String phrase() {
        return mMessage;
    }

    public boolean isSuccess() {
        return mCode == HttpURLConnection.HTTP_OK;
    }

    public Response throwIfNotSuccess() throws ResponseException {
        if (!isSuccess()) throw new ResponseException(this);
        return this;
    }

    public byte[] asBytes() {
        return mData;
    }

    public String asString() {
        try {
            final byte[] bytes = asBytes();
            if (bytes == null || bytes.length == 0) return null;
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spanned asHtml() {
        return Html.fromHtml(asString());
    }

    @Override
    public Bitmap asBitmap() {
        if (mBitmapCache == null) {
            final InputStream is = new ByteArrayInputStream(asBytes());
            mBitmapCache = BitmapFactory.decodeStream(is);
            Util.closeQuietly(is);
        }
        return mBitmapCache;
    }

    public JSONObject asJsonObject() throws JSONException {
        return new JSONObject(asString());
    }

    public JSONArray asJsonArray() throws JSONException {
        return new JSONArray(asString());
    }

    public void asFile(File destination) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(destination);
            os.write(asBytes());
            os.flush();
        } finally {
            Util.closeQuietly(os);
        }
    }

    @Override
    public String toString() {
        return String.format("%d %s, %d bytes", mCode, mMessage, mData != null ? mData.length : 0);
    }
}
package com.afollestad.bridge;

import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class MultipartForm {

    protected final String BOUNDARY;
    private final byte[] LINE_FEED = "\r\n".getBytes();
    private ByteArrayOutputStream mBos;

    public MultipartForm() {
        BOUNDARY = String.format("------%d------", System.currentTimeMillis());
        mBos = new ByteArrayOutputStream();
    }

    public MultipartForm add(@NonNull String fieldName, @NonNull final File file) throws IOException {
        add(fieldName, file.getName(), Pipe.forFile(file));
        return this;
    }

    public MultipartForm add(@NonNull String fieldName, @NonNull String fileName, @NonNull Pipe pipe) throws IOException {
        if (mBos == null)
            throw new IllegalStateException("This MultipartForm is already consumed.");
        mBos.write(("--" + BOUNDARY).getBytes());
        mBos.write(LINE_FEED);
        mBos.write(("Content-Disposition: form-data; name=\"" + fieldName
                + "\"; filename=\"" + fileName + "\"").getBytes());
        mBos.write(LINE_FEED);
        mBos.write(("Content-Type: " + pipe.contentType()).getBytes());
        mBos.write(LINE_FEED);
        mBos.write("Content-Transfer-Encoding: binary".getBytes());
        mBos.write(LINE_FEED);
        mBos.write(LINE_FEED);
        pipe.writeTo(mBos);
        mBos.write(LINE_FEED);
        return this;
    }

    public MultipartForm add(@NonNull String fieldName, @NonNull Object value) {
        if (mBos == null)
            throw new IllegalStateException("This MultipartForm is already consumed.");
        try {
            mBos.write(("--" + BOUNDARY).getBytes());
            mBos.write(LINE_FEED);
            mBos.write(String.format("Content-Disposition: form-data; name=\"%s\"", fieldName).getBytes());
            mBos.write(LINE_FEED);
            mBos.write("Content-Type: text/plain; charset=utf-8".getBytes());
            mBos.write(LINE_FEED);
            mBos.write(LINE_FEED);
            mBos.write((value + "").getBytes());
            mBos.write(LINE_FEED);
        } catch (Exception e) {
            // Shouldn't happen
            throw new RuntimeException(e);
        }
        return this;
    }

    protected byte[] data() {
        try {
            mBos.write(LINE_FEED);
            mBos.write(String.format("--%s--", BOUNDARY).getBytes());
            mBos.write(LINE_FEED);
        } catch (Exception e) {
            // Shouldn't happen
            throw new RuntimeException(e);
        }
        final byte[] data = mBos.toByteArray();
        Util.closeQuietly(mBos);
        mBos = null;
        return data;
    }
}

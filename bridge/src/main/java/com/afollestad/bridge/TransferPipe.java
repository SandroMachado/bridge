package com.afollestad.bridge;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Aidan Follestad (afollestad)
 */
class TransferPipe extends Pipe {

    private final InputStream mIs;
    private final String mContentType;

    public TransferPipe(@NonNull InputStream is, @NonNull String contentType) {
        mIs = is;
        mContentType = contentType;
    }

    @Override
    public void writeTo(@NonNull OutputStream os) throws IOException {
        try {
            byte[] buffer = new byte[Bridge.config().BUFFER_SIZE];
            int read;
            while ((read = mIs.read(buffer)) != -1)
                os.write(buffer, 0, read);
        } finally {
            Util.closeQuietly(mIs);
        }
    }

    @Override
    @NonNull
    public String contentType() {
        return mContentType;
    }
}

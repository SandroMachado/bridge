package com.afollestad.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Util {

    public static void closeQuietly(@Nullable Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable ignored) {
            }
        }
    }

    public static byte[] readEntireStream(@NonNull InputStream is) throws IOException {
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[Bridge.client().config().mBufferSize];
            int read;
            while ((read = is.read(buffer)) != -1)
                os.write(buffer, 0, read);
            os.flush();
            return os.toByteArray();
        } finally {
            Util.closeQuietly(os);
            Util.closeQuietly(is);
        }
    }

    private Util() {
    }
}

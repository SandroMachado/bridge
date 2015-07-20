package com.afollestad.bridge;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class Pipe {

    public Pipe() {
    }

    public abstract void writeTo(@NonNull OutputStream os) throws IOException;

    @NonNull
    public abstract String contentType();

    public static Pipe forUri(@NonNull Context context, @NonNull Uri uri) {
        return new UriPipe(context, uri);
    }

    public static Pipe forFile(@NonNull File file) {
        return new UriPipe(null, Uri.fromFile(file));
    }
}

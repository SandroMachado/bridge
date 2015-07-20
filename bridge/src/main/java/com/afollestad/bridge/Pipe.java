package com.afollestad.bridge;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Creates a Pipe that reads a Uri (file:// or content://) into the Pipe.
     */
    public static Pipe forUri(@NonNull Context context, @NonNull Uri uri) {
        return new UriPipe(context, uri);
    }

    /**
     * Creates a Pipe that reads the contents of a File into the Pipe.
     */
    public static Pipe forFile(@NonNull File file) {
        return new UriPipe(null, Uri.fromFile(file));
    }

    /**
     * Creates a Pipe that reads an InputStream and transfers the content into the Pipe.
     */
    public static Pipe forStream(@NonNull InputStream is, @NonNull String contentType) {
        return new TransferPipe(is, contentType);
    }
}
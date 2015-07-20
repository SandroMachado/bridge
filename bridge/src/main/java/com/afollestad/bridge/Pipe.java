package com.afollestad.bridge;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class Pipe {

    public Pipe() {
    }

    public abstract void writeTo(OutputStream os) throws IOException;

    public abstract String contentType();

    public static Pipe forUri(Context context, Uri uri) {
        return new UriPipe(context, uri);
    }

    public static Pipe forFile(File file) {
        return new UriPipe(null, Uri.fromFile(file));
    }
}

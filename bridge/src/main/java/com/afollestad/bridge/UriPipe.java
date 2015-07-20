package com.afollestad.bridge;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

/**
 * @author Aidan Follestad (afollestad)
 */
class UriPipe extends Pipe {

    private final Context mContext;
    private final Uri mUri;

    public UriPipe(Context context, Uri uri) {
        mContext = context;
        mUri = uri;
    }

    @Override
    public void writeTo(@NonNull OutputStream os) throws IOException {
        InputStream is = null;
        try {
            if (mUri.getScheme() == null || mUri.getScheme().equalsIgnoreCase("file"))
                is = new FileInputStream(mUri.getPath());
            else is = mContext.getContentResolver().openInputStream(mUri);
            byte[] buffer = new byte[Bridge.client().config().mBufferSize];
            int read;
            while ((read = is.read(buffer)) != -1)
                os.write(buffer, 0, read);
        } finally {
            Util.closeQuietly(is);
        }
    }

    @Override
    @NonNull
    public String contentType() {
        String type;
        if (mUri.getScheme() == null || mUri.getScheme().equalsIgnoreCase("file")) {
            type = URLConnection.guessContentTypeFromName(new File(mUri.getPath()).getName());
        } else {
            type = mContext.getContentResolver().getType(mUri);
        }
        if (type == null || type.trim().isEmpty())
            type = "application/octet-stream";
        return type;
    }
}

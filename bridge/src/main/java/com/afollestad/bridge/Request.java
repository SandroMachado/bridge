package com.afollestad.bridge;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Request {

    private final RequestBuilder mBuilder;
    private boolean isCancelled;
    private Response mResponse;

    protected Request(RequestBuilder builder) {
        mBuilder = builder;
    }

    protected RequestBuilder builder() {
        return mBuilder;
    }

    protected Request makeRequest() throws RequestException {
        try {
            URL url = new URL(mBuilder.mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setReadTimeout(mBuilder.mReadTimeout);
                conn.setConnectTimeout(mBuilder.mConnectTimeout);
                conn.setRequestMethod(mBuilder.mMethod.name());
                conn.setInstanceFollowRedirects(true);

                if (mBuilder.mHeaders != null && mBuilder.mHeaders.size() > 0) {
                    for (final String key : mBuilder.mHeaders.keySet()) {
                        final Object value = mBuilder.mHeaders.get(key);
                        conn.setRequestProperty(key, value + "");
                    }
                }
                conn.setDoInput(true);

                checkCancelled();
                if (mBuilder.mPipe != null || mBuilder.mBody != null) {
                    conn.setDoOutput(true);
                    OutputStream os = null;
                    try {
                        os = conn.getOutputStream();
                        if (mBuilder.mPipe != null) {
                            mBuilder.mPipe.writeTo(os);
                        } else {
                            os.write(mBuilder.mBody);
                        }
                        os.flush();
                    } finally {
                        Util.closeQuietly(os);
                    }
                }

                checkCancelled();
                byte[] data = null;
                InputStream is = null;
                ByteArrayOutputStream bos = null;
                try {
                    is = conn.getInputStream();
                    bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[Bridge.config().BUFFER_SIZE];
                    int read;
                    int totalRead = 0;
                    int totalAvailable;
                    if (conn.getHeaderField("Content-Length") != null)
                        totalAvailable = Integer.parseInt(conn.getHeaderField("Content-Length"));
                    else totalAvailable = is.available();
                    if (totalAvailable != 0)
                        mBuilder.mContext.fireProgress(Request.this, 0, totalAvailable);
                    while ((read = is.read(buf)) != -1) {
                        checkCancelled();
                        bos.write(buf, 0, read);
                        totalRead += read;
                        if (totalAvailable != 0)
                            mBuilder.mContext.fireProgress(Request.this, totalRead, totalAvailable);
                    }
                    if (totalAvailable == 0)
                        mBuilder.mContext.fireProgress(Request.this, 100, 100);
                    data = bos.toByteArray();
                } finally {
                    Util.closeQuietly(is);
                    Util.closeQuietly(bos);
                }

                checkCancelled();
                mResponse = new Response(data, conn.getResponseCode(), conn.getResponseMessage());
            } catch (FileNotFoundException fnf) {
                mResponse = new Response(null, conn.getResponseCode(), conn.getResponseMessage());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            if (e instanceof RequestException)
                throw (RequestException) e;
            throw new RequestException(e);
        }
        return this;
    }

    private void checkCancelled() throws RequestException {
        if (isCancelled) {
            RequestException ex = new RequestException(this);
            Log.d(this, ex.getMessage());
            throw ex;
        }
    }

    public void cancel() {
        isCancelled = true;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public Response response() {
        return mResponse;
    }

    public String url() {
        return mBuilder.mUrl;
    }

    public Method method() {
        return mBuilder.mMethod;
    }

    @Override
    public String toString() {
        if (mResponse != null)
            return String.format("[%s]: %s %s", mResponse.toString(), method().name(), url());
        else if (isCancelled())
            return String.format("[CANCELLED] %s %s", method().name(), url());
        return String.format("%s %s", method().name(), url());
    }
}
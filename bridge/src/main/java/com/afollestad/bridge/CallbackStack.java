package com.afollestad.bridge;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
final class CallbackStack {

    public static String createKey(Request req) {
        return String.format("%s\0%s\0%s", req.method().name(), req.url(),
                req.builder().mBody != null ? req.builder().mBody.length + "" : "");
    }

    private final Object LOCK = new Object();
    private List<Callback> mCallbacks;
    private List<Request> mRequests;
    private int mPercent = -1;
    private Handler mHandler;

    public CallbackStack() {
        mCallbacks = new ArrayList<>();
        mRequests = new ArrayList<>();
        mHandler = new Handler();
    }

    public int size() {
        synchronized (LOCK) {
            if (mCallbacks == null) return -1;
            return mCallbacks.size();
        }
    }

    public void push(Callback callback, Request request) {
        synchronized (LOCK) {
            if (mCallbacks == null)
                throw new IllegalStateException("This stack has already been fired or cancelled.");
            mCallbacks.add(callback);
            mRequests.add(request);
        }
    }

    public void fireAll(final Request request, final Response response, final RequestException error) {
        synchronized (LOCK) {
            if (mCallbacks == null)
                throw new IllegalStateException("This stack has already been fired.");
            for (final Callback cb : mCallbacks) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cb.response(request, response, error);
                    }
                });
            }
            mCallbacks.clear();
            mCallbacks = null;
            mRequests.clear();
            mRequests = null;
        }
    }

    public void fireAllProgress(final Request request, final int current, final int total) {
        synchronized (LOCK) {
            if (mCallbacks == null)
                throw new IllegalStateException("This stack has already been fired.");
            int newPercent = (int) (((float) current / (float) total) * 100f);
            if (newPercent != mPercent) {
                mPercent = newPercent;
                synchronized (LOCK) {
                    for (final Callback cb : mCallbacks) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                cb.progress(request, current, total, mPercent);
                            }
                        });
                    }
                }
            }
        }
    }

    public void cancelAll(boolean force) {
        synchronized (LOCK) {
            if (mCallbacks == null)
                throw new IllegalStateException("This stack has already been cancelled.");
            int index = 0;
            for (final Request req : mRequests) {
                if (req.isCancelable() || force) {
                    req.cancel(force);
                    final Callback fCallback = mCallbacks.get(index);
                    req.mCancelCallbackFired = true;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            fCallback.response(req, null, new RequestException(req));
                        }
                    });
                }
                index++;
            }
            mCallbacks.clear();
            mCallbacks = null;
            mRequests.clear();
            mRequests = null;
        }
    }
}

package com.afollestad.bridge;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Iterator;
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
    private Request mDriverRequest;
    private int mPercent = -1;
    private Handler mHandler;

    public CallbackStack() {
        mCallbacks = new ArrayList<>();
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
            callback.isCancellable = request.isCancellable();
            mCallbacks.add(callback);
            if (mDriverRequest == null)
                mDriverRequest = request;
        }
    }

    public void fireAll(final Response response, final RequestException error) {
        synchronized (LOCK) {
            if (mCallbacks == null)
                throw new IllegalStateException("This stack has already been fired.");
            for (final Callback cb : mCallbacks) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cb.response(mDriverRequest, response, error);
                    }
                });
            }
            mDriverRequest = null;
            mCallbacks.clear();
            mCallbacks = null;
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

    public boolean cancelAll(boolean force) {
        synchronized (LOCK) {
            if (mCallbacks == null)
                throw new IllegalStateException("This stack has already been cancelled.");
            final Iterator<Callback> callIter = mCallbacks.iterator();
            while (callIter.hasNext()) {
                final Callback callback = callIter.next();
                if (callback.isCancellable || force) {
                    callIter.remove();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.response(mDriverRequest, null, new RequestException(mDriverRequest));
                        }
                    });
                }
            }
            if (mCallbacks.size() == 0) {
                mDriverRequest.mCancelCallbackFired = true;
                mDriverRequest.cancel(force);
                mDriverRequest = null;
                mCallbacks = null;
                return true;
            } else {
                return false;
            }
        }
    }
}
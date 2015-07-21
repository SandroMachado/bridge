package com.afollestad.bridge;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
final class CallbackStack {

    public static String createKey(Request req) {
        return String.format("%s:%s:%s", req.method().name(), req.url(),
                req.builder().mBody != null ? req.builder().mBody.length + "" : "");
    }

    private final Object LOCK = new Object();
    private List<Callback> mCallbacks;
    private List<Request> mRequests;
    private int mPercent = -1;

    public CallbackStack() {
        mCallbacks = new ArrayList<>();
        mRequests = new ArrayList<>();
    }

    public int size() {
        if (mCallbacks == null) return -1;
        synchronized (LOCK) {
            return mCallbacks.size();
        }
    }

    public void push(Callback callback, Request request) {
        if (mCallbacks == null)
            throw new IllegalStateException("This stack has already been fired or cancelled.");
        synchronized (LOCK) {
            mCallbacks.add(callback);
            mRequests.add(request);
        }
    }

    public void fireAll(Request request, Response response, RequestException error) {
        if (mCallbacks == null)
            throw new IllegalStateException("This stack has already been fired.");
        synchronized (LOCK) {
            for (Callback cb : mCallbacks)
                cb.response(request, response, error);
            mCallbacks.clear();
            mCallbacks = null;
            mRequests.clear();
            mRequests = null;
        }
    }

    public void fireAllProgress(Request request, int current, int total) {
        if (mCallbacks == null)
            throw new IllegalStateException("This stack has already been fired.");
        int newPercent = (int) (((float) current / (float) total) * 100f);
        if (newPercent != mPercent) {
            mPercent = newPercent;
            synchronized (LOCK) {
                for (Callback cb : mCallbacks)
                    cb.progress(request, current, total, mPercent);
            }
        }
    }

    public void cancelAll() {
        if (mCallbacks == null)
            throw new IllegalStateException("This stack has already been cancelled.");
        mCallbacks.clear();
        mCallbacks = null;
        synchronized (LOCK) {
            for (Request req : mRequests)
                req.cancel();
            mRequests.clear();
            mRequests = null;
        }
    }
}

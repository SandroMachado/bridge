package com.afollestad.bridge;

import android.support.annotation.Nullable;

import java.io.Closeable;

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

    private Util() {
    }
}

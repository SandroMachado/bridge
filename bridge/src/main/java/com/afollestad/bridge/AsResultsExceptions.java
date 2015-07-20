package com.afollestad.bridge;

import android.graphics.Bitmap;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * A version of {@link AsResults} that throws exceptions for all methods. Used in {@link RequestBuilder}.
 *
 * @author Aidan Follestad (afollestad)
 */
interface AsResultsExceptions {

    byte[] asBytes() throws Exception;

    String asString() throws Exception;

    Spanned asHtml() throws Exception;

    Bitmap asBitmap() throws Exception;

    JSONObject asJsonObject() throws Exception;

    JSONArray asJsonArray() throws Exception;

    void asFile(File destination) throws Exception;
}

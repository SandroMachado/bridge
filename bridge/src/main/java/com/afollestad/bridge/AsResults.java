package com.afollestad.bridge;

import android.graphics.Bitmap;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * @author Aidan Follestad (afollestad)
 */
interface AsResults {

    byte[] asBytes();

    String asString();

    Spanned asHtml();

    Bitmap asBitmap();

    JSONObject asJsonObject() throws ResponseException;

    JSONArray asJsonArray() throws ResponseException;

    void asFile(File destination) throws IOException;
}

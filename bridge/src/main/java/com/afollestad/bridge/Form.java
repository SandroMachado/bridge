package com.afollestad.bridge;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class Form {

    public static class Entry {

        public final String name;
        public final Object value;

        public Entry(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public Form() {
        mEntries = new ArrayList<>();
    }

    private final List<Entry> mEntries;

    public Form add(String name, Object value) {
        mEntries.add(new Entry(name, value));
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < mEntries.size(); i++) {
            if (i > 0) result.append("&");
            final Entry entry = mEntries.get(i);
            try {
                result.append(URLEncoder.encode(entry.name, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.value + "", "UTF-8"));
            } catch (Exception e) {
                // This should never happen
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }
}

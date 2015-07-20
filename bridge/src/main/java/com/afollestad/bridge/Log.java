package com.afollestad.bridge;

/**
 * @author Aidan Follestad (afollestad)
 */
final class Log {

    private static String getTag(Object context) {
        if (context instanceof String) return (String) context;
        final Class cls;
        if (context instanceof Class) cls = (Class) context;
        else cls = context.getClass();
        return cls.getSimpleName();
    }

    public static void d(Object context, String message, Object... formatArgs) {
        android.util.Log.d(getTag(context), String.format(message, formatArgs));
    }

    public static void v(Object context, String message, Object... formatArgs) {
        android.util.Log.v(getTag(context), String.format(message, formatArgs));
    }

    public static void e(Object context, String message, Object... formatArgs) {
        android.util.Log.e(getTag(context), String.format(message, formatArgs));
    }

    public static void i(Object context, String message, Object... formatArgs) {
        android.util.Log.i(getTag(context), String.format(message, formatArgs));
    }

    public static void w(Object context, String message, Object... formatArgs) {
        android.util.Log.w(getTag(context), String.format(message, formatArgs));
    }

    public static void wtf(Object context, String message, Object... formatArgs) {
        android.util.Log.wtf(getTag(context), String.format(message, formatArgs));
    }

    public static void println(Object context, int priority, String message, Object... formatArgs) {
        android.util.Log.println(priority, getTag(context), String.format(message, formatArgs));
    }

    private Log() {
    }
}

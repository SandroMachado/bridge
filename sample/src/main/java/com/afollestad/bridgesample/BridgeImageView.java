package com.afollestad.bridgesample;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.Callback;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.RequestException;
import com.afollestad.bridge.Response;

/**
 * Recycles its Bitmap when detached from the window (Activity)
 *
 * @author Aidan Follestad (afollestad)
 */
public class BridgeImageView extends ImageView {

    public BridgeImageView(Context context) {
        super(context);
    }

    public BridgeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BridgeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean mCancelable = true;
    private Object mTag;

    public void setImageURI(Uri uri, boolean cancelable, Object tag) {
        mCancelable = cancelable;
        mTag = tag;
        setImageURI(uri);
    }

    @Override
    public void setImageURI(Uri uri) {
        Bridge.client()
                .get(uri.toString())
                .cancellable(mCancelable)
                .tag(mTag)
                .request(new Callback() {
                    @Override
                    public void response(Request request, Response response, RequestException e) {
                        if (response != null)
                            setImageBitmap(response.asBitmap());
                    }
                });
    }
}

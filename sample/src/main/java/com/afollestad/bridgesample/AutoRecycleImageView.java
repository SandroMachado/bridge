package com.afollestad.bridgesample;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Recycles its Bitmap when detached from the window (Activity)
 *
 * @author Aidan Follestad (afollestad)
 */
public class AutoRecycleImageView extends ImageView {

    public AutoRecycleImageView(Context context) {
        super(context);
    }

    public AutoRecycleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoRecycleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Bitmap mBitmap;

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}

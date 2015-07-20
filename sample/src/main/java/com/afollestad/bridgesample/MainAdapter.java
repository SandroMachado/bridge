package com.afollestad.bridgesample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.Callback;
import com.afollestad.bridge.Request;
import com.afollestad.bridge.RequestException;
import com.afollestad.bridge.Response;

/**
 * @author Aidan Follestad (afollestad)
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    public MainAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_main, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Bridge.client()
                .get("http://assets.toptal.io/uploads/blog/category/logo/10/android.png")
                .request(new Callback() {
                    @Override
                    public void response(Request request, Response response, RequestException e) {
                        if (response != null)
                            viewHolder.image.setImageBitmap(response.asBitmap());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return 60;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final AutoRecycleImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (AutoRecycleImageView) itemView.findViewById(R.id.image);
        }
    }
}

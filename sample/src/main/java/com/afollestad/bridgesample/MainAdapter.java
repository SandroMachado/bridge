package com.afollestad.bridgesample;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        viewHolder.image.setImageURI(Uri.parse(
                "http://assets.toptal.io/uploads/blog/category/logo/10/android.png"));
    }

    @Override
    public int getItemCount() {
        return 60;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final BridgeImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (BridgeImageView) itemView.findViewById(R.id.image);
        }
    }
}

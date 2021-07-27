package com.example.singbike.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.singbike.R;

import java.util.ArrayList;

public class AttachmentRecyclerViewAdapter extends RecyclerView.Adapter<AttachmentRecyclerViewAdapter.ViewHolder> {

    public interface AttachmentOnClickListener {
        void onClickAttachment (Uri imageUri, final int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView attachmentImage;

        public ViewHolder (View v) {
            super(v);
            this.attachmentImage = v.findViewById (R.id.attachmentImage);
        }

        public void bind (final Uri uri, final int position, final AttachmentOnClickListener listener) {
            this.attachmentImage.setImageURI (uri);
            itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onClickAttachment (uri, position);
                    }
                }
            );
        }

    }

    private final ArrayList<Uri> imageUris;
    private final AttachmentOnClickListener listener;

    public AttachmentRecyclerViewAdapter (AttachmentOnClickListener listener) {
        this.imageUris = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup, final int position) {
        View v = LayoutInflater.from (viewGroup.getContext())
                .inflate (R.layout.attachment_item, viewGroup, false);
        return new ViewHolder (v);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.bind (this.imageUris.get(position), position, this.listener);
    }

    @Override
    public int getItemCount () { return this.imageUris.size(); }

    public void addAttachment (Uri imageUri) { this.imageUris.add(imageUri); }

    public void removeAttachment (final int position) { this.imageUris.remove(position); }

}

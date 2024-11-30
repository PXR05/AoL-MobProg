package com.pxr.golf.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.pxr.golf.R;
import com.pxr.golf.models.Post;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final List<Post.Edge> posts;
    private Context ctx;

    public PostAdapter(List<Post.Edge> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View item = inflater.inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post.Node node = posts.get(position).getNode();
        holder.postImage.setImageResource(R.drawable.logo);

        new Thread(() -> {
            try {
                InputStream stream = (InputStream) new URL(node.getDisplayUrl()).getContent();
                Drawable drawable = Drawable.createFromStream(stream, "src name");
                holder.itemView.post(() -> {
                    holder.postImage.setImageDrawable(drawable);
                });
            } catch (IOException e) {
                Log.e("PostAdapter", "onBindViewHolder: Failed to load image", e);
            }
        }).start();

        holder.postContainer.setOnClickListener(e -> {
            Intent redirect = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/p/" + node.getShortcode()));
            ctx.startActivity(redirect);
        });
    }

    @Override
    public int getItemCount() {
        if (posts == null) {
            return 0;
        }
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout postContainer;
        ShapeableImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postContainer = itemView.findViewById(R.id.postItemContainer);
            postImage = itemView.findViewById(R.id.postItemImage);
        }
    }
}

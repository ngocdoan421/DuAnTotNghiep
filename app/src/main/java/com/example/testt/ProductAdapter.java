package com.example.testt;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(ProductItem item, boolean shouldAdd);
    }

    private List<ProductItem> items;
    private Set<String> favoriteIds;
    private OnFavoriteToggleListener favoriteListener;

    public ProductAdapter(List<ProductItem> items) {
        this(items, new HashSet<>(), null);
    }

    public ProductAdapter(List<ProductItem> items, Set<String> favoriteIds, OnFavoriteToggleListener favoriteListener) {
        this.items = items;
        this.favoriteIds = favoriteIds != null ? favoriteIds : new HashSet<>();
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem item = items.get(position);
        holder.tvProductName.setText(item.getName());
        holder.tvProductPrice.setText(item.getPrice());
        Glide.with(holder.ivProductImage.getContext())
                .load(item.getImageUrl())
                .centerCrop()
                .into(holder.ivProductImage);

        boolean isFavorite = favoriteIds.contains(item.getId());
        holder.ivFavorite.setColorFilter(isFavorite ? Color.RED : Color.WHITE);
        holder.ivFavorite.setOnClickListener(v -> {
            if (favoriteListener != null) {
                favoriteListener.onFavoriteToggle(item, !isFavorite);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", item.getId());
            intent.putExtra("PRODUCT_NAME", item.getName());
            intent.putExtra("PRODUCT_PRICE", item.getPrice());
            intent.putExtra("PRODUCT_IMAGE", item.getImageUrl());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ProductItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setFavoriteIds(Set<String> favoriteIds) {
        this.favoriteIds = favoriteIds != null ? favoriteIds : new HashSet<>();
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductPrice;
        ImageView ivFavorite;
        ImageView ivProductImage;

        ProductViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvProductPrice = view.findViewById(R.id.tvProductPrice);
            ivFavorite = view.findViewById(R.id.ivFavorite);
            ivProductImage = view.findViewById(R.id.ivProductImage);
        }
    }
}

package com.example.testt.fragment;

import com.example.testt.R;

import com.example.testt.activity.*;
import com.example.testt.fragment.*;
import com.example.testt.adapter.*;
import com.example.testt.model.*;
import com.example.testt.helper.*;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavorites;
    private TextView tvNoFavorites;
    private FavoriteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites);

        rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new FavoriteAdapter(new ArrayList<>(), this::removeFavorite);
        rvFavorites.setAdapter(adapter);

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            tvNoFavorites.setText("Đăng nhập để xem sản phẩm yêu thích.");
            tvNoFavorites.setVisibility(View.VISIBLE);
            adapter.setItems(new ArrayList<>());
            return;
        }

        FirestoreHelper.loadFavoriteProducts(new FirestoreHelper.ProductsCallback() {
            @Override
            public void onLoaded(List<ProductItem> products) {
                if (products.isEmpty()) {
                    tvNoFavorites.setText("Bạn chưa có sản phẩm yêu thích.");
                    tvNoFavorites.setVisibility(View.VISIBLE);
                } else {
                    tvNoFavorites.setVisibility(View.GONE);
                }
                adapter.setItems(products);
            }

            @Override
            public void onFailure(String error) {
                tvNoFavorites.setText("Không thể tải yêu thích: " + error);
                tvNoFavorites.setVisibility(View.VISIBLE);
                adapter.setItems(new ArrayList<>());
            }
        });
    }

    private void removeFavorite(ProductItem item) {
        FirestoreHelper.removeFavoriteProduct(item.getId(), new FirestoreHelper.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                loadFavorites();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Không thể bỏ yêu thích: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

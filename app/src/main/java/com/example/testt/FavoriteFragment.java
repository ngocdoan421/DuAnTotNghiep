package com.example.testt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        
        RecyclerView rvFavorites = view.findViewById(R.id.rvFavorites);
        
        List<ProductItem> items = new ArrayList<>();
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        
        FavoriteAdapter adapter = new FavoriteAdapter(items);
        rvFavorites.setAdapter(adapter);
        
        return view;
    }
}

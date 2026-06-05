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

public class CategoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);
        
        List<CategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem("Thời Trang Nữ"));
        items.add(new CategoryItem("Thời Trang Nam"));
        items.add(new CategoryItem("Trẻ Em"));
        items.add(new CategoryItem("Phụ Kiện"));
        items.add(new CategoryItem("Đồ Bơi"));
        items.add(new CategoryItem("Đồ Lót"));
        
        CategoryAdapter adapter = new CategoryAdapter(items);
        rvCategories.setAdapter(adapter);
        
        return view;
    }
}

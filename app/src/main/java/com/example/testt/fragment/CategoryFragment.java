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
import android.widget.Toast;
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
        
        CategoryAdapter adapter = new CategoryAdapter(new ArrayList<>());
        rvCategories.setAdapter(adapter);

        FirestoreHelper.loadCategories(new FirestoreHelper.CategoriesCallback() {
            @Override
            public void onLoaded(List<CategoryItem> categories) {
                adapter.setItems(categories);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Không thể tải danh mục: " + error, Toast.LENGTH_LONG).show();
            }
        });
        
        return view;
    }
}

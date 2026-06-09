package com.example.testt;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private LinearLayout llCategoryContainer;
    private RecyclerView rvNewArrivals;
    private ProductAdapter newArrivalsAdapter;
    private Set<String> favoriteIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView ivSearch = view.findViewById(R.id.ivSearch);
        ImageView ivNotification = view.findViewById(R.id.ivNotification);
        llCategoryContainer = view.findViewById(R.id.llCategoryContainer);
        rvNewArrivals = view.findViewById(R.id.rvNewArrivals);

        rvNewArrivals.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        newArrivalsAdapter = new ProductAdapter(new ArrayList<>(), favoriteIds, this::handleFavoriteToggle);
        rvNewArrivals.setAdapter(newArrivalsAdapter);

        ivSearch.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).setCurrentPage(1);
            }
        });

        ivNotification.setOnClickListener(v -> {
            if (getActivity() != null) {
                startActivity(new Intent(requireContext(), NotificationsActivity.class));
            }
        });

        loadCategories();
        loadFavoriteIds();
        loadNewArrivals();
        return view;
    }

    private void loadCategories() {
        FirestoreHelper.loadCategories(new FirestoreHelper.CategoriesCallback() {
            @Override
            public void onLoaded(List<CategoryItem> categories) {
                showCategories(categories);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Không thể tải danh mục: " + error, Toast.LENGTH_SHORT).show();
                showCategories(null);
            }
        });
    }

    private void showCategories(List<CategoryItem> categories) {
        llCategoryContainer.removeAllViews();
        if (categories == null || categories.isEmpty()) {
            String[] fallback = {"Nữ", "Nam", "Trẻ em", "Phụ kiện"};
            for (String name : fallback) {
                llCategoryContainer.addView(createCategoryPill(name, null));
            }
            return;
        }

        for (CategoryItem category : categories) {
            llCategoryContainer.addView(createCategoryPill(category.getName(), category));
        }
    }

    private void loadNewArrivals() {
        FirestoreHelper.loadAllProducts(new FirestoreHelper.ProductsCallback() {
            @Override
            public void onLoaded(List<ProductItem> products) {
                int count = Math.min(products.size(), 10);
                newArrivalsAdapter.setItems(products.subList(0, count));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Không thể tải sản phẩm mới: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavoriteIds() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            favoriteIds.clear();
            newArrivalsAdapter.setFavoriteIds(favoriteIds);
            return;
        }

        FirestoreHelper.loadFavoriteIds(new FirestoreHelper.FavoriteIdsCallback() {
            @Override
            public void onLoaded(List<String> ids) {
                favoriteIds.clear();
                favoriteIds.addAll(ids);
                newArrivalsAdapter.setFavoriteIds(favoriteIds);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Không thể tải danh sách yêu thích: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFavoriteToggle(ProductItem item, boolean shouldAdd) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để quản lý yêu thích", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        if (shouldAdd) {
            FirestoreHelper.addFavoriteProduct(item, new FirestoreHelper.SimpleCallback() {
                @Override
                public void onSuccess() {
                    favoriteIds.add(item.getId());
                    newArrivalsAdapter.setFavoriteIds(favoriteIds);
                    Toast.makeText(requireContext(), "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(requireContext(), "Không thể thêm yêu thích: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            FirestoreHelper.removeFavoriteProduct(item.getId(), new FirestoreHelper.SimpleCallback() {
                @Override
                public void onSuccess() {
                    favoriteIds.remove(item.getId());
                    newArrivalsAdapter.setFavoriteIds(favoriteIds);
                    Toast.makeText(requireContext(), "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(requireContext(), "Không thể bỏ yêu thích: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private TextView createCategoryPill(String title, @Nullable CategoryItem category) {
        TextView tv = new TextView(requireContext());
        tv.setText(title != null ? title : "Danh mục");
        tv.setTextColor(getResources().getColor(R.color.black, requireContext().getTheme()));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setPadding(dpToPx(16), 0, dpToPx(16), 0);
        tv.setBackgroundResource(R.drawable.bg_pill);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dpToPx(40));
        params.setMarginEnd(dpToPx(12));
        tv.setLayoutParams(params);
        tv.setOnClickListener(v -> {
            if (category != null && category.getId() != null) {
                Intent intent = new Intent(requireContext(), ProductListActivity.class);
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_NAME", category.getName());
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn danh mục khả dụng.", Toast.LENGTH_SHORT).show();
            }
        });
        return tv;
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}

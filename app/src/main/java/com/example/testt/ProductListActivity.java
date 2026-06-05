package com.example.testt;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvTitle), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + 20, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        TextView tvTitle = findViewById(R.id.tvTitle);
        if (categoryName != null) {
            tvTitle.setText(categoryName);
        }

        ImageView ivFilter = findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(v -> {
            FilterBottomSheetFragment filterSheet = new FilterBottomSheetFragment();
            filterSheet.show(getSupportFragmentManager(), filterSheet.getTag());
        });

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        List<ProductItem> items = new ArrayList<>();
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        items.add(new ProductItem("Áo Sơ Mi Lụa", "1.250.000đ"));
        items.add(new ProductItem("Quần Âu Công Sở", "1.450.000đ"));
        items.add(new ProductItem("Chân Váy Bút Chì", "950.000đ"));
        items.add(new ProductItem("Đầm Dạ Hội Cao Cấp", "3.200.000đ"));

        ProductAdapter adapter = new ProductAdapter(items);
        rvProducts.setAdapter(adapter);
    }
}

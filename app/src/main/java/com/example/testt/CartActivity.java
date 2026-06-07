package com.example.testt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomSummary), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom() + systemBars.bottom);
            return insets;
        });

        ImageView ivCloseCart = findViewById(R.id.ivCloseCart);
        ivCloseCart.setOnClickListener(v -> finish());

        RecyclerView rvCartItems = findViewById(R.id.rvCartItems);
        List<ProductItem> items = new ArrayList<>();
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));
        items.add(new ProductItem("Váy Thanh Lịch", "1.890.000đ"));

        CartAdapter adapter = new CartAdapter(items);
        rvCartItems.setAdapter(adapter);

        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, ShippingAddressActivity.class));
        });
    }
}

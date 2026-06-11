package com.example.testt.activity;

import com.example.testt.R;

import com.example.testt.activity.*;
import com.example.testt.fragment.*;
import com.example.testt.adapter.*;
import com.example.testt.model.*;
import com.example.testt.helper.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ivBack), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), s.top + 20, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        String productId   = getIntent().getStringExtra("PRODUCT_ID");
        String productName = getIntent().getStringExtra("PRODUCT_NAME");
        String productPrice = getIntent().getStringExtra("PRODUCT_PRICE");
        String imageUrl    = getIntent().getStringExtra("PRODUCT_IMAGE");

        if ((productId == null || productId.isEmpty()) && productName != null) {
            productId = productName.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
        }

        final String finalProductId = productId;
        final String finalProductName = productName != null ? productName : "";
        final String finalProductPrice = productPrice != null ? productPrice : "";
        final String finalImageUrl = imageUrl != null ? imageUrl : "";

        TextView tvName  = findViewById(R.id.tvProductName);
        TextView tvPrice = findViewById(R.id.tvProductPrice);
        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        if (!finalImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(finalImageUrl)
                    .centerCrop()
                    .into(ivProductImage);
        }
        if (!finalProductName.isEmpty()) tvName.setText(finalProductName);
        if (!finalProductPrice.isEmpty()) tvPrice.setText(finalProductPrice);

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnAddToCart).setOnClickListener(v -> {
            if (finalProductId == null) {
                Toast.makeText(this, "Lỗi sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            CartItem item = new CartItem(finalProductId, finalProductName, finalProductPrice, 1, finalImageUrl != null ? finalImageUrl : "");
            new CartManager().addToCart(item, new CartManager.CartCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng ✓", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(ProductDetailActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
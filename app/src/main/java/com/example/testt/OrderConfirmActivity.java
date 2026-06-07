package com.example.testt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class OrderConfirmActivity extends AppCompatActivity {

    private static final long SHIPPING_FEE = 30000;

    private ProgressBar progressBar;
    private CartManager cartManager;
    private TextView tvShippingAddress;
    private TextView tvSubtotal;
    private TextView tvTotal;
    private TextView tvPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), s.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        cartManager = new CartManager();
        progressBar = findViewById(R.id.progressBar);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTotal = findViewById(R.id.tvTotal);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        String shippingAddress = getIntent().getStringExtra("shipping_address");
        if (shippingAddress != null && !shippingAddress.isEmpty()) {
            tvShippingAddress.setText(shippingAddress);
        }
        if (tvPaymentMethod != null) {
            tvPaymentMethod.setText("Thẻ tín dụng");
        }
        loadOrderSummary();

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> placeOrder());
    }

    private void loadOrderSummary() {
        cartManager.loadCart(new CartManager.CartLoadCallback() {
            @Override
            public void onLoaded(List<CartItem> items) {
                long subtotal = 0;
                for (CartItem item : items) {
                    subtotal += item.getPriceAsLong() * item.getQuantity();
                }
                long total = subtotal + SHIPPING_FEE;

                if (tvSubtotal != null) {
                    tvSubtotal.setText(formatCurrency(subtotal));
                }
                if (tvTotal != null) {
                    tvTotal.setText(formatCurrency(total));
                }
            }

            @Override
            public void onFailure(String error) {
                // Không cần hiển thị lỗi cho bản tóm tắt đơn hàng
            }
        });
    }

    private void placeOrder() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.btnPlaceOrder).setEnabled(false);

        // Load giỏ hàng rồi lưu đơn hàng
        cartManager.loadCart(new CartManager.CartLoadCallback() {
            @Override
            public void onLoaded(List<CartItem> items) {
                if (items.isEmpty()) {
                    Toast.makeText(OrderConfirmActivity.this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    findViewById(R.id.btnPlaceOrder).setEnabled(true);
                    return;
                }
                saveOrderToFirestore(items);
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(OrderConfirmActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                findViewById(R.id.btnPlaceOrder).setEnabled(true);
            }
        });
    }

    private void saveOrderToFirestore(List<CartItem> items) {
        String uid     = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String date    = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        long subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getPriceAsLong() * item.getQuantity();
        }
        long total = subtotal + SHIPPING_FEE;

        Map<String, Object> order = new HashMap<>();
        order.put("orderId", orderId);
        order.put("userId", uid);
        order.put("date", date);
        order.put("createdAt", Timestamp.now());
        order.put("status", "Đang xử lý");
        order.put("subtotal", subtotal);
        order.put("shippingFee", SHIPPING_FEE);
        order.put("total", total);
        String shippingAddress = getIntent().getStringExtra("shipping_address");
        if (shippingAddress != null && !shippingAddress.isEmpty()) {
            order.put("shippingAddress", shippingAddress);
        }
        order.put("items", items); // Firestore tự serialize list

        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(v -> {
                    // Xóa giỏ hàng sau khi đặt thành công
                    cartManager.clearCart(new CartManager.CartCallback() {
                        @Override public void onSuccess() {
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(OrderConfirmActivity.this, OrderSuccessActivity.class);
                            intent.putExtra("order_id", orderId);
                            startActivity(intent);
                            finish();
                        }
                        @Override public void onFailure(String error) {
                            // Đơn đã lưu, giỏ chưa xóa được — vẫn chuyển màn
                            if (progressBar != null) progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(OrderConfirmActivity.this, OrderSuccessActivity.class);
                            intent.putExtra("order_id", orderId);
                            startActivity(intent);
                            finish();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    findViewById(R.id.btnPlaceOrder).setEnabled(true);
                    Toast.makeText(this, "Đặt hàng thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private String formatCurrency(long value) {
        return String.format("%,dđ", value).replace(",", ".");
    }}
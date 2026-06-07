package com.example.testt;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderConfirmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.topBar), (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), s.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        // Nút back
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        // Nút Đặt Hàng → màn 20 OrderSuccess (đã có sẵn)
        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("order_id", "#TRF-2025-001234");
            startActivity(intent);
        });
    }
}
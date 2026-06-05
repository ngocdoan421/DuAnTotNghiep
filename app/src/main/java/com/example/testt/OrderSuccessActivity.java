package com.example.testt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvOrderId;
    private Button btnTrackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        ivBack = findViewById(R.id.ivBack);
        tvOrderId = findViewById(R.id.tvOrderId);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);

        // Setup back button
        ivBack.setOnClickListener(v -> finish());

        // Get order ID from intent if passed
        String orderId = getIntent().getStringExtra("order_id");
        if (orderId != null) {
            tvOrderId.setText(orderId);
        }

        // Setup track order button
        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, TrackOrderActivity.class);
            intent.putExtra("order_id", tvOrderId.getText().toString());
            startActivity(intent);
        });
    }
}

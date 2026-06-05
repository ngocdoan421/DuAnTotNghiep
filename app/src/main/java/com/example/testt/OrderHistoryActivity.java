package com.example.testt;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOrders;
    private OrderHistoryAdapter adapter;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        ivBack = findViewById(R.id.ivBack);
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);

        // Setup back button
        ivBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter();
        recyclerViewOrders.setAdapter(adapter);

        // Load mock data
        loadMockOrders();
    }

    private void loadMockOrders() {
        List<OrderItem> orders = new ArrayList<>();
        
        orders.add(new OrderItem(
            "#TRF-2024-001231",
            "Đã giao",
            "19 Tháng 5, 2026",
            "Váy Thanh Lịch",
            1,
            "1.890.000đ",
            R.drawable.ic_shopping_bag
        ));

        orders.add(new OrderItem(
            "#TRF-2024-001230",
            "Đang vận chuyển",
            "18 Tháng 5, 2026",
            "Áo Sơ Mi Trắng",
            2,
            "890.000đ",
            R.drawable.ic_shopping_bag
        ));

        orders.add(new OrderItem(
            "#TRF-2024-001229",
            "Đã giao",
            "15 Tháng 5, 2026",
            "Quần Jean Slim Fit",
            1,
            "1.250.000đ",
            R.drawable.ic_shopping_bag
        ));

        adapter.setOrderList(orders);
    }
}

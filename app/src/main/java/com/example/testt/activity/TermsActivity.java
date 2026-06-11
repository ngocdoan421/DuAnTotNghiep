package com.example.testt.activity;

import com.example.testt.R;

import com.example.testt.activity.*;
import com.example.testt.fragment.*;
import com.example.testt.adapter.*;
import com.example.testt.model.*;
import com.example.testt.helper.*;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TermsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvUpdatedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        initViews();
        setupToolbar();
        bindDocumentInfo();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvUpdatedDate = findViewById(R.id.tvUpdatedDate);
    }

    private void setupToolbar() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void bindDocumentInfo() {
        tvUpdatedDate.setText("Cập nhật lần cuối: 05/06/2026");
    }
}

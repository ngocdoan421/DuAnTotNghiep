package com.example.testt;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class AddressManagementActivity extends AppCompatActivity {

    private ImageView ivBack;
    private MaterialButton btnAddAddress;
    private MaterialButton btnEditHome;
    private MaterialButton btnDeleteHome;
    private MaterialButton btnEditOffice;
    private MaterialButton btnDeleteOffice;
    private MaterialButton btnSetOfficeDefault;
    private MaterialButton btnEditFamily;
    private MaterialButton btnDeleteFamily;
    private MaterialButton btnSetFamilyDefault;
    private MaterialCardView cardHomeAddress;
    private MaterialCardView cardOfficeAddress;
    private MaterialCardView cardFamilyAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_management);

        initViews();
        setupToolbar();
        setupAddressActions();
        setupPrimaryAction();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        btnEditHome = findViewById(R.id.btnEditHome);
        btnDeleteHome = findViewById(R.id.btnDeleteHome);
        btnEditOffice = findViewById(R.id.btnEditOffice);
        btnDeleteOffice = findViewById(R.id.btnDeleteOffice);
        btnSetOfficeDefault = findViewById(R.id.btnSetOfficeDefault);
        btnEditFamily = findViewById(R.id.btnEditFamily);
        btnDeleteFamily = findViewById(R.id.btnDeleteFamily);
        btnSetFamilyDefault = findViewById(R.id.btnSetFamilyDefault);
        cardHomeAddress = findViewById(R.id.cardHomeAddress);
        cardOfficeAddress = findViewById(R.id.cardOfficeAddress);
        cardFamilyAddress = findViewById(R.id.cardFamilyAddress);
    }

    private void setupToolbar() {
        ivBack.setOnClickListener(v -> finish());
    }

    private void setupAddressActions() {
        cardHomeAddress.setOnClickListener(v -> showMessage("Địa chỉ mặc định của bạn"));
        cardOfficeAddress.setOnClickListener(v -> showMessage("Địa chỉ văn phòng"));
        cardFamilyAddress.setOnClickListener(v -> showMessage("Địa chỉ gia đình"));

        btnEditHome.setOnClickListener(v -> showMessage("Sửa địa chỉ mặc định"));
        btnDeleteHome.setOnClickListener(v -> showMessage("Không thể xóa địa chỉ mặc định"));

        btnEditOffice.setOnClickListener(v -> showMessage("Sửa địa chỉ văn phòng"));
        btnDeleteOffice.setOnClickListener(v -> showMessage("Xóa địa chỉ văn phòng"));
        btnSetOfficeDefault.setOnClickListener(v -> showMessage("Đặt văn phòng làm mặc định"));

        btnEditFamily.setOnClickListener(v -> showMessage("Sửa địa chỉ gia đình"));
        btnDeleteFamily.setOnClickListener(v -> showMessage("Xóa địa chỉ gia đình"));
        btnSetFamilyDefault.setOnClickListener(v -> showMessage("Đặt gia đình làm mặc định"));
    }

    private void setupPrimaryAction() {
        btnAddAddress.setOnClickListener(v -> showMessage("Thêm địa chỉ giao hàng mới"));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

package com.example.testt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private LinearLayout llEditProfile;
    private LinearLayout llMyOrders;
    private LinearLayout llDeliveryAddress;
    private LinearLayout llPaymentMethods;
    private LinearLayout llNotifications;
    private LinearLayout btnHelpCenter;
    private LinearLayout btnPrivacyPolicy;
    private LinearLayout btnTermsOfService;
    private LinearLayout llSettings;
    private LinearLayout llLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        llEditProfile = view.findViewById(R.id.llEditProfile);
        llMyOrders = view.findViewById(R.id.llMyOrders);
        llDeliveryAddress = view.findViewById(R.id.llDeliveryAddress);
        llPaymentMethods = view.findViewById(R.id.llPaymentMethods);
        llNotifications = view.findViewById(R.id.llNotifications);
        btnHelpCenter = view.findViewById(R.id.btnHelpCenter);
        btnPrivacyPolicy = view.findViewById(R.id.btnPrivacyPolicy);
        btnTermsOfService = view.findViewById(R.id.btnTermsOfService);
        llSettings = view.findViewById(R.id.llSettings);
        llLogout = view.findViewById(R.id.llLogout);

        // Set click listeners
        llEditProfile.setOnClickListener(v -> handleEditProfile());
        llMyOrders.setOnClickListener(v -> handleMyOrders());
        llDeliveryAddress.setOnClickListener(v -> handleDeliveryAddress());
        llPaymentMethods.setOnClickListener(v -> handlePaymentMethods());
        llNotifications.setOnClickListener(v -> handleNotifications());
        btnHelpCenter.setOnClickListener(v -> handleHelpCenter());
        btnPrivacyPolicy.setOnClickListener(v -> handlePrivacyPolicy());
        btnTermsOfService.setOnClickListener(v -> handleTermsOfService());
        llSettings.setOnClickListener(v -> handleSettings());
        llLogout.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void handleEditProfile() {
        // Navigate to edit profile screen
        // TODO: Create EditProfileActivity or navigate to existing one
    }

    private void handleMyOrders() {
        // Navigate to orders screen
        // TODO: Create OrdersActivity or navigate to existing one
    }

    private void handleDeliveryAddress() {
        // Navigate to address management screen
        Intent intent = new Intent(getContext(), AddressManagementActivity.class);
        startActivity(intent);
    }

    private void handlePaymentMethods() {
        // Navigate to payment methods screen
        // TODO: Create PaymentMethodsActivity or navigate to existing one
    }

    private void handleNotifications() {
        // Navigate to notifications screen
        // TODO: Create NotificationsActivity or navigate to existing one
    }

    private void handleHelpCenter() {
        // Navigate to Help Center screen
        Intent intent = new Intent(getContext(), HelpCenterActivity.class);
        startActivity(intent);
    }

    private void handlePrivacyPolicy() {
        // Navigate to Privacy Policy screen
        Intent intent = new Intent(getContext(), PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    private void handleTermsOfService() {
        // Navigate to Terms of Service screen
        Intent intent = new Intent(getContext(), TermsActivity.class);
        startActivity(intent);
    }

    private void handleSettings() {
        // Navigate to settings screen
        // TODO: Create SettingsActivity or navigate to existing one
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng Xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // TODO: Clear token/session and navigate to login
                    performLogout();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void performLogout() {
        // Clear user session/token here
        // TODO: Implement actual logout logic (clear SharedPreferences, etc.)

        // Navigate to login screen
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}

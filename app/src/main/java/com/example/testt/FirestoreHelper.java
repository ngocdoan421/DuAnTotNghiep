package com.example.testt;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_CATEGORIES = "categories";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String SUBCOLLECTION_ADDRESSES = "addresses";

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public interface ProfileCallback {
        void onLoaded(UserProfile profile);
        void onFailure(String error);
    }

    public interface AddressesCallback {
        void onLoaded(List<UserAddress> addresses);
        void onFailure(String error);
    }

    public interface CategoriesCallback {
        void onLoaded(List<CategoryItem> categories);
        void onFailure(String error);
    }

    public interface ProductsCallback {
        void onLoaded(List<ProductItem> products);
        void onFailure(String error);
    }

    private static String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            throw new IllegalStateException("Người dùng chưa đăng nhập");
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private static FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    private static CollectionReference getUsersCollection() {
        return getDb().collection(COLLECTION_USERS);
    }

    private static CollectionReference getAddressesCollection() {
        return getUsersCollection().document(getCurrentUserId()).collection(SUBCOLLECTION_ADDRESSES);
    }

    public static void saveUserProfile(@NonNull UserProfile profile, @NonNull SimpleCallback callback) {
        getUsersCollection().document(getCurrentUserId())
                .set(profile)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void loadUserProfile(@NonNull ProfileCallback callback) {
        getUsersCollection().document(getCurrentUserId())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        UserProfile profile = snapshot.toObject(UserProfile.class);
                        if (profile != null) {
                            callback.onLoaded(profile);
                        } else {
                            callback.onFailure("Không tìm thấy dữ liệu người dùng");
                        }
                    } else {
                        callback.onFailure("Không tìm thấy hồ sơ người dùng");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void updateUserProfile(@NonNull Map<String, Object> updates, @NonNull SimpleCallback callback) {
        getUsersCollection().document(getCurrentUserId())
                .update(updates)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void loadAddresses(@NonNull AddressesCallback callback) {
        getAddressesCollection()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<UserAddress> addresses = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        UserAddress address = document.toObject(UserAddress.class);
                        if (address != null) {
                            address.setId(document.getId());
                            addresses.add(address);
                        }
                    }
                    callback.onLoaded(addresses);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void loadCategories(@NonNull CategoriesCallback callback) {
        getDb().collection(COLLECTION_CATEGORIES)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<CategoryItem> categories = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        CategoryItem category = document.toObject(CategoryItem.class);
                        if (category != null) {
                            category.setId(document.getId());
                            categories.add(category);
                        }
                    }
                    callback.onLoaded(categories);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void loadProducts(@NonNull String categoryId, @NonNull ProductsCallback callback) {
        getDb().collection(COLLECTION_PRODUCTS)
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ProductItem> products = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String catId = document.getString("categoryId");
                        String imageUrl = document.getString("imageUrl");
                        Object priceObj = document.get("price");
                        String price = priceObj != null ? String.valueOf(priceObj) : "";
                        
                        ProductItem product = new ProductItem(id, catId, name, price, imageUrl);
                        products.add(product);
                    }
                    callback.onLoaded(products);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void loadAllProducts(@NonNull ProductsCallback callback) {
        getDb().collection(COLLECTION_PRODUCTS)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ProductItem> products = new ArrayList<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String catId = document.getString("categoryId");
                        String imageUrl = document.getString("imageUrl");
                        Object priceObj = document.get("price");
                        String price = priceObj != null ? String.valueOf(priceObj) : "";
                        
                        ProductItem product = new ProductItem(id, catId, name, price, imageUrl);
                        products.add(product);
                    }
                    callback.onLoaded(products);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void saveAddress(@NonNull UserAddress address, @NonNull SimpleCallback callback) {
        if (address.getId() == null || address.getId().isEmpty()) {
            DocumentReference newRef = getAddressesCollection().document();
            address.setId(newRef.getId());
            newRef.set(address)
                    .addOnSuccessListener(v -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } else {
            getAddressesCollection().document(address.getId())
                    .set(address)
                    .addOnSuccessListener(v -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        }
    }

    public static void deleteAddress(@NonNull String addressId, @NonNull SimpleCallback callback) {
        getAddressesCollection().document(addressId)
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static Map<String, Object> buildUserProfileMap(@NonNull String fullName, @NonNull String email, @NonNull String phone) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("fullName", fullName);
        profile.put("email", email);
        profile.put("phone", phone);
        profile.put("createdAt", Timestamp.now());
        return profile;
    }
}

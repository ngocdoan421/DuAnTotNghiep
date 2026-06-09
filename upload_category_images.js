#!/usr/bin/env node

const admin = require("firebase-admin");
const fs = require("fs");
const path = require("path");

const SERVICE_ACCOUNT_PATH = path.resolve(__dirname, "serviceAccountKey.json");

if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
  console.error("❌ Cannot find serviceAccountKey.json");
  process.exit(1);
}

const serviceAccount = JSON.parse(
  fs.readFileSync(SERVICE_ACCOUNT_PATH, "utf8")
);

const projectId = serviceAccount.project_id;
const bucketName = `${projectId}.firebasestorage.app`;

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  storageBucket: bucketName,
});

const db = admin.firestore();
const bucket = admin.storage().bucket();

// Categories sample data
const categories = [
  {
    id: "women-fashion",
    name: "Thời Trang Nữ",
    imagePath: "./onbshot.png",
  },
  {
    id: "men-fashion",
    name: "Thời Trang Nam",
    imagePath: "./cartshot.png",
  },
  {
    id: "shoes",
    name: "Giày Dép",
    imagePath: "./onbshot.png",
  },
  {
    id: "bags",
    name: "Túi Xách",
    imagePath: "./cartshot.png",
  },
  {
    id: "accessories",
    name: "Phụ Kiện",
    imagePath: "./onbshot.png",
  },
  {
    id: "sports",
    name: "Đồ Thể Thao",
    imagePath: "./cartshot.png",
  },
];

async function uploadCategoryImage(category) {
  try {
    // Check if image file exists
    if (!fs.existsSync(category.imagePath)) {
      console.log(`⚠️  File not found: ${category.imagePath}, skipping...`);
      return null;
    }

    // Generate unique filename
    const filename = `${Date.now()}_${path.basename(category.imagePath)}`;
    const storagePath = `categories/${category.id}/${filename}`;

    console.log(`📤 Uploading ${category.name}...`);

    // Upload to Firebase Storage
    const options = {
      metadata: {
        firebaseStorageDownloadTokens: require("uuid").v4(),
      },
    };

    await bucket.upload(category.imagePath, {
      destination: storagePath,
      metadata: options.metadata,
    });

    // Generate download URL (permanent)
    const file = bucket.file(storagePath);
    const token = require("uuid").v4();
    await file.setMetadata({
      metadata: {
        firebaseStorageDownloadTokens: token,
      },
    });
    const url = `https://firebasestorage.googleapis.com/v0/b/${bucketName}/o/${encodeURIComponent(storagePath)}?alt=media&token=${token}`;

    // Save to Firestore
    await db.collection("categories").doc(category.id).set(
      {
        name: category.name,
        imageUrl: url,
        storagePath: storagePath,
        uploadedAt: new Date(),
      },
      { merge: true }
    );

    console.log(`✅ ${category.name}`);
    console.log(`   📍 Firestore: categories/${category.id}`);
    console.log(`   🔗 Image URL: ${url.substring(0, 80)}...`);

    return { id: category.id, name: category.name, imageUrl: url };
  } catch (error) {
    console.error(`❌ Error uploading ${category.name}:`, error.message);
    return null;
  }
}

async function main() {
  console.log("\n🎨 Category Images Upload");
  console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

  const results = [];
  for (const category of categories) {
    const result = await uploadCategoryImage(category);
    if (result) results.push(result);
  }

  console.log("\n📊 Summary:");
  console.log(`━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`);
  console.log(`✅ Successfully uploaded: ${results.length}/${categories.length}`);

  if (results.length > 0) {
    console.log("\n📋 Uploaded Categories:");
    results.forEach((cat) => {
      console.log(`   • ${cat.name} (${cat.id})`);
    });
  }

  console.log("\n✨ Done! Your app will now display category images.\n");

  process.exit(results.length === categories.length ? 0 : 1);
}

main().catch((error) => {
  console.error("Fatal error:", error);
  process.exit(1);
});

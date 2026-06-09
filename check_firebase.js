#!/usr/bin/env node

const admin = require("firebase-admin");
const path = require("path");
const fs = require("fs");

const SERVICE_ACCOUNT_PATH = path.resolve(__dirname, "serviceAccountKey.json");

async function checkSetup() {
  if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
    console.error("❌ Cannot find serviceAccountKey.json");
    process.exit(1);
  }

  const keyJson = JSON.parse(fs.readFileSync(SERVICE_ACCOUNT_PATH, "utf8"));
  const projectId = keyJson.project_id;
  const bucketName = `${projectId}.firebasestorage.app`;

  console.log("\n🔍 Firebase Configuration Check:");
  console.log("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
  console.log(`Project ID: ${projectId}`);
  console.log(`Storage Bucket: ${bucketName}`);
  console.log(`Service Account: ${keyJson.client_email}`);

  try {
    admin.initializeApp({
      credential: admin.credential.cert(keyJson),
      storageBucket: bucketName,
    });

    console.log("\n✅ Firebase Admin SDK initialized");

    const bucket = admin.storage().bucket();
    console.log(`📦 Storage bucket object created`);

    // Try a simple operation to verify access
    const exists = await bucket.exists();
    console.log(`\n✅ Storage bucket is accessible: ${exists[0] ? "YES" : "NO"}`);

    if (!exists[0]) {
      console.log("\n⚠️  WARNING: Bucket does not exist or is not accessible!");
      console.log("\n📝 To fix this:");
      console.log("  1. Go to: https://console.firebase.google.com/");
      console.log(`  2. Select project: ${projectId}`);
      console.log("  3. Navigate to Storage");
      console.log("  4. Click 'Create bucket' if not present");
      console.log(`  5. Use location: us-central1 (default)`);
      console.log("  6. Ensure IAM permissions allow upload");
      process.exit(1);
    }

    console.log("\n✅ All checks passed! You can now upload images.\n");
  } catch (error) {
    console.log(`\n❌ Error connecting to Firebase:`);
    console.log(`   ${error.message}`);
    console.log("\n📝 Common issues:");
    console.log("  1. Storage bucket not created in Firebase Console");
    console.log("  2. Service account doesn't have Storage permissions");
    console.log("  3. Project billing not enabled");
    process.exit(1);
  }
}

checkSetup();

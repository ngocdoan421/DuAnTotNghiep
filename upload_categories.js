#!/usr/bin/env node

const fs = require("fs");
const path = require("path");
const crypto = require("crypto");
const admin = require("firebase-admin");

const SERVICE_ACCOUNT_PATH = path.resolve(__dirname, "serviceAccountKey.json");

function usage() {
  console.log(`Usage: node upload_categories.js --file <csv-file> [--dryrun]`);
  console.log("\nCSV format (with header):");
  console.log("  name,imageUrl");
  console.log("  Thời Trang Nữ,https://example.com/women.jpg");
  console.log("  Thời Trang Nam,https://example.com/men.jpg");
  console.log("\nOr upload from local images:");
  console.log("  node upload_categories.js --imageDir ./category-images --file categories.csv\n");
}

function parseArgs() {
  const args = process.argv.slice(2);
  const options = {};
  let key = null;
  for (const token of args) {
    if (token.startsWith("--")) {
      key = token.slice(2);
      options[key] = true;
    } else if (key) {
      options[key] = token;
      key = null;
    }
  }
  return options;
}

function getContentType(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  switch (ext) {
    case ".jpg":
    case ".jpeg":
      return "image/jpeg";
    case ".png":
      return "image/png";
    case ".gif":
      return "image/gif";
    case ".webp":
      return "image/webp";
    case ".svg":
      return "image/svg+xml";
    default:
      return "application/octet-stream";
  }
}

function isImageFile(filePath) {
  const ext = path.extname(filePath).toLowerCase();
  return [".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg"].includes(ext);
}

async function uploadImage(bucket, keyJson, imagePath, folder = "categories") {
  const token = crypto.randomUUID();
  const fileName = path.basename(imagePath);
  const destination = `${folder}/${Date.now()}_${fileName}`;
  const contentType = getContentType(imagePath);
  
  const metadata = {
    metadata: {
      firebaseStorageDownloadTokens: token,
    },
    contentType,
  };

  console.log(`Uploading ${imagePath}...`);
  await bucket.upload(imagePath, {
    destination,
    metadata,
  });

  const bucketName = `${keyJson.project_id}.appspot.com`;
  const downloadUrl = `https://firebasestorage.googleapis.com/v0/b/${bucketName}/o/${encodeURIComponent(destination)}?alt=media&token=${token}`;
  return downloadUrl;
}

async function main() {
  const options = parseArgs();
  
  if (!options.file) {
    usage();
    process.exit(1);
  }

  if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
    console.error("Cannot find serviceAccountKey.json in repository root.");
    process.exit(1);
  }

  const keyJson = JSON.parse(fs.readFileSync(SERVICE_ACCOUNT_PATH, "utf8"));
  admin.initializeApp({
    credential: admin.credential.cert(keyJson),
    storageBucket: `${keyJson.project_id}.appspot.com`,
  });

  const firestore = admin.firestore();
  const bucket = admin.storage().bucket();

  const csvPath = path.resolve(process.cwd(), options.file);
  if (!fs.existsSync(csvPath)) {
    console.error("CSV file not found:", csvPath);
    process.exit(1);
  }

  const csvContent = fs.readFileSync(csvPath, "utf8");
  const lines = csvContent.trim().split("\n");
  const headers = lines[0].split(",").map(h => h.trim());
  
  const nameIdx = headers.indexOf("name");
  const imageUrlIdx = headers.indexOf("imageUrl");
  
  if (nameIdx === -1) {
    console.error("CSV must have 'name' column");
    process.exit(1);
  }

  const categories = [];
  const imageMap = {};

  // Parse CSV
  for (let i = 1; i < lines.length; i++) {
    const parts = lines[i].split(",").map(p => p.trim());
    if (parts.length > nameIdx && parts[nameIdx]) {
      const name = parts[nameIdx];
      const imageUrl = imageUrlIdx >= 0 && parts[imageUrlIdx] ? parts[imageUrlIdx] : "";
      categories.push({ name, imageUrl });
    }
  }

  if (options.imageDir) {
    const imageDir = path.resolve(process.cwd(), options.imageDir);
    if (!fs.existsSync(imageDir) || !fs.lstatSync(imageDir).isDirectory()) {
      console.error("Image directory not found:", imageDir);
      process.exit(1);
    }
    const files = fs.readdirSync(imageDir).filter(f => isImageFile(path.resolve(imageDir, f)));
    for (const file of files) {
      imageMap[file.toLowerCase()] = path.resolve(imageDir, file);
    }
  }

  if (options.dryrun) {
    console.log("DRY RUN - Categories to upload:");
    categories.forEach((cat, idx) => {
      console.log(`${idx + 1}. ${cat.name} -> ${cat.imageUrl || "(no URL)"}`);
    });
    return;
  }

  // Upload categories
  for (const cat of categories) {
    let finalImageUrl = cat.imageUrl;

    // Try to find local image
    if (!finalImageUrl) {
      const nameNorm = cat.name.toLowerCase().replaceAll(" ", "").replaceAll(/[^\w]/g, "");
      for (const [key, localPath] of Object.entries(imageMap)) {
        const keyNorm = key.toLowerCase().replaceAll(" ", "").replaceAll(/[^\w]/g, "");
        if (keyNorm.includes(nameNorm) || nameNorm.includes(keyNorm)) {
          console.log(`Matching ${cat.name} with ${key}`);
          finalImageUrl = await uploadImage(bucket, keyJson, localPath);
          break;
        }
      }
    }

    const docRef = firestore.collection("categories").doc();
    const data = {
      id: docRef.id,
      name: cat.name,
      imageUrl: finalImageUrl || "",
      createdAt: admin.firestore.Timestamp.now(),
    };

    await docRef.set(data);
    console.log(`✓ Uploaded category: ${cat.name}`);
    console.log(`  Image URL: ${finalImageUrl || "(none)"}`);
  }

  console.log("\nAll categories uploaded successfully!");
}

main().catch(error => {
  console.error("Upload failed:", error);
  process.exit(1);
});

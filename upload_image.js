#!/usr/bin/env node

const fs = require("fs");
const path = require("path");
const crypto = require("crypto");
const admin = require("firebase-admin");

const SERVICE_ACCOUNT_PATH = path.resolve(__dirname, "serviceAccountKey.json");
const DEFAULT_COLLECTION = "images";
const DEFAULT_FOLDER = "uploads";

function usage() {
  console.log(`Usage: node upload_image.js --file <image-path> | --dir <image-directory> [--recursive] [--collection <firestore-collection>] [--folder <storage-folder>] [--docId <firestore-doc-id>] [--meta '{"key":"value"}'] [--tags tag1,tag2]`);
  console.log("\nExamples:");
  console.log("  node upload_image.js --file ./assets/photo.jpg --collection images --folder products --tags clothes,fashion");
  console.log("  node upload_image.js --dir ./assets/product-images --recursive --collection products --folder product_images");
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

function walkDirectory(dir, recursive) {
  const entries = fs.readdirSync(dir, { withFileTypes: true });
  let files = [];
  for (const entry of entries) {
    const resolved = path.resolve(dir, entry.name);
    if (entry.isDirectory()) {
      if (recursive) {
        files = files.concat(walkDirectory(resolved, recursive));
      }
    } else if (entry.isFile() && isImageFile(resolved)) {
      files.push(resolved);
    }
  }
  return files;
}

async function uploadFile(bucket, firestore, keyJson, options, filePath) {
  const storageFolder = options.folder ? options.folder.replace(/^\/+|\/+$/g, "") : DEFAULT_FOLDER;
  const fileName = path.basename(filePath);
  const destination = `${storageFolder}/${Date.now()}_${fileName}`;
  const token = crypto.randomUUID();
  const contentType = getContentType(filePath);
  const metadata = {
    metadata: {
      firebaseStorageDownloadTokens: token,
    },
    contentType,
  };

  console.log(`Uploading ${filePath} to gs://${options.bucket || `${keyJson.project_id}.firebasestorage.app`}/${destination} ...`);
  await bucket.upload(filePath, {
    destination,
    metadata,
  });

  const downloadUrl = `https://firebasestorage.googleapis.com/v0/b/${options.bucket || `${keyJson.project_id}.firebasestorage.app`}/o/${encodeURIComponent(destination)}?alt=media&token=${token}`;
  const collection = options.collection || DEFAULT_COLLECTION;
  const docRef = options.docId && !options.dir
    ? firestore.collection(collection).doc(options.docId)
    : firestore.collection(collection).doc();

  const customMeta = options.meta ? JSON.parse(options.meta) : {};
  const tags = options.tags ? options.tags.split(",").map(tag => tag.trim()).filter(Boolean) : [];
  const docData = {
    fileName,
    storagePath: destination,
    downloadUrl,
    imageUrl: downloadUrl,
    bucket: options.bucket || `${keyJson.project_id}.firebasestorage.app`,
    mimeType: contentType,
    uploadedAt: admin.firestore.Timestamp.now(),
    tags,
    ...customMeta,
  };

  await docRef.set(docData, { merge: true });
  console.log("Uploaded:", filePath);
  console.log("Firestore document:", docRef.path);
  console.log("Download URL:", downloadUrl);
  console.log("---");
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
  const bucketName = options.bucket || `${keyJson.project_id}.firebasestorage.app`;

  admin.initializeApp({
    credential: admin.credential.cert(keyJson),
    storageBucket: bucketName,
  });

  const firestore = admin.firestore();
  const bucket = admin.storage().bucket();

  const files = [];
  if (options.dir) {
    const dirPath = path.resolve(process.cwd(), options.dir);
    if (!fs.existsSync(dirPath) || !fs.lstatSync(dirPath).isDirectory()) {
      console.error("Directory not found:", dirPath);
      process.exit(1);
    }
    const recursive = options.recursive === true || options.recursive === "true";
    const dirFiles = walkDirectory(dirPath, recursive);
    if (dirFiles.length === 0) {
      console.error("No supported image files found in directory:", dirPath);
      process.exit(1);
    }
    files.push(...dirFiles);
  } else {
    const filePath = path.resolve(process.cwd(), options.file);
    if (!fs.existsSync(filePath) || !fs.lstatSync(filePath).isFile()) {
      console.error("File not found:", filePath);
      process.exit(1);
    }
    if (!isImageFile(filePath)) {
      console.error("Unsupported file type:", filePath);
      process.exit(1);
    }
    files.push(filePath);
  }

  if (options.dir && options.docId) {
    console.warn("Warning: --docId is ignored when uploading a directory of files.");
  }

  for (const filePath of files) {
    await uploadFile(bucket, firestore, keyJson, options, filePath);
  }
}

main().catch(error => {
  console.error("Upload failed:", error);
  process.exit(1);
});

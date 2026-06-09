#!/usr/bin/env node

const admin = require('firebase-admin');
const path = require('path');
const fs = require('fs');

const SERVICE_ACCOUNT_PATH = path.resolve(__dirname, 'serviceAccountKey.json');
const DEFAULT_EMAIL = 'admin@testt.com';
const DEFAULT_PASSWORD = 'Admin@12345';
const DEFAULT_DISPLAY_NAME = 'Admin User';

function showUsage() {
  console.log('Usage: node create_admin_user.js [email] [password]');
  console.log('Example: node create_admin_user.js admin@testt.com Admin@12345');
}

async function main() {
  if (!fs.existsSync(SERVICE_ACCOUNT_PATH)) {
    console.error('❌ Cannot find serviceAccountKey.json in project root.');
    process.exit(1);
  }

  const email = process.argv[2] || DEFAULT_EMAIL;
  const password = process.argv[3] || DEFAULT_PASSWORD;

  if (!email || !password) {
    showUsage();
    process.exit(1);
  }

  const keyJson = JSON.parse(fs.readFileSync(SERVICE_ACCOUNT_PATH, 'utf8'));

  admin.initializeApp({
    credential: admin.credential.cert(keyJson),
  });

  try {
    const userRecord = await admin.auth().createUser({
      email,
      emailVerified: false,
      password,
      displayName: DEFAULT_DISPLAY_NAME,
      disabled: false,
    });

    await admin.auth().setCustomUserClaims(userRecord.uid, { admin: true });

    console.log('\n✅ Admin user created successfully!');
    console.log('-----------------------------------');
    console.log(`Email: ${email}`);
    console.log(`Password: ${password}`);
    console.log(`UID: ${userRecord.uid}`);
    console.log('\nNext steps:');
    console.log('  1. Open Firebase Console > Authentication > Users');
    console.log('  2. Confirm the admin user exists.');
    console.log('  3. Use these credentials in the admin panel login form.');
    console.log('  4. If you want, set a stronger password in the Firebase Console.');
    console.log('');
  } catch (error) {
    console.error('\n❌ Failed to create admin user:');
    console.error(error.message);
    if (error.code === 'auth/email-already-exists') {
      console.log('\n🔎 The email already exists. You can re-run with a different email or set admin claim manually.');
    }
    process.exit(1);
  }
}

main();

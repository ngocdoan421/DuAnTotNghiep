const admin = require('firebase-admin');
const fs = require('fs');

const serviceAccount = JSON.parse(fs.readFileSync('serviceAccountKey.json', 'utf8'));

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

async function checkAdmin() {
  try {
    const userRecord = await admin.auth().getUserByEmail('admin@testt.com');
    console.log('\n✅ Tài khoản admin tồn tại!');
    console.log('-----------------------------------');
    console.log(`Email: ${userRecord.email}`);
    console.log(`UID: ${userRecord.uid}`);
    console.log(`Đã xác minh email: ${userRecord.emailVerified}`);
    console.log(`Custom Claims:`, userRecord.customClaims);
    console.log('\n💾 Nơi lưu: Firebase Authentication (Cloud)');
    console.log('📍 Truy cập: Firebase Console > Authentication > Users');
  } catch (error) {
    console.error('\n❌ Lỗi:', error.message);
    if (error.code === 'auth/user-not-found') {
      console.log('Chưa tạo tài khoản admin. Chạy: npm run create-admin');
    }
  }
  process.exit(0);
}

checkAdmin();

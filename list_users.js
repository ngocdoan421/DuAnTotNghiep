const admin = require('firebase-admin');
const fs = require('fs');

const serviceAccount = JSON.parse(fs.readFileSync('serviceAccountKey.json', 'utf8'));

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

async function listUsers() {
  try {
    const listUsersResult = await admin.auth().listUsers(100);
    
    console.log('\n📋 Danh sách toàn bộ User:');
    console.log('===================================\n');
    
    listUsersResult.users.forEach((userRecord) => {
      const isAdmin = userRecord.customClaims?.admin === true;
      const badge = isAdmin ? '👑 ADMIN' : '👤 USER';
      
      console.log(`${badge}`);
      console.log(`  Email: ${userRecord.email}`);
      console.log(`  UID: ${userRecord.uid}`);
      console.log(`  Custom Claims: ${JSON.stringify(userRecord.customClaims)}`);
      console.log('---');
    });
    
    console.log('\n📌 Phân biệt:');
    console.log('  👑 ADMIN: Có custom claim { admin: true }');
    console.log('  👤 USER: Không có custom claim hoặc admin=false');
    
  } catch (error) {
    console.error('❌ Lỗi:', error.message);
  }
  process.exit(0);
}

listUsers();

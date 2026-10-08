module.exports = {
  /**
   * @param db {import('mongodb').Db}
   * @param client {import('mongodb').MongoClient}
   * @returns {Promise<void>}
   */
  async up(db, client) {
    await db.collection('customers').createIndex({ email: 1 }, { unique: true });
    await db.collection('orders').createIndex({ status: 1, createdAt: 1 });
  },

  /**
   * @param db {import('mongodb').Db}
   * @param client {import('mongodb').MongoClient}
   * @returns {Promise<void>}
   */
  async down(db, client) {
    await db.collection('customers').dropIndex("email_1");
    await db.collection('orders').dropIndex("status_1_createdAt_1");
  }
};

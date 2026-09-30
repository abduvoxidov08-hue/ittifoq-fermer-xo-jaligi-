package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [FarmerEntity::class, PaymentEntity::class, AdvisoryCacheEntity::class],
    version = 4,
    exportSchema = false
)
abstract class FarmDatabase : RoomDatabase() {

    abstract fun farmDao(): FarmDao

    companion object {
        @Volatile
        private var INSTANCE: FarmDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FarmDatabase::class.java,
                    "ittifoq_fermer_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(FarmDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class FarmDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedAllAccountsAndPayments(database.farmDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        ensureAccountsExist(database.farmDao())
                    }
                }
            }

            private suspend fun ensureAccountsExist(dao: FarmDao) {
                try {
                    dao.insertAllFarmers(PredefinedAccounts.ALL_ACCOUNTS)
                } catch (_: Exception) { }
            }

            private suspend fun seedAllAccountsAndPayments(dao: FarmDao) {
                // Insert all predefined accounts (Rahbar + 11 Dehqons)
                dao.insertAllFarmers(PredefinedAccounts.ALL_ACCOUNTS)
            }
        }
    }
}

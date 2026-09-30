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
    version = 2,
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
                        populateInitialAccounts(database.farmDao())
                    }
                }
            }

            private suspend fun populateInitialAccounts(dao: FarmDao) {
                // 1. Rahbar (Farm Leader)
                val rahbar = FarmerEntity(
                    username = "abdurahmon",
                    password = "rahbar",
                    role = "RAHBAR",
                    dehqonId = "IFX-2026-001",
                    name = "Abdumalikov Abdurahmon",
                    landSizeHectares = 25.0,
                    annualPlanTargetUzs = 75_000_000L,
                    phone = "+998 90 345 67 89",
                    cropType = "Bug'doy va Paxta (Bosh xo'jalik)",
                    region = "Ittifoq MFY, Markaziy bino"
                )

                // 2. Dehqonlar (Farmers with their user-specified logins & passwords)
                val dehqons = listOf(
                    FarmerEntity(
                        username = "abduvosiq",
                        password = "kabinet1",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-002",
                        name = "Abduvosiq G'ofurov",
                        landSizeHectares = 18.0,
                        annualPlanTargetUzs = 54_000_000L,
                        phone = "+998 90 111 22 33",
                        cropType = "Kuzgi bug'doy va sabzavot",
                        region = "Ittifoq MFY, 1-hudud"
                    ),
                    FarmerEntity(
                        username = "saidkarim",
                        password = "kabinet2",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-003",
                        name = "Saidkarim Qodirov",
                        landSizeHectares = 22.5,
                        annualPlanTargetUzs = 67_500_000L,
                        phone = "+998 91 222 33 44",
                        cropType = "Paxtachilik va makkajo'xori",
                        region = "Ittifoq MFY, 2-hudud"
                    ),
                    FarmerEntity(
                        username = "sobit",
                        password = "kabinet3",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-004",
                        name = "Sobit Rahimov",
                        landSizeHectares = 15.0,
                        annualPlanTargetUzs = 45_000_000L,
                        phone = "+998 93 333 44 55",
                        cropType = "G'allachilik",
                        region = "Ittifoq MFY, 1-hudud"
                    ),
                    FarmerEntity(
                        username = "karim",
                        password = "kabinet4",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-005",
                        name = "Karim Nazarov",
                        landSizeHectares = 12.0,
                        annualPlanTargetUzs = 36_000_000L,
                        phone = "+998 94 444 55 66",
                        cropType = "Poliz va sabzavot ekinlari",
                        region = "Ittifoq MFY, 3-hudud"
                    ),
                    FarmerEntity(
                        username = "mirzohid",
                        password = "kabinet5",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-006",
                        name = "Mirzohid Jo'rayev",
                        landSizeHectares = 16.5,
                        annualPlanTargetUzs = 49_500_000L,
                        phone = "+998 95 555 66 77",
                        cropType = "Bug'doy va beda",
                        region = "Ittifoq MFY, 2-hudud"
                    ),
                    FarmerEntity(
                        username = "ravshan",
                        password = "kabinet6",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-007",
                        name = "Ravshan Tursunov",
                        landSizeHectares = 20.0,
                        annualPlanTargetUzs = 60_000_000L,
                        phone = "+998 97 666 77 88",
                        cropType = "Paxtachilik",
                        region = "Ittifoq MFY, 4-hudud"
                    ),
                    FarmerEntity(
                        username = "abdujabbor",
                        password = "kabinet7",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-008",
                        name = "Abdujabbor Aliyev",
                        landSizeHectares = 14.0,
                        annualPlanTargetUzs = 42_000_000L,
                        phone = "+998 98 777 88 99",
                        cropType = "Makkajo'xori va dukkaklilar",
                        region = "Ittifoq MFY, 1-hudud"
                    ),
                    FarmerEntity(
                        username = "xolmurod",
                        password = "kabinet8",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-009",
                        name = "Xolmurod Ergashev",
                        landSizeHectares = 19.0,
                        annualPlanTargetUzs = 57_000_000L,
                        phone = "+998 99 888 99 00",
                        cropType = "G'alla va poliz",
                        region = "Ittifoq MFY, 3-hudud"
                    ),
                    FarmerEntity(
                        username = "hamidulla",
                        password = "kabinet9",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-010",
                        name = "Hamidulla Xolmatov",
                        landSizeHectares = 17.5,
                        annualPlanTargetUzs = 52_500_000L,
                        phone = "+998 90 999 00 11",
                        cropType = "Bug'doy va kungaboqar",
                        region = "Ittifoq MFY, 2-hudud"
                    ),
                    FarmerEntity(
                        username = "ubaydulla",
                        password = "kabinet10",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-011",
                        name = "Ubaydulla Mamatov",
                        landSizeHectares = 21.0,
                        annualPlanTargetUzs = 63_000_000L,
                        phone = "+998 91 123 78 90",
                        cropType = "Paxta va g'alla",
                        region = "Ittifoq MFY, 4-hudud"
                    ),
                    FarmerEntity(
                        username = "shuhrat",
                        password = "kabinet11",
                        role = "DEHQON",
                        dehqonId = "IFX-2026-012",
                        name = "Shuhrat Yusupov",
                        landSizeHectares = 13.5,
                        annualPlanTargetUzs = 40_500_000L,
                        phone = "+998 93 234 89 01",
                        cropType = "Sabzavot va kartoshka",
                        region = "Ittifoq MFY, 3-hudud"
                    )
                )

                dao.insertFarmer(rahbar)
                dao.insertAllFarmers(dehqons)

                // Initial confirmed payments for Abduvosiq and Saidkarim
                val p1 = PaymentEntity(
                    receiptNumber = "IFX-2026-0925-1024",
                    dehqonId = "IFX-2026-002",
                    farmerName = "Abduvosiq G'ofurov",
                    landSizeHectares = 18.0,
                    paymentType = "CLICK",
                    paymentAmountUzs = 12_000_000L,
                    annualPlanTargetUzs = 54_000_000L,
                    remainingDebtUzs = 42_000_000L,
                    completionPercentage = 22.22,
                    timestamp = System.currentTimeMillis() - (5L * 24 * 3600 * 1000),
                    formattedDate = "25-Sentyabr, 2026 10:15",
                    referenceCode = "CLK-1024-UZ-881",
                    status = "CONFIRMED",
                    isOfflineRecorded = false,
                    expenseCategory = "Yoqilg'i / Solyarka",
                    recordedByRole = "DEHQON",
                    telegramSent = true,
                    notes = "1-bosqich: Karta orqali shudgorlash yoqilg'isi"
                )

                val p2 = PaymentEntity(
                    receiptNumber = "IFX-2026-0928-3382",
                    dehqonId = "IFX-2026-003",
                    farmerName = "Saidkarim Qodirov",
                    landSizeHectares = 22.5,
                    paymentType = "PAYME",
                    paymentAmountUzs = 20_000_000L,
                    annualPlanTargetUzs = 67_500_000L,
                    remainingDebtUzs = 47_500_000L,
                    completionPercentage = 29.63,
                    timestamp = System.currentTimeMillis() - (2L * 24 * 3600 * 1000),
                    formattedDate = "28-Sentyabr, 2026 16:40",
                    referenceCode = "PYM-3382-UZ-492",
                    status = "CONFIRMED",
                    isOfflineRecorded = false,
                    expenseCategory = "Mineral o'g'it (Ammofos)",
                    recordedByRole = "DEHQON",
                    telegramSent = true,
                    notes = "Payme orqali kuzgi o'g'it to'lovi"
                )

                val p3 = PaymentEntity(
                    receiptNumber = "IFX-2026-0929-7711",
                    dehqonId = "IFX-2026-001",
                    farmerName = "Abdumalikov Abdurahmon",
                    landSizeHectares = 25.0,
                    paymentType = "CASH",
                    paymentAmountUzs = 25_000_000L,
                    annualPlanTargetUzs = 75_000_000L,
                    remainingDebtUzs = 50_000_000L,
                    completionPercentage = 33.33,
                    timestamp = System.currentTimeMillis() - (1L * 24 * 3600 * 1000),
                    formattedDate = "29-Sentyabr, 2026 11:20",
                    referenceCode = "CSH-7711-UZ-103",
                    status = "CONFIRMED",
                    isOfflineRecorded = false,
                    expenseCategory = "Traktor xizmati / Agrotexnika",
                    recordedByRole = "RAHBAR",
                    telegramSent = true,
                    notes = "G'aznaga naqd to'lov: Shudgor agregatlari"
                )

                dao.insertPayment(p1)
                dao.insertPayment(p2)
                dao.insertPayment(p3)
            }
        }
    }
}

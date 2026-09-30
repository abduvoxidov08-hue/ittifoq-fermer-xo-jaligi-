package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

    // Farmer Operations
    @Query("SELECT * FROM farmers ORDER BY id ASC")
    fun getAllFarmers(): Flow<List<FarmerEntity>>

    @Query("SELECT * FROM farmers WHERE dehqonId = :dehqonId LIMIT 1")
    suspend fun getFarmerByDehqonId(dehqonId: String): FarmerEntity?

    @Query("SELECT * FROM farmers WHERE username = :username LIMIT 1")
    suspend fun findFarmerByUsername(username: String): FarmerEntity?

    @Query("SELECT * FROM farmers WHERE username = :username LIMIT 1")
    fun getFarmerByUsername(username: String): Flow<FarmerEntity?>

    @Query("SELECT * FROM farmers WHERE LOWER(username) = LOWER(:username) AND password = :password LIMIT 1")
    suspend fun authenticate(username: String, password: String): FarmerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFarmer(farmer: FarmerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFarmers(farmers: List<FarmerEntity>)

    @Update
    suspend fun updateFarmer(farmer: FarmerEntity)

    // Payment Operations
    @Query("SELECT * FROM payments ORDER BY timestamp DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE dehqonId = :dehqonId ORDER BY timestamp DESC")
    fun getPaymentsForFarmer(dehqonId: String): Flow<List<PaymentEntity>>

    @Query("SELECT SUM(paymentAmountUzs) FROM payments WHERE dehqonId = :dehqonId")
    fun getTotalPaidForFarmer(dehqonId: String): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Query("UPDATE payments SET telegramSent = :sent WHERE id = :id")
    suspend fun updatePaymentTelegramStatus(id: Long, sent: Boolean)

    @Query("DELETE FROM payments WHERE id = :id")
    suspend fun deletePaymentById(id: Long)

    // Advisory Cache Operations
    @Query("SELECT * FROM advisories WHERE dehqonId = :dehqonId LIMIT 1")
    fun getAdvisoryCache(dehqonId: String): Flow<AdvisoryCacheEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAdvisoryCache(cache: AdvisoryCacheEntity)
}

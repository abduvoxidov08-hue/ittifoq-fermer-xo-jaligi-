package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "farmers",
    indices = [Index(value = ["username"], unique = true)]
)
data class FarmerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String = "",
    val password: String = "",
    val role: String = "DEHQON", // "RAHBAR" or "DEHQON"
    val dehqonId: String,
    val name: String,
    val landSizeHectares: Double,
    val annualPlanTargetUzs: Long,
    val phone: String = "+998 90 123 45 67",
    val cropType: String = "Bug'doy va Paxta (Almashlab ekish)",
    val region: String = "Ittifoq MFY, Toshkent / Farg'ona",
    val createdAt: Long = System.currentTimeMillis()
)

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "advisories")
data class AdvisoryCacheEntity(
    @PrimaryKey
    val dehqonId: String,
    val title: String,
    val summary: String,
    val recommendationsJson: String, // serialized lines
    val nextActionsJson: String,
    val fertilizerSchedule: String,
    val waterManagement: String,
    val seasonNotice: String,
    val isOfflineEngine: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

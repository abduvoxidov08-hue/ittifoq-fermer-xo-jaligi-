package com.example.data.model

data class AgriculturalAdvisory(
    val title: String,
    val summary: String,
    val recommendations: List<String>,
    val nextActions: List<String>,
    val fertilizerSchedule: String,
    val waterManagement: String,
    val seasonNotice: String,
    val isOfflineEngine: Boolean,
    val generatedAt: Long = System.currentTimeMillis()
)

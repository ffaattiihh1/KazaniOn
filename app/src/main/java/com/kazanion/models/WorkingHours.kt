package com.kazanion.models

data class WorkingHours(
    val startTime: Time,
    val endTime: Time
)

data class Time(
    val hour: Int,
    val minute: Int
) 
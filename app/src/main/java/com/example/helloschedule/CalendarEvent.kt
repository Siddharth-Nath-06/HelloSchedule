package com.example.helloschedule

import java.io.Serializable

data class CalendarEvent(
    val id: Long,
    val title: String,
    val startTime: Long,
    val endTime: Long,
    val location: String,
    val isAllDay: Boolean
) : Serializable

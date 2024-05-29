package com.bhelllr.eventsapp.models

data class Booking(
    val userId: String = "",
    val postId: String = "",
    val timestamp: Long = 0L,
    val phoneNumber: String = "",
    val queries: String = ""
)

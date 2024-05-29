package com.bhelllr.eventsapp.models

data class Post(
    val userId: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val ratings: MutableMap<String, Float> = mutableMapOf(),
    var averageRating: Float = 0f,
    val bookings: MutableMap<String, Booking> = mutableMapOf() // Add this line
)
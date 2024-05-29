package com.hellllr.bookingapp.adapters



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bhelllr.eventsapp.models.Booking
import com.hellllr.bookingapp.R

class BookingAdapter(private val bookings: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)
        val queriesTextView: TextView = itemView.findViewById(R.id.queriesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.phoneNumberTextView.text = booking.phoneNumber
        holder.queriesTextView.text = booking.queries
    }

    override fun getItemCount() = bookings.size
}

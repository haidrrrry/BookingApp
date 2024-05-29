package com.bhelllr.eventsapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bhelllr.eventsapp.models.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.hellllr.bookingapp.R
import com.hellllr.bookingapp.adapters.BookingAdapter

class UserBookingsFragment : Fragment() {

    private lateinit var bookingsRecyclerView: RecyclerView
    private lateinit var bookingsAdapter: BookingAdapter
    private lateinit var bookings: MutableList<Booking>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_bookings, container, false)

        bookingsRecyclerView = view.findViewById(R.id.userBookingsRecyclerView)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(context)
        bookings = mutableListOf()
        bookingsAdapter = BookingAdapter(bookings)
        bookingsRecyclerView.adapter = bookingsAdapter

        loadUserBookings()

        return view
    }

    private fun loadUserBookings() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("bookings")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                bookings.clear()
                for (document in querySnapshot.documents) {
                    val booking = document.toObject(Booking::class.java)
                    if (booking != null) {
                        bookings.add(booking)
                        Log.d("UserBookingsFragment", "Booking: ${booking.userId}, ${booking.postId}, ${booking.timestamp}, ${booking.phoneNumber}, ${booking.queries}")
                    }
                }
                bookingsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("UserBookingsFragment", "Error fetching bookings: ", e)
            }
    }
}



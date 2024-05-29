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

class PostQueriesFragment : Fragment() {

    private lateinit var queriesRecyclerView: RecyclerView
    private lateinit var queriesAdapter: BookingAdapter
    private lateinit var bookings: MutableList<Booking>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_queries, container, false)

        queriesRecyclerView = view.findViewById(R.id.postQueriesRecyclerView)
        queriesRecyclerView.layoutManager = LinearLayoutManager(context)
        bookings = mutableListOf()
        queriesAdapter = BookingAdapter(bookings)
        queriesRecyclerView.adapter = queriesAdapter

        firestore = FirebaseFirestore.getInstance()

        loadPostQueries()

        return view
    }

    private fun loadPostQueries() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Reference to the bookings collection
        val bookingsRef = firestore.collection("bookings")

        // Get bookings where the current user is the booker
//        bookingsRef.whereEqualTo("userId", userId).get()
//            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
//                bookings.clear()
//                for (document in querySnapshot.documents) {
//                    val booking = document.toObject(Booking::class.java)
//                    if (booking != null) {
//                        bookings.add(booking)
//                        Log.d("PostQueriesFragment", "Booking (user booked): ${booking.userId}, ${booking.postId}, ${booking.timestamp}, ${booking.phoneNumber}, ${booking.queries}")
//                    }
//                }
//                queriesAdapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Log.e("PostQueriesFragment", "Error fetching bookings (user booked): ", e)
//            }

        // Also get bookings where the current user is the post owner
        bookingsRef.whereEqualTo("postId", userId).get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document in querySnapshot.documents) {
                    val booking = document.toObject(Booking::class.java)
                    if (booking != null && booking !in bookings) {
                        bookings.add(booking)
                        Log.d("PostQueriesFragment", "Booking (user posted): ${booking.userId}, ${booking.postId}, ${booking.timestamp}, ${booking.phoneNumber}, ${booking.queries}")
                    }
                }
                queriesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("PostQueriesFragment", "Error fetching bookings (user posted): ", e)
            }
    }
}

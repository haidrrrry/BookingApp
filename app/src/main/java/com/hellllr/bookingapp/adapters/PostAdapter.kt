package com.bhelllr.eventsapp.adapters

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bhelllr.eventsapp.models.Booking

import com.bhelllr.eventsapp.models.Post
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hellllr.bookingapp.R

import java.util.*

class PostAdapter(private val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val listeners = mutableMapOf<String, ListenerRegistration>()

    init {
        // Initialize real-time listeners for each post
        posts.forEach { post ->
            val listener = firestore.collection("ratings").document(post.userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("PostAdapter", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val ratings = snapshot.get("ratings") as? Map<String, Float>
                        if (ratings != null) {
                            post.ratings.clear()
                            post.ratings.putAll(ratings)
                            post.averageRating = calculateAverageRating(ratings)
                            Log.d("PostAdapter", "Updated ratings: ${post.ratings}, Average: ${post.averageRating}")
                            notifyDataSetChanged() // Update the UI
                        }
                    }
                }
            listeners[post.userId] = listener
        }

        // Load total rating for each post
        loadTotalRatings()
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val postDescriptionTextView: TextView = itemView.findViewById(R.id.postDescriptionTextView)
        val time: TextView = itemView.findViewById(R.id.time)
        val rateButton: Button = itemView.findViewById(R.id.rateButton)
        val bookButton: Button = itemView.findViewById(R.id.Bookthis) // Add booking button in your layout
        val postAverageRating: TextView = itemView.findViewById(R.id.postAverageRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postDescriptionTextView.text = post.description
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = post.timestamp
        val dateString = android.text.format.DateFormat.format("MMM dd, yyyy hh:mm:ss a", calendar).toString()
        holder.time.text = dateString
        holder.postAverageRating.text = "Rating: ${post.averageRating}"

        Glide.with(holder.postImageView.context)
            .load(post.imageUrl)
            .into(holder.postImageView)

        holder.rateButton.setOnClickListener {
            showRatingDialog(holder.itemView.context, post)
        }

        holder.bookButton.setOnClickListener {
            showBookingDialog(holder.itemView.context, post)
        }
    }

    override fun getItemCount() = posts.size

    private fun showRatingDialog(context: Context, post: Post) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Submitting rating...")
        progressDialog.setCancelable(false)

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_rate_post)
        dialog.setCancelable(true)

        val ratingBar = dialog.findViewById<RatingBar>(R.id.ratingBar)
        val submitButton = dialog.findViewById<Button>(R.id.submitRatingButton)

        submitButton.setOnClickListener {
            val rating = ratingBar.rating
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener

            progressDialog.show() // Show progress dialog

            // Create or update the rating for the current user
            val postRef = firestore.collection("ratings").document(post.userId)

            postRef.get().addOnSuccessListener { document ->
                val ratings = document.get("ratings") as? MutableMap<String, Float> ?: mutableMapOf()
                ratings[userId] = rating

                postRef.set(mapOf("ratings" to ratings))
                    .addOnSuccessListener {
                        post.ratings[userId] = rating
                        post.averageRating = calculateAverageRating(ratings)
                        notifyDataSetChanged()  // Notify the adapter to refresh the view
                        progressDialog.dismiss() // Dismiss progress dialog
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PostAdapter", "Failed to set ratings: ${e.message}")
                        progressDialog.dismiss() // Dismiss progress dialog
                    }
            }.addOnFailureListener { e ->
                Log.e("PostAdapter", "Failed to get document: ${e.message}")
                progressDialog.dismiss() // Dismiss progress dialog
            }
        }

        dialog.show()
    }

    private fun showBookingDialog(context: Context, post: Post) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Booking appointment...")
        progressDialog.setCancelable(false)

        val dialog = Dialog(context)
        dialog.setContentView(R.layout.layout_book_appointment)
        dialog.setCancelable(true)

        val phoneNumberEditText = dialog.findViewById<EditText>(R.id.phoneNumberEditText)
        val queriesEditText = dialog.findViewById<EditText>(R.id.queriesEditText)
        val bookButton = dialog.findViewById<Button>(R.id.bookAppointmentButton)

        bookButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val phoneNumber = phoneNumberEditText.text.toString().trim()
            val queries = queriesEditText.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                phoneNumberEditText.error = "Phone number is required"
                return@setOnClickListener
            }

            progressDialog.show() // Show progress dialog

            val booking = Booking(
                userId = userId,
                postId = post.userId,
                timestamp = System.currentTimeMillis(),
                phoneNumber = phoneNumber,
                queries = queries
            )

            // Reference to the specific post's bookings collection
            val bookingsRef = firestore.collection("bookings").document(post.userId)

            bookingsRef.set(booking)
                .addOnSuccessListener {
                    post.bookings[userId] = booking
                    notifyDataSetChanged()  // Notify the adapter to refresh the view
                    progressDialog.dismiss() // Dismiss progress dialog
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Log.e("PostAdapter", "Failed to set booking: ${e.message}")
                    progressDialog.dismiss() // Dismiss progress dialog
                }
        }

        dialog.show()
    }


    private fun calculateAverageRating(ratings: Map<String, Float>): Float {
        val totalRating = ratings.values.sum()
        return if (ratings.isNotEmpty()) totalRating / ratings.size else 0f
    }

    private fun loadTotalRatings() {
        firestore.collection("ratings").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val postId = document.id
                    val ratings = document.get("ratings") as? Map<String, Float>
                    if (ratings != null) {
                        val post = posts.find { it.userId == postId }
                        if (post != null) {
                            post.ratings.clear()
                            post.ratings.putAll(ratings)
                            post.averageRating = calculateAverageRating(ratings)
                            notifyDataSetChanged() // Update the UI
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("PostAdapter", "Error getting documents: ", exception)
            }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        // Remove listeners to avoid memory leaks
        listeners.forEach { (_, listener) -> listener.remove() }
    }
}

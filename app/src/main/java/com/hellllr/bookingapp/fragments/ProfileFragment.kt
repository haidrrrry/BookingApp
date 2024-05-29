package com.bhelllr.eventsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hellllr.bookingapp.R

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nameTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var adreessTv: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameTextView = view.findViewById(R.id.nameTextView)
        bioTextView = view.findViewById(R.id.bioTextView)
        adreessTv = view.findViewById(R.id.adreessTv)

        loadUserProfile()

        return view
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("usersbio").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name")
                        val bio = document.getString("bio")
                        val adrees = document.getString("adress")

                        nameTextView.text = "Name: $name"
                        bioTextView.text = "Bio: $bio"
                        adreessTv.text = "Address: $adrees"
                    } else {
                        Toast.makeText(requireContext(), "No profile found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

package com.hellllr.bookingapp

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhelllr.eventsapp.adapters.PostAdapter
import com.bhelllr.eventsapp.models.Post

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hellllr.bookingapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var postAdapter: PostAdapter


    private var selectedImageUri: Uri? = null
    private val posts = mutableListOf<Post>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentHomeBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

       binding. recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(posts)
        binding. recyclerView.adapter = postAdapter

        loadPosts()
        return binding.root

    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->
                posts.clear()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    posts.add(post)
                }
                postAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
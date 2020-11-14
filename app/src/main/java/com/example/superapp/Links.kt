package com.example.superapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.link_add_bottom_sheet.*
import kotlinx.android.synthetic.main.link_item.*
import kotlinx.android.synthetic.main.link_item.view.*
import www.sanju.motiontoast.MotionToast
import java.util.*


class Links : AppCompatActivity(), (PostModel) -> Unit {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var bottomSheetLayout: ConstraintLayout
    lateinit var db: FirebaseFirestore

    private val firebaseRepo: FirebaseRepo = FirebaseRepo()
    private var postList: List<PostModel> = ArrayList()

    private val postListAdapter: PostAdapter = PostAdapter(postList, this)

    lateinit var documName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        supportActionBar?.hide()
        bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        db = FirebaseFirestore.getInstance()


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postListAdapter

        if (firebaseRepo.getUser() == null) {
            firebaseRepo.createUser().addOnCompleteListener {
                if (it.isSuccessful) {
                    loadPostData()
                } else {
                    Log.d("TAG", "Error: ${it.exception!!.message}")
                }
            }
        } else {
            loadPostData()
        }

        addLink.setOnClickListener {
            val city = Link(
                nameLink.text.toString(),
                addressLink.text.toString(),
                commentLink.text.toString(),
                "0",
                Timestamp(Date()).toString()
            )

            db.collection("SuperApp")
                .document(Timestamp(Date()).toString())
                .set(city)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        addLinkButton.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        addLinkButton.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        })

        addLinkButton.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        changeLink.setOnClickListener {
            val sfDocRef = db.collection("SuperApp").document(documName)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(sfDocRef)

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                val newPopulation = snapshot.getDouble("Hello APP")
                transaction.update(sfDocRef, "linkName", nameLink.text.toString())
                transaction.update(sfDocRef, "addressLink", addressLink.text.toString())
                transaction.update(sfDocRef, "commentLink", commentLink.text.toString())
                // Success
                null
            }.addOnSuccessListener { Log.d("TAG", "Transaction success!") }
                .addOnFailureListener { e -> Log.w("TAG", "Transaction failure.", e) }
        }
    }

    private fun loadPostData() {
        firebaseRepo.getPostList().addOnCompleteListener {
            if (it.isSuccessful) {
                postList = it.result!!.toObjects(PostModel::class.java)
                postListAdapter.postListItem = postList
                postListAdapter.notifyDataSetChanged()
                countLinks.text = postList.size.toString()
                Log.d("TAG", "Error: ${postList}")
            } else {
                Log.d("TAG", "Error: ${it.exception!!.message}")
            }
        }
    }



    override fun invoke(postModel: PostModel) {

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        addLink.visibility = View.GONE
        changeLink.visibility = View.VISIBLE
        nameLink.setText(postModel.linkName)
        addressLink.setText(postModel.addressLink)
        commentLink.setText(postModel.commentLink)
        documName = postModel.docName


        //Toast
//        MotionToast.darkToast(this,
//            "Hurray success 😍",
//            "Upload Completed successfully!",
//            MotionToast.TOAST_NO_INTERNET,
//            MotionToast.GRAVITY_BOTTOM,
//            MotionToast.LONG_DURATION,
//            ResourcesCompat.getFont(this,R.font.helvetica_regular))
    }

    fun del() {
        this.db.collection("SuperApp").document(documName)
            .delete()
            .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("TAG", "Error deleting document", e) }

    }

    data class Link(
        val linkName: String? = null,
        val addressLink: String? = null,
        val commentLink: String? = null,
        val id: String? = null,
        val docName: String? = null
    )
}
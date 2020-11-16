package com.example.superapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.Timestamp
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.link_add_bottom_sheet.*
import kotlinx.android.synthetic.main.link_item.view.*
import java.util.*


class Links : AppCompatActivity(), (PostModel, View) -> Unit {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var bottomSheetLayout: ConstraintLayout
    lateinit var db: FirebaseFirestore

    private val firebaseRepo: FirebaseRepo = FirebaseRepo()
    private var postList: List<PostModel> = ArrayList()

    private val postListAdapter: PostAdapter = PostAdapter(postList, this)

    lateinit var documName: String

    lateinit var addDocName: Timestamp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        supportActionBar?.hide()
        bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        db = FirebaseFirestore.getInstance()



        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postListAdapter

        allLinks.setOnClickListener {
            db.disableNetwork()
                .addOnCompleteListener { Log.d("2", "Error: ") }
                .addOnFailureListener { e -> Log.w("2", "Error writing document", e) }
            firebaseRepo.firebaseFirestore.disableNetwork()
                .addOnCompleteListener { Log.d("2", "Error: ") }
                .addOnFailureListener { e -> Log.w("2", "Error writing document", e) }
        }
        countLinks.setOnClickListener {
            db.enableNetwork()
                .addOnSuccessListener { Log.d("3", "Error: ") }
                .addOnFailureListener { e -> Log.w("3", "Error writing document", e) }
            firebaseRepo.firebaseFirestore.enableNetwork()
                .addOnSuccessListener { Log.d("3", "Error: ") }
                .addOnFailureListener { e -> Log.w("3", "Error writing document", e) }
        }

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

        db.collection("SuperApp")
            .whereEqualTo("id", "0")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                val cities = ArrayList<String>()
                for (doc in value!!) {
                    postList = value.toObjects(PostModel::class.java)
                    postListAdapter.postListItem = postList
                    postListAdapter.notifyDataSetChanged()
                }
                Log.d("TAG", "Current cites in CA: $cities")
            }

        addLink.setOnClickListener {
            addDocName = Timestamp(Date())
            val city = Link(
                nameLink.text.toString(),
                addressLink.text.toString(),
                commentLink.text.toString(),
                "0",
                addDocName.toString()
            )

            db.collection("SuperApp")
                .document(addDocName.toString())
                .set(city)
                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            postListAdapter.notifyDataSetChanged()
        }


        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        addLinkButton.visibility = View.VISIBLE
                        recyclerView.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                        //addLinkButton.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                    }
                }
            }
        })

        addLinkButton.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


        changeLink.setOnClickListener {
            val sfDocRef = db.collection("SuperApp").document(documName)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(sfDocRef)

                // Note: this could be done without a transaction
                //       by updating the population using FieldValue.increment()
                transaction.update(sfDocRef, "linkName", nameLink.text.toString())
                transaction.update(sfDocRef, "addressLink", addressLink.text.toString())
                transaction.update(sfDocRef, "commentLink", commentLink.text.toString())
                // Success
                null
            }.addOnSuccessListener {
                Log.d("Update", "Transaction success! + ${documName}")
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                addLink.visibility = View.VISIBLE
                changeLink.visibility = View.GONE
            }
                .addOnFailureListener { e ->
                    Log.w(
                        "Update",
                        "Transaction failure. + ${documName}",
                        e
                    )
                }
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


    override fun invoke(postModel: PostModel, itemView: View) {

        when (itemView) {
            itemView.goLinkButton -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postModel.addressLink))
                startActivity(browserIntent)
            }
            itemView.delLinkButton -> {
                db.collection("SuperApp").document(postModel.docName)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(
                            "Delete",
                            "DocumentSnapshot successfully deleted! + ${postModel.docName}"
                        )
                    }
                    .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
            }
            itemView.linkItemCard -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                addLink.visibility = View.GONE
                changeLink.visibility = View.VISIBLE
                nameLink.setText(postModel.linkName)
                addressLink.setText(postModel.addressLink)
                commentLink.setText(postModel.commentLink)
                documName = postModel.docName


            }
        }


        //Toast
//        MotionToast.darkToast(this,
//            "${postModel.docName}",
//            "Upload Completed successfully!",
//            MotionToast.TOAST_NO_INTERNET,
//            MotionToast.GRAVITY_BOTTOM,
//            MotionToast.LONG_DURATION,
//            ResourcesCompat.getFont(this,R.font.helvetica_regular))
    }


    data class Link(
        val linkName: String? = null,
        val addressLink: String? = null,
        val commentLink: String? = null,
        val id: String? = null,
        val docName: String? = null
    )

}
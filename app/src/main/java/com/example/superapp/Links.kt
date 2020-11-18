package com.example.superapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superapp.dagger.DaggerComponent
import com.example.superapp.dagger.DaggerDaggerComponent
import com.example.superapp.dagger.DaggerDaggerComponent.create
import com.example.superapp.firestore.database
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.link_add_bottom_sheet.*
import kotlinx.android.synthetic.main.link_item.*
import kotlinx.android.synthetic.main.link_item.view.*
import soup.neumorphism.NeumorphFloatingActionButton
import soup.neumorphism.NeumorphImageButton
import www.sanju.motiontoast.MotionToast
import java.util.*
import javax.inject.Inject


class Links : AppCompatActivity() {
    private val POST_TYPE_DESC: Int = 0
    private val POST_TYPE_IMAGE: Int = 1

    @Inject
    lateinit var db: database

    //Объявляем BottomSheetBehavior
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    //Объявляем view слоя
    lateinit var bottomSheetLayout: ConstraintLayout

    //Объявляем базу данных
    //lateinit var db: FirebaseFirestore
    lateinit var adapter: FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>

    //Объявляем класс проверки пользователя
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()

    //Объявляем имя документа над которым идет работа (создание удаление редактирование)
    lateinit var documName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        DaggerDaggerComponent.create().inject(this)
        //init()
        //Скрываем ActionBar
        supportActionBar?.hide()
        //Инициализация всего что связано с BottomSheet
        bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        //Инициализация recyclerView и привязка адаптера
        recyclerView.layoutManager = LinearLayoutManager(this)

        allLinks.setOnClickListener {
            db.db.disableNetwork()
                .addOnCompleteListener { Log.d("2", "Error: ") }
                .addOnFailureListener { e -> Log.w("2", "Error writing document", e) }
        }
        countLinks.setOnClickListener {
            db.db.enableNetwork()
                .addOnSuccessListener { Log.d("3", "Error: ") }
                .addOnFailureListener { e -> Log.w("3", "Error writing document", e) }
        }

        //Проверяем есть ли пользователь с таким номером телефона, если да загружаем данные,
        //если нет регистрируем
        if (firebaseRepo.getUser() == null) else getFriendList()

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

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
    }

    fun ErrorToast(title: String, message: String) {
        MotionToast.darkToast(
            this,
            title,
            message,
            MotionToast.TOAST_ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }

    private fun getFriendList() {
        val query: Query = db.db.collection("SuperApp").orderBy("id", Query.Direction.DESCENDING)
        val response: FirestoreRecyclerOptions<FriendsResponse> =
            FirestoreRecyclerOptions.Builder<FriendsResponse>()
                .setQuery(query, FriendsResponse::class.java)
                .build()
        adapter = object : FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>(response) {
            override fun onBindViewHolder(
                holder: FriendsHolder,
                position: Int,
                model: FriendsResponse
            ) {
                countLinks.text = adapter.itemCount.toString()
                holder.textlinkNameUI.text = model.linkName
                holder.linkAddress.text = model.addressLink
                holder.textlinkComment.text = model.commentLink
                Glide.with(applicationContext)
                    .load("https://www.google.com/s2/favicons?sz=64&domain_url=" + model.addressLink)
                    .circleCrop()
                    .into(holder.imageView)
                holder.itemView.setOnClickListener {
                    recyclerView.let {

                        if (holder.commentLink.isVisible) {
                            holder.commentLink.visibility = View.GONE
                        } else {
                            holder.commentLink.visibility = View.VISIBLE

                        }
                    }
                }
                holder.fabMenu.setOnClickListener {
                    if (holder.fabGoLink.isVisible) {
                        holder.fabGoLink.visibility = View.GONE
                        holder.fabEditLink.visibility = View.GONE
                        holder.fabDelButton.visibility = View.GONE
                    } else {
                        holder.fabGoLink.visibility = View.VISIBLE
                        holder.fabEditLink.visibility = View.VISIBLE
                        holder.fabDelButton.visibility = View.VISIBLE
                    }
                }

                holder.fabGoLink.setOnClickListener {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(model.addressLink))
                    startActivity(browserIntent)
                }
                holder.fabEditLink.setOnClickListener {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    addLink.visibility = View.GONE
                    changeLink.visibility = View.VISIBLE
                    nameLink.setText(model.linkName)
                    addressLink.setText(model.addressLink)
                    commentLink.setText(model.commentLink)
                    documName = model.docName
                }
                holder.fabDelButton.setOnClickListener {
                    db.db.collection("SuperApp").document(model.docName)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(
                                "Delete",
                                "DocumentSnapshot successfully deleted! + ${model.docName}"
                            )
                        }
                        .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
                }

                changeLink.setOnClickListener {
                    val washingtonRef = db.db.collection("SuperApp").document(documName)
                    washingtonRef.update(
                        mapOf(
                            "linkName" to nameLink.text.toString(),
                            "addressLink" to addressLink.text.toString(),
                            "commentLink" to commentLink.text.toString()
                        )
                    ).addOnSuccessListener {}
                        .addOnFailureListener {}
                }

            }


            override fun onCreateViewHolder(group: ViewGroup, i: Int): FriendsHolder {
                if (i == POST_TYPE_DESC) {
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.link_item, group, false)
                    return FriendsHolder(view)
                } else {
                    val view: View = LayoutInflater.from(group.context)
                        .inflate(R.layout.link_item1, group, false)
                    return FriendsHolder(view)
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("error", e.message!!)
            }

            override fun getItemViewType(position: Int): Int {
                return if (adapter.getItem(position).id.toInt() == 0) {
                    POST_TYPE_DESC
                } else {
                    POST_TYPE_IMAGE
                }
            }
        }
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter

    }


    class FriendsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView!!) {

        var textlinkNameUI: TextView = itemView.findViewById(R.id.linkName)

        var imageView: ImageView = itemView.findViewById(R.id.imageView4)

        var linkAddress: TextView = itemView.findViewById(R.id.linkAddress)

        var textlinkComment: TextView = itemView.findViewById(R.id.commentTextView2)

        var fabMenu: NeumorphImageButton = itemView.findViewById(R.id.fabMenu)

        var fabEditLink: NeumorphImageButton = itemView.findViewById(R.id.fabEditLink)

        var fabDelButton: NeumorphImageButton = itemView.findViewById(R.id.fabDelButton)

        var fabGoLink: NeumorphImageButton = itemView.findViewById(R.id.fabGoLink)

        var commentLink: TextView = itemView.findViewById(R.id.commentTextView2)
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


}
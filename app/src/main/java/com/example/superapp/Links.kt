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


class Links : AppCompatActivity(), (PostModel, View) -> Unit {

    //Объявляем BottomSheetBehavior
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    //Объявляем view слоя
    lateinit var bottomSheetLayout: ConstraintLayout

    //Объявляем базу данных
    //lateinit var db: FirebaseFirestore
    lateinit var db: FirebaseFirestore
    lateinit var adapter: FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>

    //Объявляем класс проверки пользователя
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()

    //Объявляем лист для загрузки данных
    private var postList: List<PostModel> = ArrayList()

    //Объявляем Адаптер для RecyclerView
    //private val postListAdapter: PostAdapter = PostAdapter(postList, this)

    //Объявляем имя документа над которым идет работа (создание удаление редактирование)
    lateinit var documName: String

    //Объявляем имя документа для верного сохранения
    lateinit var addDocName: Timestamp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)
        init()
        //Скрываем ActionBar
        supportActionBar?.hide()
        //Инициализация всего что связано с BottomSheet
        bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        //Инициализация базы данных
        //db = FirebaseFirestore.getInstance()
        //Инициализация recyclerView и привязка адаптера
        recyclerView.layoutManager = LinearLayoutManager(this)
        //recyclerView.adapter = postListAdapter

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

        //Проверяем есть ли пользователь с таким номером телефона, если да загружаем данные,
        //если нет регистрируем
        if (firebaseRepo.getUser() == null) {
            firebaseRepo.createUser().addOnCompleteListener {
                if (it.isSuccessful) { getFriendList()}
                else { ErrorToast("Ошибка", it.exception!!.message.toString())}
            }
        } else {
            getFriendList()
        }

        //Создаем слушателя и подписываемся на изменения БД в realTime
//        db.collection("SuperApp")
//            .whereEqualTo("id", "0")
//            .addSnapshotListener { value, e ->
//                if (e != null) {
//                    ErrorToast("Error",e.message.toString())
//                    return@addSnapshotListener
//                }
//                for (doc in value!!) {
//                    postList = value.toObjects(PostModel::class.java)
//                    postListAdapter.postListItem = postList
//                    postListAdapter.notifyDataSetChanged()
//                }
//            }



//        addLink.setOnClickListener {
//            addDocName = Timestamp(Date())
//            val city = Link(
//                nameLink.text.toString(),
//                addressLink.text.toString(),
//                commentLink.text.toString(),
//                "0",
//                addDocName.toString()
//            )
//
//            db.collection("SuperApp")
//                .document(addDocName.toString())
//                .set(city)
//                .addOnSuccessListener { Log.d("TAG", "DocumentSnapshot successfully written!") }
//                .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
//
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//        }


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

            val washingtonRef = db.collection("SuperApp").document(documName)

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

//    private fun loadPostData() {
//        firebaseRepo.getPostList().addOnCompleteListener {
//            if (it.isSuccessful) {
//                postList = it.result!!.toObjects(PostModel::class.java)
//                postListAdapter.postListItem = postList
//                postListAdapter.notifyDataSetChanged()
//                countLinks.text = postList.size.toString()
//                Log.d("TAG", "Error: ${postList}")
//            } else {
//                Log.d("TAG", "Error: ${it.exception!!.message}")
//            }
//        }
//    }


    override fun invoke(postModel: PostModel, itemView: View) {

//        when (itemView) {
//            itemView.fabGoLink -> {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(postModel.addressLink))
//                startActivity(browserIntent)
//            }
//            itemView.fabDelButton -> {
//                db.collection("SuperApp").document(postModel.docName)
//                    .delete()
//                    .addOnSuccessListener {
//                        Log.d(
//                            "Delete",
//                            "DocumentSnapshot successfully deleted! + ${postModel.docName}"
//                        )
//                    }
//                    .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
//            }
//            itemView.fabEditLink -> {
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                addLink.visibility = View.GONE
//                changeLink.visibility = View.VISIBLE
//                nameLink.setText(postModel.linkName)
//                addressLink.setText(postModel.addressLink)
//                commentLink.setText(postModel.commentLink)
//                documName = postModel.docName
//            }
//        }
    }

    fun ErrorToast(title: String, message: String){
        MotionToast.darkToast(this,
            title,
            message,
            MotionToast.TOAST_ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.helvetica_regular))
    }


//    data class Link(
//        val linkName: String? = null,
//        val addressLink: String? = null,
//        val commentLink: String? = null,
//        val id: String? = null,
//        val docName: String? = null
//    )

    private fun init() {
        //recyclerView.layoutManager = GridLayoutManager(applicationContext, 2)
        db = FirebaseFirestore.getInstance()
    }

    private fun getFriendList() {
        val query: Query = db.collection("SuperApp").orderBy("id", Query.Direction.DESCENDING)
        val response: FirestoreRecyclerOptions<FriendsResponse> = FirestoreRecyclerOptions.Builder<FriendsResponse>()
            .setQuery(query, FriendsResponse::class.java)
            .build()
        adapter = object : FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>(response) {
            override fun onBindViewHolder(
                holder: FriendsHolder,
                position: Int,
                model: FriendsResponse
            ) {

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
                    db.collection("SuperApp").document(model.docName)
                        .delete()
                        .addOnSuccessListener {
                            Log.d(
                                "Delete",
                                "DocumentSnapshot successfully deleted! + ${model.docName}"
                            )
                        }
                        .addOnFailureListener { e -> Log.w("Delete", "Error deleting document", e) }
                }

            }


            override fun onCreateViewHolder(group: ViewGroup, i: Int): FriendsHolder {
                val view: View = LayoutInflater.from(group.context)
                    .inflate(R.layout.link_item, group, false)
                return FriendsHolder(view)
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.e("error", e.message!!)
            }
        }
        adapter.notifyDataSetChanged()
        recyclerView.adapter = adapter
    }

    class FriendsHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView!!) {

        var textlinkNameUI: TextView = itemView.findViewById(R.id.linkName)

        var imageView: ImageView  = itemView.findViewById(R.id.imageView4)

        var linkAddress: TextView = itemView.findViewById(R.id.linkAddress)

        var textlinkComment: TextView = itemView.findViewById(R.id.commentTextView2)

        var fabMenu: NeumorphImageButton = itemView.findViewById(R.id.fabMenu)

        var fabEditLink: NeumorphImageButton = itemView.findViewById(R.id.fabEditLink)

        var fabDelButton: NeumorphImageButton = itemView.findViewById(R.id.fabDelButton)

        var fabGoLink: NeumorphImageButton = itemView.findViewById(R.id.fabGoLink)

        var commentLink: TextView = itemView.findViewById(R.id.commentTextView2)

        var itemCard: CardView = itemView.findViewById(R.id.linkItemCard)
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
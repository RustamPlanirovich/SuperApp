package com.example.superapp

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.superapp.dagger.DaggerDaggerComponent.create
import com.example.superapp.firestore.database
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.activity_links.*
import kotlinx.android.synthetic.main.link_add_bottom_sheet.*
import kotlinx.android.synthetic.main.link_filter_bottom_sheet.*
import kotlinx.android.synthetic.main.second_special_selected_calendar_item.view.*
import net.vrgsoft.layoutmanager.RollingLayoutManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.random.Random


class Links : AppCompatActivity() {
    private val POST_TYPE_DESC: Int = 0
    private val POST_TYPE_IMAGE: Int = 1
    private val POST_TYPE_STICKY: Int = 2
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0

    //Объявляем базу данных
    @Inject
    lateinit var db: database

    @Inject
    lateinit var errorToast: database

    //Объявляем BottomSheetBehavior
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetBehavior1: BottomSheetBehavior<ConstraintLayout>

    //Объявляем view слоя
    lateinit var bottomSheetLayout: ConstraintLayout
    lateinit var bottomSheetLayout1: ConstraintLayout

    lateinit var adapter: FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>

    //Объявляем класс проверки пользователя
    private val firebaseRepo: FirebaseRepo = FirebaseRepo()

    //Объявляем имя документа над которым идет работа (создание удаление редактирование)
    lateinit var docName: String

    val dateStack: MutableList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_links)

        // set current date to calendar and current month to currentMonth variable
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // enable white status bar with black icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }


        // calendar view manager is responsible for our displaying logic
        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                // if item is selected we return this layout items
                // in this example. monday, wednesday and friday will have special item views and other days
                // will be using basic item view
                return if (isSelected)
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_selected_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_selected_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_selected_calendar_item
                        else -> R.layout.selected_calendar_item
                    }
                else
                // here we return items which are not selected
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        Calendar.MONDAY -> R.layout.first_special_calendar_item
                        Calendar.WEDNESDAY -> R.layout.second_special_calendar_item
                        Calendar.FRIDAY -> R.layout.third_special_calendar_item
                        else -> R.layout.calendar_item
                    }

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

            }
        }

        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {

            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                tvDate.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getDayNumber(date)} "
                tvDay.text = DateUtils.getDayName(date)
                val cal = Calendar.getInstance()
                cal.time = date
                val createDateAndTime = SimpleDateFormat("dd M yyyy")
                val currentDateAndTime = createDateAndTime.format(date)


                if (isSelected == false) {
                    dateStack.remove(currentDateAndTime)
                    var i = 0
                    if (i < dateStack.size) {

                        val newQuery = db.firestoreDb.collection("SuperApp")
                            .orderBy("createTime")
                            .startAt("${dateStack[i]}")
                            .endAt("${dateStack[i]}" + "\uf8ff")

                        // Make new options
                        val newOptions = FirestoreRecyclerOptions.Builder<FriendsResponse>()
                            .setQuery(newQuery, FriendsResponse::class.java)
                            .build()

                        // Change options of adapter.
                        adapter.updateOptions(newOptions)
                        ++i
                    } else {
                        val newQuery = db.firestoreDb.collection("SuperApp")
                            .orderBy("id")

                        // Make new options
                        val newOptions = FirestoreRecyclerOptions.Builder<FriendsResponse>()
                            .setQuery(newQuery, FriendsResponse::class.java)
                            .build()

                        // Change options of adapter.
                        adapter.updateOptions(newOptions)
                    }
                }
                super.whenSelectionChanged(isSelected, position, date)
            }
        }

        // selection manager is responsible for managing selection
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                val createDateAndTime = SimpleDateFormat("dd M yyyy")
                val currentDateAndTime = createDateAndTime.format(date)

                if (dateStack.contains("$currentDateAndTime")) {

                } else {
                    dateStack.add("$currentDateAndTime")
                }


                var i = 0
                while (i < dateStack.size) {
                    Log.d("lof", "${dateStack}" + "${dateStack.size}")

                    val newQuery = db.firestoreDb.collection("SuperApp")
                        .orderBy("createTime")
                        .startAt("${dateStack[i]}")
                        .endAt("${dateStack[i]}" + "\uf8ff")

                    // Make new options
                    val newOptions = FirestoreRecyclerOptions.Builder<FriendsResponse>()
                        .setQuery(newQuery, FriendsResponse::class.java)
                        .build()
                    save.setOnClickListener {
                        // Change options of adapter.
                        adapter.updateOptions(newOptions)
                    }
                    ++i
                }


                // in this example sunday and saturday can't be selected, others can
                return when (cal[Calendar.DAY_OF_WEEK]) {
//                    Calendar.SATURDAY -> false
//                    Calendar.SUNDAY -> false
                    else -> true
                }
            }
        }

        // here we init our calendar, also you can set more properties if you haven't specified in XML layout
        val singleRowCalendar = main_single_row_calendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            setDates(getFutureDatesOfCurrentMonth())
            includeCurrentDate = true
            deselection = true
            multiSelection = true
            init()
        }

        btnRight.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfNextMonth())
        }

        btnLeft.setOnClickListener {
            singleRowCalendar.setDates(getDatesOfPreviousMonth())
        }


        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            }
        }

        //Инициализация Dagger
        create().inject(this)
        //Скрываем ActionBar
        supportActionBar?.hide()
        //Инициализация всего что связано с BottomSheet
        bottomSheetLayout = findViewById<ConstraintLayout>(R.id.bottomSheet)
        bottomSheetLayout1 = findViewById(R.id.bottomSheetFilter)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheetLayout1)
        //Инициализация recyclerView и привязка адаптера

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && addLinkButton.isVisible) {
                    addLinkButton.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    addLinkButton.visibility = View.VISIBLE
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        val rollingLayoutManager = RollingLayoutManager(this)
        recyclerView.layoutManager = rollingLayoutManager

        searchEditText.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    changeQuery(s.toString())
                }
            })


        //Выключаем доступ базы данных в интернет
        allLinks.setOnClickListener()
        {
            db.firestoreDb.disableNetwork()
                .addOnSuccessListener {
                    db.successToast.SuccessToast(
                        "Success",
                        "The database is in offline mode",
                        this
                    )
                }
                .addOnFailureListener { e -> db.errorToast.ErrorToast("Error", "$e", this) }
        }
        //Включаем доступ базы данных в интернет
        countLinks.setOnClickListener()
        {
            db.firestoreDb.enableNetwork()
                .addOnSuccessListener {
                    db.successToast.SuccessToast(
                        "Success",
                        "The database is in online mode",
                        this
                    )
                }
                .addOnFailureListener { e -> db.errorToast.ErrorToast("Error", "$e", this) }
        }

        //Проверяем есть ли пользователь с таким номером телефона, если да загружаем данные,
        //если нет регистрируем
        if (firebaseRepo.getUser() == null) else {
            getFriendList()
        }


        bottomSheetBehavior.addBottomSheetCallback(
            object :
                BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            addLinkButton.visibility = View.VISIBLE
                            closeAddLinkButton.visibility = View.GONE
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            addLinkButton.visibility = View.GONE
                            closeAddLinkButton.visibility = View.VISIBLE
                        }
                    }
                }
            })

        closeAddLinkButton.setOnClickListener()
        {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            nameLink.setText("")
            addressLink.setText("")
            commentLink.setText("")
        }
        addLinkButton.setOnClickListener()
        {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        filterLink.setOnClickListener {
            if (bottomSheetBehavior1.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior1.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bottomSheetBehavior1.state = BottomSheetBehavior.STATE_EXPANDED
//                var anim = AnimationUtils.loadAnimation(this,R.anim.animation)
//                addLinkButton.startAnimation(anim)
            }
        }

        addLink.setOnClickListener()
        {
            val createdTime = Timestamp(Date())
            val createDateAndTime = SimpleDateFormat("dd M yyyy")
            val currentDateAndTime = createDateAndTime.format(Date())
            val linkForSave = hashMapOf(
                "linkName" to nameLink.text.toString(),
                "addressLink" to addressLink.text.toString(),
                "commentLink" to commentLink.text.toString(),
                "id" to "0",
                "docName" to createdTime.toString(),
                "createTime" to currentDateAndTime
            )
            db.firestoreDb.collection("SuperApp").document(createdTime.toString())
                .set(linkForSave)
                .addOnSuccessListener { }
                .addOnFailureListener { }
        }
    }


    fun getFriendList() {

        val query: Query =
            db.firestoreDb.collection("SuperApp")
                .orderBy("id", Query.Direction.DESCENDING)
        val response: FirestoreRecyclerOptions<FriendsResponse> =
            FirestoreRecyclerOptions.Builder<FriendsResponse>()
                .setQuery(query, FriendsResponse::class.java)
                .build()

        adapter =
            object : FirestoreRecyclerAdapter<FriendsResponse, FriendsHolder>(response) {
                override fun onBindViewHolder(
                    holder: FriendsHolder,
                    position: Int,
                    model: FriendsResponse
                ) {
                    countLinks.text = adapter.itemCount.toString()
                    holder.textlinkNameUI.text = model.linkName
                    holder.linkAddress.text = model.addressLink
                    holder.createDateAndTime.text = model.createTime

                    Glide.with(applicationContext)
                        .load("https://www.google.com/s2/favicons?sz=64&domain_url=" + model.addressLink)
                        .circleCrop()
                        .into(holder.imageView)


                    holder.itemView.setOnClickListener {
                        MyCustomDialog(
                            model.commentLink,
                            model.addressLink,
                            applicationContext
                        ).show(supportFragmentManager, "MyCustomFragment")
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
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(model.addressLink))
                        startActivity(browserIntent)

                    }
                    holder.fabEditLink.setOnClickListener {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        addLink.visibility = View.GONE
                        changeLink.visibility = View.VISIBLE
                        nameLink.setText(model.linkName)
                        addressLink.setText(model.addressLink)
                        commentLink.setText(model.commentLink)
                        docName = model.docName
                    }
                    holder.fabDelButton.setOnClickListener {
                        db.firestoreDb.collection("SuperApp").document(model.docName)
                            .delete()
                            .addOnSuccessListener {
                                Log.d(
                                    "Delete",
                                    "DocumentSnapshot successfully deleted! + ${model.docName}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "Delete",
                                    "Error deleting document",
                                    e
                                )
                            }
                    }

                    changeLink.setOnClickListener {
                        addLink.visibility = View.VISIBLE
                        changeLink.visibility = View.GONE
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                        val washingtonRef =
                            db.firestoreDb.collection("SuperApp").document(docName)
                        washingtonRef.update(
                            mapOf(
                                "linkName" to nameLink.text.toString(),
                                "addressLink" to addressLink.text.toString(),
                                "commentLink" to commentLink.text.toString()
                            )
                        ).addOnSuccessListener {
                            nameLink.setText("")
                            addressLink.setText("")
                            commentLink.setText("")
                            db.successToast.SuccessToast(
                                "Success",
                                "Изменения записаны",
                                this@Links
                            )
                        }
                            .addOnFailureListener { exception ->
                                db.errorToast.ErrorToast(
                                    "Ошибка изменения",
                                    exception.toString(),
                                    this@Links
                                )
                            }
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
                    errorToast.errorToast.ErrorToast(
                        "Error",
                        "Ошибка загрузки данных  +  $e.message",
                        this@Links
                    )
                }

                override fun getItemViewType(position: Int): Int {
                    if (adapter.getItem(position).id.toInt() == 0) {
                        val type = POST_TYPE_DESC
                        return type
                    } else {
                        val type = POST_TYPE_IMAGE
                        return type
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

        var fabMenu: FloatingActionButton = itemView.findViewById(R.id.fabMenu)

        var fabEditLink: FloatingActionButton = itemView.findViewById(R.id.fabEditLink)

        var fabDelButton: FloatingActionButton = itemView.findViewById(R.id.fabDelButton)

        var fabGoLink: FloatingActionButton = itemView.findViewById(R.id.fabGoLink)

        var createDateAndTime: TextView = itemView.findViewById(R.id.dateAndTime)

    }

    fun changeQuery(s: String) {

        if (s.length - 1 == 1) {
            val newQuery = db.firestoreDb.collection("SuperApp")
                .orderBy("id")
                .whereEqualTo("id", 0)
                .limitToLast(100)

            // Make new options
            val newOptions = FirestoreRecyclerOptions.Builder<FriendsResponse>()
                .setQuery(newQuery, FriendsResponse::class.java)
                .build()

            // Change options of adapter.
            adapter.updateOptions(newOptions)
        } else {
            val newQuery = db.firestoreDb.collection("SuperApp")
                .orderBy("linkName")
                .startAt(s)
                .endAt(s + "\uf8ff")
                //.whereEqualTo("linkName",s)

                .limitToLast(100)

            // Make new options
            val newOptions = FirestoreRecyclerOptions.Builder<FriendsResponse>()
                .setQuery(newQuery, FriendsResponse::class.java)
                .build()

            // Change options of adapter.
            adapter.updateOptions(newOptions)
        }
    }

    fun handleSendText(intent: Intent) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            addressLink.setText(sharedText)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()

    }

    private fun getDatesOfNextMonth(): List<Date> {
        currentMonth++ // + because we want next month
        if (currentMonth == 12) {
            // we will switch to january of next year, when we reach last month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] + 1)
            currentMonth = 0 // 0 == january
        }
        return getDates(mutableListOf())
    }

    fun getDatesOfPreviousMonth(): List<Date> {
        currentMonth-- // - because we want previous month
        if (currentMonth == -1) {
            // we will switch to december of previous year, when we reach first month of year
            calendar.set(Calendar.YEAR, calendar[Calendar.YEAR] - 1)
            currentMonth = 11 // 11 == december
        }
        return getDates(mutableListOf())
    }

    fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }
}
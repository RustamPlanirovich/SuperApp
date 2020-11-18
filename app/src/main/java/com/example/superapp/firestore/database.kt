package com.example.superapp.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class database @Inject constructor() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    @Inject get

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    @Inject get


}

class Db @Inject constructor()
class FirebaseAuth @Inject constructor()
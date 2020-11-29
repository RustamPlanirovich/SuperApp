package com.example.superapp.firestore

import com.example.superapp.toast.ErrorToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class database @Inject constructor() {

    var firestoreDb: FirebaseFirestore = FirebaseFirestore.getInstance()
        @Inject get

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        @Inject get

    var errorToast = ErrorToast()
        @Inject get

    var successToast = ErrorToast()
        @Inject get


}

class Db @Inject constructor()
class FirebaseAuth @Inject constructor()
class ErrorToast @Inject constructor()
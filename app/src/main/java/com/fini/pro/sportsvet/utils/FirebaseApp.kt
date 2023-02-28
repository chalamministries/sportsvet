package com.fini.pro.sportsvet.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class FirebaseApp {

    companion object {

        fun writeException(type: String, message: String) {
            val data = mutableMapOf<String, Any>()
            data["sdk"] = android.os.Build.VERSION.SDK_INT
            data["type"] = type
            data["message"] = message
            data["date"] = Date().time
            val ref = Firebase.database.getReference("exceptions")
            val exceptRef = ref.push()
            exceptRef.setValue(data)
        }

    }

}
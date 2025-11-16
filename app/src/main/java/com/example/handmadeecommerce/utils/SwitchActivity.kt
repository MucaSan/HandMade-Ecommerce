package com.example.handmadeecommerce.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity


fun switchActivity(activity : AppCompatActivity, cls : Class<*>){
    val intent = Intent(activity, cls)
    activity.startActivity(intent)
}
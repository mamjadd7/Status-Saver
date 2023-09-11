package com.example.statussaverapplication.utils

import android.content.Context
import android.content.Intent

object NewScreen {
    fun startScreen(context: Context, c : Class<*>){
        context.startActivity(Intent(context,c))
    }
}
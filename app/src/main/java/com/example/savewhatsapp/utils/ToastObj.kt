package com.example.statussaverapplication.utils

import android.content.Context
import android.widget.Toast

object ToastObj {


    fun shortMessage(context: Context,s:String){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show()
    }
    fun longMessage(context: Context,s:String){
        Toast.makeText(context,s,Toast.LENGTH_LONG).show()
    }
}
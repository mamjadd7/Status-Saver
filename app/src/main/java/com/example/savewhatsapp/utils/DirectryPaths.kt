package com.example.savewhatsapp.utils

import android.os.Environment
import java.io.File

object DirectryPaths {
    val whatsAppPath = Environment.getExternalStorageDirectory().absolutePath.toString() +
            "/WhatsApp/Media/.statuses"

    val WBpath = Environment.getExternalStorageDirectory().absolutePath.toString() +
            "/WhatsApp Business/Media/.statuses"


    val whatsAppVideosSavePath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/WhatsApp Videos"
    )

    fun creatWhatsAppVideosDestinationFolder() {
        if (!whatsAppVideosSavePath.exists()) {
            whatsAppVideosSavePath.mkdirs()
        }
    }

    val whatsAppImagesSavePath = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                + "/WhatsApp Images"
    )

    fun creatWhatsAppImagesDestinationFolder() {
        if (!whatsAppImagesSavePath.exists()) {
            whatsAppImagesSavePath.mkdirs()
        }
    }

}
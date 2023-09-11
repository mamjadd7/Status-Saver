package com.example.statusapplication.utils

import android.os.Environment
import java.io.File

object Common {


    val STATUS_DIRECTORY: File = File(
        Environment.getExternalStorageDirectory().toString() +
                File.separator + "WhatsApp/Media/.Statuses"
    )

    const val APP_DIR = "/SavedStatus"
}

package com.example.savewhatsapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.statussaverapplication.utils.ConstantVariables
import com.example.statussaverapplication.utils.ToastObj
import com.savewhatsapp.databinding.ActivityImageViewerBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class ImageViewerActivity : AppCompatActivity() {

    private val TAG = "DownloadFragment"
    private lateinit var binding: ActivityImageViewerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uri = intent.getStringExtra("uri").toString()
        val check=intent.getStringExtra(ConstantVariables.PASS_DATA)
        if (check.equals(ConstantVariables.PASS_DATA)){
            ToastObj.longMessage(this,"Dwon")
        }else{
            ToastObj.longMessage(this,"nooo")
        }
        Glide.with(this).load(uri).into(binding.imageview)
        binding.back.setOnClickListener {
            finish()
        }

        binding.buttonCard.whatsappBtn.setOnClickListener{
            val mainUri = Uri.parse(uri)

            shareonwhatsapp(mainUri)
        }
        binding.buttonCard.shareBtn.setOnClickListener {



            try {
                val mainUri = Uri.parse(uri)
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "image/*"
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(
                        Intent.createChooser(
                            sharingIntent,
                            "Share Video using"
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this@ImageViewerActivity,
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (ex: Exception) {
                Toast.makeText(
                    this@ImageViewerActivity,
                    ex.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        binding.buttonCard.downloadBtn.setOnClickListener {



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val sourceUri: Uri = Uri.parse(uri)
                val resolver = applicationContext.contentResolver
                val inputStream = resolver.openInputStream(sourceUri)

                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val folder = File(downloadsDir, "WhatsApp Images")

                if (!folder.exists()) {
                    folder.mkdir()
                }

                val fileName = System.currentTimeMillis().toString() + "wp_image.png"
                val destinationFile = File(folder, fileName)

                destinationFile.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }

                MediaScannerConnection.scanFile(
                    applicationContext,
                    arrayOf(destinationFile.absolutePath),
                    null,
                    null
                )

                Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()

            } else {

                val file = File(uri)

                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val folder = File(downloadsDir, "WhatsApp Images")

                if (!folder.exists()) {
                    folder.mkdir()
                }

                val fileName = "whatsappImage" + System.currentTimeMillis() + ".jpg"
                val des = File(folder, fileName)

                copyFile(file, des)

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun shareonwhatsapp(uri: Uri){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setPackage("com.whatsapp")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show()
        }

    }
    private fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        source = FileInputStream(sourceFile).getChannel()
        destination = FileOutputStream(destFile).getChannel()
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        if (source != null) {
            source.close()
        }
        if (destination != null) {
            destination.close()
        }
    }


}
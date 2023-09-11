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
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.savewhatsapp.R
import com.savewhatsapp.databinding.ActivityVideoPlayerBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class VideoPlayerActivity : AppCompatActivity() {


    private val TAG = "DownloadFragment"


    private lateinit var player: SimpleExoPlayer

    private lateinit var binding: ActivityVideoPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val uri = intent.getStringExtra("uri").toString()

        player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        binding.back.setOnClickListener {
            finish()
        }
        binding.buttonCard.whatsappBtn.setOnClickListener{
            val mainUri = Uri.parse(uri)

            shareVideoToWhatsApp(mainUri)
        }
        binding.buttonCard.shareBtn.setOnClickListener {


            val mainUri = Uri.parse(uri)
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "video/mp4"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            try {
                startActivity(Intent.createChooser(sharingIntent, "Share Video using"))
            } catch (e: Exception) {
                Toast.makeText(
                    this@VideoPlayerActivity,
                    e.message.toString(),
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
                val folder = File(downloadsDir, "WhatsApp Videos")

                if (!folder.exists()) {
                    folder.mkdir()
                }

                val fileName = "whatsappvideo" + System.currentTimeMillis().toString() + ".mp4"
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
                val folder = File(downloadsDir, "WhatsApp Videos")

                if (!folder.exists()) {
                    folder.mkdir()
                }

                val fileName = "whatsappImage" + System.currentTimeMillis() + ".mp4"
                val des = File(folder, fileName)

                copyFile(file, des)

                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun shareVideoToWhatsApp(videoUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        intent.putExtra(Intent.EXTRA_STREAM, videoUri)
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

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }



}
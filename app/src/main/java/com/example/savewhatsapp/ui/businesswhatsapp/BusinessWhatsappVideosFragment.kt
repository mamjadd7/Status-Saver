package com.example.savewhatsapp.ui.businesswhatsapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.example.savewhatsapp.modals.StatusModel
import com.example.savewhatsapp.ui.VideoPlayerActivity
import com.example.savewhatsapp.utils.DirectryPaths
import com.example.statussaverapplication.adapters.recyclers.BusinessVideoAdapter
import com.example.statussaverapplication.utils.ConstantVariables
import com.example.statussaverapplication.utils.SharedPrefObj

import com.savewhatsapp.R
import com.savewhatsapp.databinding.FragmentBusinessWhatsappVideosBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class BusinessWhatsappVideosFragment : Fragment() {
    private val TAG = "DownloadFragment"
    private val list: ArrayList<StatusModel> by lazy { ArrayList() }
    private val adapter by lazy { BusinessVideoAdapter(list) }
    private lateinit var binding: FragmentBusinessWhatsappVideosBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBusinessWhatsappVideosBinding.inflate(inflater, container, false)

        recyclerView()
        binding.recyclerview.adapter = adapter
        versionControlling()
        return binding.root
    }

    private fun versionControlling() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
// Current version is equal to or greater than Android Q
                // Your code for handling Android Q and above here
                if (SharedPrefObj.getString(requireContext(),ConstantVariables.businessWhatsapp_Path_Key).equals("")) {
                    getWhatsappFolderPermission()
                } else {
                    val path =SharedPrefObj.getString(requireContext(),ConstantVariables.businessWhatsapp_Path_Key)
                    requireContext().contentResolver.takePersistableUriPermission(
                        path!!.toUri(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    if (path != "") {

                        val doc = DocumentFile.fromTreeUri(requireContext(), path.toUri())
                        if (doc != null) {
                            for (file in doc.listFiles()) {
                                if (!file.name!!.endsWith(".nomedia")
                                ) {
                                    if (file.uri.path?.endsWith(".mp4") == true) {
                                        list.add(StatusModel(file.uri.toString()))
                                        // Log.d("Uris", file.uri.toString())
                                    }
                                }
                            }
                            if (list.isEmpty()){
                                binding.nofilelayout.visibility = View.VISIBLE
                                binding.recyclerview.visibility = View.GONE

                            }else {
                                // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()

                                binding.nofilelayout.visibility = View.GONE
                                binding.recyclerview.visibility = View.VISIBLE
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            } catch (e: Exception) {

            }


        } else {
            try {

                // Current version is below Android Q
                // Your code for handling below Android Q here
                val folder = File(DirectryPaths.WBpath)

                val files = folder.listFiles()

                if (files != null) {
                    for (i in files) {
                        if (i.path.endsWith(".mp4")) {

                            //  Toast.makeText(requireContext(),"running",Toast.LENGTH_SHORT).show()
                            val f = File(i.toString())
                            val path = f.absolutePath
                            list.add(StatusModel(path))
                        }
                    }
                }
                if (list.isEmpty()){
                    binding.nofilelayout.visibility = View.VISIBLE
                    binding.recyclerview.visibility = View.GONE

                }else {
                    binding.nofilelayout.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE

                    // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()

                }
                adapter.notifyDataSetChanged()

                //   Toast.makeText(requireContext(), list.size.toString(), Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {

            }
        }
    }

    private fun recyclerView() {
        adapter.recyclerClick(object :BusinessVideoAdapter.PassData{
            override fun videoClick(position: Int) {
                val uri = list[position].file
                val i = Intent(requireContext(), VideoPlayerActivity::class.java)
                i.putExtra("uri", uri)
                startActivity(i)            }

            override fun shareClick(model: StatusModel) {
                val mainUri = Uri.parse(model.file)
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "video/mp4"
                sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
                sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(Intent.createChooser(sharingIntent, "Share Video using"))
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        e.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }            }

            override fun saveClick(position: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {

                        val sourceUri: Uri = Uri.parse(list[position].file)
                        val resolver = requireContext().applicationContext.contentResolver
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
                            requireContext().applicationContext,
                            arrayOf(destinationFile.absolutePath),
                            null,
                            null
                        )

                        Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "File is not Saved !" + e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } else {
                    try {
                        val file = File(list[position].file)

                        val downloadsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val folder = File(downloadsDir, "WhatsApp Videos")

                        if (!folder.exists()) {
                            folder.mkdir()
                        }

                        val fileName = "whatsappImage" + System.currentTimeMillis() + ".mp4"
                        val des = File(folder, fileName)

                        copyFile(file, des)

                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "File is not Saved ! " + e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getWhatsappFolderPermission() {
        val sm = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val startDir = "Android%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        startActivityForResult(intent, 2002)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val treeUri = data?.data
            Log.d("treeuri", treeUri.toString())
            SharedPrefObj.saveString(requireContext(),ConstantVariables.businessWhatsapp_Path_Key,treeUri.toString())
            if (treeUri != null) {
                requireActivity().contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val doc = DocumentFile.fromTreeUri(requireContext().applicationContext, treeUri)
                if (doc != null) {
                    doc.listFiles()?.let { files ->
                        for (file in files) {
                            if (!file.name.orEmpty().endsWith(".nomedia")) {
                                val uri = file.uri
                                if (uri.path.orEmpty().endsWith(".mp4")) {
                                    list.add(StatusModel(uri.toString()))
                                }
                            }
                        }
                    }
                    if (list.isEmpty()){
                        binding.nofilelayout.visibility = View.VISIBLE
                        binding.recyclerview.visibility = View.GONE


                    }else {
                        // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()
                        binding.nofilelayout.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE

                    }
                    adapter.notifyDataSetChanged()
                }
            }

        }

    }
    private fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        source = FileInputStream(sourceFile).channel
        destination = FileOutputStream(destFile).channel
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
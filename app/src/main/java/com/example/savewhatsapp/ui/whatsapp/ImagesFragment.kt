package com.example.savewhatsapp.ui.whatsapp

import android.app.Activity
import android.app.DirectAction
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
import com.example.savewhatsapp.ui.ImageViewerActivity
import com.example.savewhatsapp.utils.DirectryPaths
import com.example.statussaverapplication.adapters.recyclers.ImageAdapter
import com.example.statussaverapplication.utils.ConstantVariables
import com.example.statussaverapplication.utils.SharedPrefObj
import com.example.statussaverapplication.utils.ToastObj
import com.savewhatsapp.R
import com.savewhatsapp.databinding.FragmentImagesBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class ImagesFragment :Fragment() {
    private val TAG = "DownloadFragment"
    private val list: ArrayList<StatusModel> by lazy { ArrayList() }
    private val adapter: ImageAdapter by lazy { ImageAdapter(list) }
    private lateinit var binding: FragmentImagesBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImagesBinding.inflate(inflater, container, false)

        recyclerView()
        whatsappButtonClick()
        versionControlling()
        return binding.root
    }

    private fun versionControlling() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWhatsappFolderPermission()

            try {
                if (SharedPrefObj.getString(requireContext(),ConstantVariables.whatsapp_Path_Key)
                        .equals("")) {

                    getWhatsappFolderPermission()
                } else {
                    ToastObj.longMessage(requireContext(),SharedPrefObj.getString(requireContext(),ConstantVariables.whatsapp_Path_Key).toString())

                    //  Toast.makeText(requireContext(),"eles",Toast.LENGTH_SHORT).show()

                    val path = SharedPrefObj.getString(requireContext(),ConstantVariables.whatsapp_Path_Key)
                    requireContext().contentResolver.takePersistableUriPermission(
                        path!!.toUri(),
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    if (path != "") {

                        val doc = DocumentFile.fromTreeUri(requireContext(), path.toUri())
                        if (doc != null) {
                            for (file in doc.listFiles()) {
                                file.uri?.let { uri ->
                                    if (!(file.name?.endsWith(".nomedia") == true)) {
                                        val path = uri.path
                                        if (path != null && (path.endsWith(".png") || path.endsWith(".jpg"))) {
                                            list.add(StatusModel(uri.toString()))
                                            // Log.d("Uris", uri.toString())
                                        }
                                    }
                                }

//                            if (!file.name!!.endsWith(".nomedia")
//                            ) {
//                                if (file.uri.path?.endsWith(".png") == true || file.uri.path?.endsWith(
//                                        ".jpg"
//                                    ) == true
//                                ) {
//                                    list.add(StatusModel(file.uri.toString()))
//                                    // Log.d("Uris", file.uri.toString())
//                                }
//                            }
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
            }catch (e:Exception){

            }

        } else {
            try {
                // Current version is below Android Q
                // Your code for handling below Android Q here
                val folder = File(DirectryPaths.whatsAppPath)

                val files = folder.listFiles()

                if (files != null) {

                    for (i in files) {
                        if (i.path.endsWith(".png") || i.path.endsWith(".jpg")) {

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
                    // Toast.makeText(requireContext(), "else", Toast.LENGTH_SHORT).show()

                    binding.nofilelayout.visibility = View.GONE
                    binding.recyclerview.visibility = View.VISIBLE
                }

                adapter.notifyDataSetChanged()
            }catch (e:Exception){

            }

            //  Toast.makeText(requireContext(), list.size.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun recyclerView() {
        binding.recyclerview.adapter = adapter
        adapter.recyclerClick(object :ImageAdapter.PassData{
            override fun imageClick(position:Int) {
                val i = Intent(requireContext(), ImageViewerActivity::class.java)
                i.putExtra("uri", list[position].file)
                startActivity(i)

            }

            override fun shareClick(modal: StatusModel) {
                try {

                    val mainUri = Uri.parse(modal.file)
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
                            requireContext(),
                            e.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (ex: Exception) {
                    Toast.makeText(
                        requireContext(),
                        ex.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun saveClick(position: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        val sourceUri: Uri = Uri.parse(list[position].file)
                        val resolver = requireContext().applicationContext.contentResolver
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
                            requireContext().applicationContext,
                            arrayOf(destinationFile.absolutePath),
                            null,
                            null
                        )

                        Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()

                    }catch (e:Exception){
                        Toast.makeText(
                            requireContext(),
                            "File is not Saved ! "+e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                } else {

                    try { val file = File(list[position].file)

                        val downloadsDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        val folder = File(downloadsDir, "WhatsApp Images")

                        if (!folder.exists()) {
                            folder.mkdir()
                        }

                        val fileName = "whatsappImage" + System.currentTimeMillis() + ".jpg"
                        val des = File(folder, fileName)

                        copyFile(file, des)

                        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()


                    }catch (e:Exception){
                        Toast.makeText(
                            requireContext(),
                            "File is not Saved ! "+e.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }
            }

        })
    }

    private fun whatsappButtonClick() {
        binding.gotowhatsapp.setOnClickListener{
            val packageName = "com.whatsapp"
            val intent = requireContext().packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getWhatsappFolderPermission() {
        ToastObj.longMessage(requireContext(),"Please per")
        val sm = requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = sm.primaryStorageVolume.createOpenDocumentTreeIntent()
        val startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI")
        var scheme = uri.toString()
        scheme = scheme.replace("/root/", "/document/")
        scheme += "%3A$startDir"
        uri = Uri.parse(scheme)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        startActivityForResult(intent, 2001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            val treeUri = data?.data
            Log.d("treeuri", treeUri.toString())
            SharedPrefObj.saveString(requireContext(),ConstantVariables.whatsapp_Path_Key,treeUri.toString())

            if (treeUri != null) {
                requireActivity().contentResolver.takePersistableUriPermission(
                    treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val doc = DocumentFile.fromTreeUri(requireContext().applicationContext, treeUri)
                if (doc != null) {
                    for (file in doc.listFiles()) {
                        if (file.name?.endsWith(".nomedia") == false) {
                            val uri = file.uri
                            if (uri?.path?.endsWith(".png") == true || uri?.path?.endsWith(".jpg") == true) {
                                list.add(StatusModel(uri.toString()))
                                // Log.d("Uris", uri.toString())
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
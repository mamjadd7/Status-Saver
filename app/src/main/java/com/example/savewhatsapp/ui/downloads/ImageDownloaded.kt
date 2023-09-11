package com.example.savewhatsapp.ui.downloads

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.savewhatsapp.modals.StatusModel
import com.example.savewhatsapp.ui.ImageViewerActivity
import com.example.statussaverapplication.adapters.recyclers.DownloadedImageAdapter
import com.example.statussaverapplication.utils.ConstantVariables
import com.savewhatsapp.databinding.FragmentImageDownloadedBinding
import com.savewhatsapp.databinding.FragmentImagesBinding
import java.io.File


class ImageDownloaded : Fragment(){

    private val TAG = "DownloadFragment"
    private val list by lazy { ArrayList<StatusModel>() }
    private val adapter by lazy { DownloadedImageAdapter(list) }
    private lateinit var binding: FragmentImageDownloadedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageDownloadedBinding.inflate(inflater, container, false)
        recyclerView()
        return binding.root
    }

    private fun recyclerView() {
        binding.recyclerview.adapter = adapter
        adapter.recyclerClick(object :DownloadedImageAdapter.PassData{
            override fun imageClick(position: Int) {
                val i = Intent(requireContext(), ImageViewerActivity::class.java)
                i.putExtra("uri", list[position].file)
                i.putExtra(ConstantVariables.PASS_DATA,ConstantVariables.PASS_DATA)
                startActivity(i)
            }

            override fun shareClick(position:Int) {
                try {
                    val mainUri = Uri.parse(list[position].file)
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "image/*"
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri)
                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    try {
                        requireContext().startActivity(
                            Intent.createChooser(
                                sharingIntent,
                                "Share Video using"
                            )
                        )
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }catch (e:Exception
                ){

                }
            }

            override fun deleteClick(position: Int) {
                val builder = AlertDialog.Builder(requireContext())

                builder.setTitle("Delete Video")
                builder.setMessage("Are you sure want to delete?")

                builder.setPositiveButton("Yes") { dialog, which ->
                    // do something when "Yes" button is clicked
                    try {
                        val file = File(list[position].file)
                        val deleted: Boolean = file.delete()
                        if (deleted) {
                            list.removeAt(position)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(requireContext(), "File Deleted", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                builder.setNegativeButton("No") { dialog, which ->
                    // do something when "No" button is clicked
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }

        })
    }
    fun isFileSaved(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    override fun onResume() {
        super.onResume()

        list.clear()

        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val folder = File(downloadsDir, "WhatsApp Images")

        val files = folder.listFiles()
        if (files != null) {
            for (i in files) {
                list.add(StatusModel(i.absolutePath))
            }
            adapter.notifyDataSetChanged()
        }
    }




}
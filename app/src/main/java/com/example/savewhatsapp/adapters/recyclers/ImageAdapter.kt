package com.example.statussaverapplication.adapters.recyclers

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savewhatsapp.modals.StatusModel
import com.savewhatsapp.R
import com.savewhatsapp.databinding.RecyclerItemViewLayoutBinding
import java.io.File


class ImageAdapter(private val imagesList: List<StatusModel>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private lateinit var context: Context
    lateinit var click:PassData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = RecyclerItemViewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }
    interface PassData {
        fun imageClick(position: Int)
        fun shareClick(modal: StatusModel)
        fun saveClick(position: Int)
    }
    fun recyclerClick(listener: PassData){
        click=listener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = imagesList[position]
        val mainUri = Uri.parse(modal.file)
        val file=File(mainUri.path)
        if (file.exists()){
            holder.binding.save.setImageResource(R.drawable.ic_save)
        }else{
            holder.binding.save.setImageResource(R.drawable.ic_save)
        }
        holder.binding.imageView.setOnClickListener {
            click.imageClick(position)
        }

        holder.binding.saveButton.setOnClickListener {
            click.saveClick(position)
        }

        holder.binding.shareButton.setOnClickListener {
            click.shareClick(modal)
        }
        Glide.with(holder.itemView.context).load(modal.file).into(holder.binding.imageView)
    }
    fun isFileExistsInDirectory(context: Context, fileName: String): Boolean {
        val path = context.filesDir.absolutePath + "/$fileName"
        val file = File(path)
        return file.exists()
    }



    override fun getItemCount(): Int {
        return imagesList.size
    }
    inner class ViewHolder(val binding: RecyclerItemViewLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
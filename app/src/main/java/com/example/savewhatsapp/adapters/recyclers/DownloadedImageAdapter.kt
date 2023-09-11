package com.example.statussaverapplication.adapters.recyclers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savewhatsapp.modals.StatusModel
import com.savewhatsapp.R
import com.savewhatsapp.databinding.RecyclerItemViewLayoutBinding
import java.io.File


class DownloadedImageAdapter(private val imagesList: List<StatusModel>) :
    RecyclerView.Adapter<DownloadedImageAdapter.ViewHolder>() {

    private lateinit var context: Context
    lateinit var click:PassData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = RecyclerItemViewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }
    interface PassData {
        fun imageClick(position: Int)
        fun shareClick(position: Int)
        fun deleteClick(position: Int)
    }
    fun recyclerClick(listener: PassData){
        click=listener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = imagesList[position]
        Glide.with(holder.itemView.context).load(modal.file).into(holder.binding.imageView)

        holder.binding.imageView.setOnClickListener {
            click.imageClick(position)
        }
        holder.binding.save.setImageResource(R.drawable.delete_icon)
        holder.binding.saveButton.setOnClickListener {
            click.deleteClick(position)

        }

        holder.binding.shareButton.setOnClickListener {
            click.shareClick(position)
        }
    }



    // Function to check if the specific file exists in storage






    override fun getItemCount(): Int {
        return imagesList.size
    }
    inner class ViewHolder(val binding: RecyclerItemViewLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
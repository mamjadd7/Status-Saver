package com.example.statussaverapplication.adapters.recyclers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.savewhatsapp.modals.StatusModel

import com.savewhatsapp.databinding.RecyclerItemViewLayoutBinding



class BusinessVideoAdapter(private val videoList: List<StatusModel>) :
    RecyclerView.Adapter<BusinessVideoAdapter.ViewHolder>() {

    lateinit var click:PassData
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RecyclerItemViewLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }
    interface PassData {
        fun videoClick(position: Int)
        fun shareClick(model: StatusModel)
        fun saveClick(position: Int)
    }
    fun recyclerClick(listener: PassData){
        click=listener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modal = videoList[position]
        Glide.with(holder.itemView.context).load(modal.file).into(holder.binding.imageView)

        holder.binding.imageView.setOnClickListener {
            click.videoClick(position)
        }

        holder.binding.saveButton.setOnClickListener {
            click.saveClick(position)

        }

        holder.binding.shareButton.setOnClickListener {
            click.shareClick(modal)
        }
    }




    override fun getItemCount(): Int {
        return videoList.size
    }
    inner class ViewHolder(val binding: RecyclerItemViewLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}

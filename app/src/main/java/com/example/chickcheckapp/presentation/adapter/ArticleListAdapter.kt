package com.example.chickcheckapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chickcheckapp.data.remote.response.Data
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.databinding.ItemLayoutArticleBinding
import com.example.chickcheckapp.presentation.ui.article.ArticleFragmentDirections
import com.example.chickcheckapp.presentation.ui.camera.CameraXFragmentDirections

class ArticleListAdapter(private val items:List<DataItem>) : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemLayoutArticleBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:DataItem){
           binding.tvDesiaseName.text = item.title
            itemView.setOnClickListener {
                val action =
                    ArticleFragmentDirections.actionNavigationArticleToResultFragment2(
                        item
                    )
                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLayoutArticleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item= items[position]
        holder.bind(item)


    }
    companion object{
        const val  TAG = "ArticleListAdapter"
    }
}
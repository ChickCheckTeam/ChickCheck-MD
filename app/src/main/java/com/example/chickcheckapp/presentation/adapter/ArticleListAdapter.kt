package com.example.chickcheckapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.databinding.ItemLayoutArticleBinding
import com.example.chickcheckapp.presentation.ui.article.ArticleFragmentDirections
import com.example.chickcheckapp.utils.Utils

class ArticleListAdapter(private val items: List<ArticleData>) :
    RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemLayoutArticleBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:ArticleData){
           binding.tvDesiaseName.text = item.title
            binding.tvCause.text = Utils.parseJsonToDisease(item.content).alternativeTitle
            when(item.title.lowercase()){
                "new castle disease" ->{
                    binding.shapeableImageView.setImageResource(R.drawable.newcastle)
                }

                "salmonellosis" -> {
                    binding.shapeableImageView.setImageResource(R.drawable.salmonella)
                }

                "coccidiosis" -> {
                    binding.shapeableImageView.setImageResource(R.drawable.coccidiosis)
                }

            }
            itemView.setOnClickListener {
                val action =
                    ArticleFragmentDirections.actionNavigationArticleToResultFragment(
                        article = item
                    )
                it.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemLayoutArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)


    }

    companion object {
        const val TAG = "ArticleListAdapter"
    }
}
package com.example.chickcheckapp.presentation.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.chickcheckapp.R
import com.example.chickcheckapp.data.local.model.ArticleContent
import com.example.chickcheckapp.data.remote.response.DataItem
import com.example.chickcheckapp.databinding.ItemLayoutArticleBinding
import com.example.chickcheckapp.databinding.ItemLayoutContentArticleBinding
import com.example.chickcheckapp.presentation.ui.article.ArticleFragmentDirections

class ArticleContentListAdapter(private val items: List<ArticleContent>) :
    RecyclerView.Adapter<ArticleContentListAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemLayoutContentArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ArticleContent) {
            binding.tvInformationTitle.text = item.title
            binding.tvInformationValue.text = item.content
            binding.imageView.setImageResource(item.icon)
            binding.ivDownArrow.setOnClickListener {
                item.isExpanded = !item.isExpanded
                binding.ivDownArrow.animate().rotation(if (item.isExpanded) 180f else 0f)
                    .setDuration(300).start()
                if (item.isExpanded) {
                    binding.tvInformationValue.apply {
                        visibility = View.VISIBLE
                        alpha = 0f
                        animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setListener(null)
                    }
                } else {
                    binding.tvInformationValue.apply {
                        animate()
                            .alpha(0f)
                            .setDuration(300)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    visibility = View.GONE
                                }
                            })
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLayoutContentArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
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
        const val TAG = "ArticleContentListAdapter"
    }
}
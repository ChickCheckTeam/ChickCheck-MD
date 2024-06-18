package com.example.chickcheckapp.presentation.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chickcheckapp.data.remote.response.ArticleData
import com.example.chickcheckapp.data.remote.response.RecentHistoryResponse
import com.example.chickcheckapp.data.remote.response.ScanDataItem
import com.example.chickcheckapp.data.remote.response.ScanHistoryItem
import com.example.chickcheckapp.databinding.ItemScanHistoryLayoutBinding
import com.example.chickcheckapp.utils.OnHistoryItemClickListener
import com.example.chickcheckapp.utils.Utils

class RecentHistoryAdapter(
    private val listener: OnHistoryItemClickListener
): ListAdapter<ScanDataItem, RecentHistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemScanHistoryLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, listener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }
    
    class MyViewHolder(
        private val binding: ItemScanHistoryLayoutBinding,
        private val listener: OnHistoryItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ScanDataItem) {
            val diseaseName = item.title
            val date = item.createdAt
            val articleData = ArticleData(
                title = item.article.title,
                content = item.article.content,
                sources = item.article.sources,
                id = item.article.id,
                tags = item.article.tags,
                author = item.article.author,
                createdAt = item.article.createdAt,
                updatedAt = item.article.updatedAt
            )
            Log.d("HistoryAdapter", "bind: $articleData")
            with(binding) {
                tvDiseaseName.text = diseaseName
                tvDate.text = Utils.formatDate(date)
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(ivScanHistory)
                when (diseaseName.lowercase()) {
                    "healthy" -> {
                        tvCauseName.visibility = View.GONE
                    }

                    "salmonellosis" -> {
                        tvCauseName.text = "Sallmonella sp"
                    }

                    "new castle disease" -> {
                        tvCauseName.text = "Avian Paramyxovirus"
                    }

                    "coccidiosis" -> {
                        tvCauseName.text =
                            "Eimeria acervulina, E. maxima, E. mitis, E. tenella, E. necatrix dan E. brunetti"
                    }
                }
                itemView.setOnClickListener {
                    listener.onHistoryItemClick(articleData, item.imageUrl)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScanDataItem>() {
            override fun areItemsTheSame(oldItem: ScanDataItem, newItem: ScanDataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ScanDataItem, newItem: ScanDataItem): Boolean {
                return oldItem == newItem
            }


        }
    }
}
package com.example.chickcheckapp.presentation.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chickcheckapp.data.ChickCheckRepository
import com.example.chickcheckapp.data.remote.response.ScanHistoryItem
import com.example.chickcheckapp.databinding.ItemScanHistoryLayoutBinding
import com.example.chickcheckapp.utils.OnHistoryItemClickListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HistoryAdapter(
    private val listener: OnHistoryItemClickListener
) : ListAdapter<ScanHistoryItem, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

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
        @Inject
        lateinit var repository: ChickCheckRepository

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: ScanHistoryItem) {

            val diseaseName = item.title
            val date = item.createdAt
            with(binding) {
                tvDiseaseName.text = diseaseName
                tvDate.text = formatDate(date)
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
                    listener.onHistoryItemClick(diseaseName, item.imageUrl)
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun formatDate(date: String): String? {
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            // Parse the input date string
            val dateTime = LocalDateTime.parse(date, inputFormat)

            // Format the date to the desired output format
            val formattedDate = dateTime.format(outputFormat)

            return formattedDate
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScanHistoryItem>() {
            override fun areItemsTheSame(
                oldItem: ScanHistoryItem,
                newItem: ScanHistoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ScanHistoryItem,
                newItem: ScanHistoryItem
            ): Boolean {
                return oldItem == newItem
            }


        }
    }

}
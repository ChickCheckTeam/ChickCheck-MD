package com.example.chickcheckapp.presentation.adapter

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chickcheckapp.data.remote.response.PlacesItem
import com.example.chickcheckapp.databinding.ItemNearbyPlacesLayoutBinding
import com.example.chickcheckapp.utils.Utils
import com.example.chickcheckapp.utils.Utils.dialogAlertBuilder

class NearbyPlacesListAdapter(private val location: Location) :
    ListAdapter<PlacesItem, NearbyPlacesListAdapter.NearbyPlacesViewHolder>(DIFF_CALLBACK) {
    class NearbyPlacesViewHolder(
        private val binding: ItemNearbyPlacesLayoutBinding,
        private val location: Location
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PlacesItem) {
            val locationLatitude = item.location.latitude as Double
            val locationLongitude = item.location.longitude as Double
            binding.tvAddress.text = item.formattedAddress
            binding.tvDisplayName.text = item.displayName.text
            val distance =
                location.distanceTo(Utils.turnIntoLocation(locationLatitude, locationLongitude))
            binding.tvDistance.text = Utils.convertDistance(distance)

            itemView.setOnClickListener {
                val title = "Buka Google Maps"
                val message = "Apakah Anda yakin ingin membuka tempat ini di Google Maps?"
                val dialog = dialogAlertBuilder(itemView.context,title,message, {
                    val gmmIntentUri = Uri.parse(item.googleMapsUri)
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    if (mapIntent.resolveActivity(itemView.context.packageManager) != null) {
                        itemView.context.startActivity(mapIntent)
                    } else {
                        val webIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(item.googleMapsUri)
                        }
                        itemView.context.startActivity(webIntent)
                    }
                }).create()
                dialog.show()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyPlacesViewHolder {
        val binding = ItemNearbyPlacesLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NearbyPlacesViewHolder(binding, location)
    }

    override fun onBindViewHolder(holder: NearbyPlacesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlacesItem>() {
            override fun areItemsTheSame(oldItem: PlacesItem, newItem: PlacesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PlacesItem,
                newItem: PlacesItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
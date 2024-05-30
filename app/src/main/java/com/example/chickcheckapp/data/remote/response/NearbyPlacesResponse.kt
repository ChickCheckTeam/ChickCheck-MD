package com.example.chickcheckapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class NearbyPlacesResponse(

	@field:SerializedName("places")
	val places: List<PlacesItem>
)

data class DisplayName(

	@field:SerializedName("text")
	val text: String,

	@field:SerializedName("languageCode")
	val languageCode: String
)

data class PlacesItem(

	@field:SerializedName("googleMapsUri")
	val googleMapsUri: String,

	@field:SerializedName("formattedAddress")
	val formattedAddress: String,

	@field:SerializedName("displayName")
	val displayName: DisplayName,

	@field:SerializedName("location")
	val location: Location,

	@field:SerializedName("photos")
	val photos: List<PhotosItem>? = null,
)

data class PhotosItem(

	@field:SerializedName("authorAttributions")
	val authorAttributions: List<AuthorAttributionsItem>,

	@field:SerializedName("widthPx")
	val widthPx: Int,

	@field:SerializedName("heightPx")
	val heightPx: Int,

	@field:SerializedName("name")
	val name: String
)

data class AuthorAttributionsItem(

	@field:SerializedName("displayName")
	val displayName: String,

	@field:SerializedName("photoUri")
	val photoUri: String,

	@field:SerializedName("uri")
	val uri: String
)

data class Location(

	@field:SerializedName("latitude")
	val latitude: Any,

	@field:SerializedName("longitude")
	val longitude: Any
)

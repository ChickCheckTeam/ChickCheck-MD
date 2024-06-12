package com.example.chickcheckapp.utils

import com.google.gson.annotations.SerializedName

data class Section(
    @SerializedName("heading") val heading: String,
    @SerializedName("content") val content: String,
    @SerializedName("list_content") val listContent: List<String>,
    @SerializedName("sub_section") val subSection: Map<String, String> // Assuming sub_section is an empty object in your example
)

data class Disease(
    @SerializedName("title") val title: String,
    @SerializedName("alternative_title") val alternativeTitle: String,
    @SerializedName("section") val section: List<Section>
)
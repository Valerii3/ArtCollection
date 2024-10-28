package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ArtworkResponse(
        val data: List<Artwork>
)

@Serializable
data class Artwork(
        val id: Int,
        val title: String,
        val image_id: String? // This ID will help construct the image URL
)
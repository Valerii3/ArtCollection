package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ArtistResponse(
        val data: List<Artist>
)

@Serializable
data class Artist(
        val id: Int,
        val title: String,
        val api_link: String,
        val description: String? = null,
)

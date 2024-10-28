package com.example.plugins

import com.example.models.ArtistResponse
import com.example.models.ArtworkResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


suspend fun fetchArtistsFromChicago(page: Int = 1, limit: Int = 10): ArtistResponse {
    // Fetch artist data from Art Institute of Chicago API
    val response: HttpResponse = client.get("https://api.artic.edu/api/v1/artists") {
        parameter("page", page)
        parameter("limit", limit)
    }

    return response.body()  // Deserialize the JSON response into ArtistResponse
}

suspend fun fetchArtworksFromChicago(page: Int = 1, limit: Int = 10): ArtworkResponse {
    val response: HttpResponse = client.get("https://api.artic.edu/api/v1/artworks") {
        parameter("page", page)
        parameter("limit", limit)
    }
    return response.body() // Deserialize JSON response into ArtworkResponse
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/artists") {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val limit = call.request.queryParameters["limit"]?.toInt() ?: 10

            // Fetch artist data and build HTML response
            val artistResponse = fetchArtistsFromChicago(page, limit)
            val htmlContent = buildArtistHtml(artistResponse)

            call.respondText(htmlContent, contentType = io.ktor.http.ContentType.Text.Html)
        }

        get("/artworks") {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val limit = call.request.queryParameters["limit"]?.toInt() ?: 10

            val artworkResponse = fetchArtworksFromChicago(page, limit)
            val htmlContent = buildArtworkHtml(artworkResponse)

            call.respondText(htmlContent, contentType = io.ktor.http.ContentType.Text.Html)
        }
    }
}

// Build HTML for artworks, including image and title
fun buildArtworkHtml(response: ArtworkResponse): String {
    val artworksHtml = response.data.joinToString(separator = "\n") { artwork ->
        val imageUrl = artwork.image_id?.let { "https://www.artic.edu/iiif/2/$it/full/843,/0/default.jpg" }
        """
        <div>
            <h2>${artwork.title}</h2>
            ${if (imageUrl != null) "<img src=\"$imageUrl\" alt=\"${artwork.title}\" style=\"max-width:300px;\">" else "<p>No image available</p>"}
        </div>
        """.trimIndent()
    }

    return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Artworks</title>
        <style>
            body { font-family: Arial, sans-serif; }
            div { margin-bottom: 20px; }
            h2 { color: #333; }
            img { border: 1px solid #ccc; border-radius: 8px; }
        </style>
    </head>
    <body>
        <h1>Artworks</h1>
        $artworksHtml
    </body>
    </html>
    """.trimIndent()
}



fun buildArtistHtml(response: ArtistResponse): String {
    val artistsHtml = response.data.joinToString(separator = "\n") { artist ->
        """
        <div>
            <h2>${artist.title}</h2>
            <p><a href="${artist.api_link}" target="_blank">View Profile</a></p>
            <p>${artist.description ?: "No description available."}</p>
        </div>
        """.trimIndent()
    }

    return """
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Artists</title>
        <style>
            body { font-family: Arial, sans-serif; }
            div { margin-bottom: 20px; }
            h2 { color: #333; }
            p { margin: 5px 0; }
        </style>
    </head>
    <body>
        <h1>Artists</h1>
        $artistsHtml
    </body>
    </html>
    """.trimIndent()
}

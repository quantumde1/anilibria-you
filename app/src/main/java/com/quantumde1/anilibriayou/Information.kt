package com.quantumde1.anilibriayou

data class TitleListResponse(
    val list: List<Title>
)

data class Title(
    val id: Int,
    val code: String,
    val names: Names,
    val posters: Posters,
    // ... include other fields as necessary
)

data class Names(
    val ru: String,
    val en: String,
    val alternative: String?
)

data class Posters(
    val small: Poster,
    val medium: Poster,
    val original: Poster
)

data class Poster(
    val url: String,
    val raw_base64_file: String?
)

data class Pagination(
    val pages: Int,
    val current_page: Int,
    val items_per_page: Int,
    val total_items: Int
)
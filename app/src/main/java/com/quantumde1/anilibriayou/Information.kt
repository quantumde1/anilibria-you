package com.quantumde1.anilibriayou

import com.google.gson.annotations.SerializedName

data class TitleListResponse(
    val list: List<Title>
)


data class Episode(
    val episode: Int,
    val name: String?,
    val uuid: String,
    val created_timestamp: Long,
    val preview: String,
    val skips: Skips,
    val hls: HLS
)

data class Skips(
    val opening: List<Int>?,
    val ending: List<Int>?
)

data class HLS(
    val fhd: String,
    val hd: String,
    val sd: String
)

data class Title(
    val id: Int,
    val code: String,
    val names: Names,
    val posters: Posters,
    val description: String,
    val season: Season,
    val list: Map<String, Episode>,
    @SerializedName("type")
    val type: Type,
    val player: Player?,

    // ... include other fields as necessary
)

data class Type (
    @SerializedName("full_string")
    val fullString: String,

    @SerializedName("code")
    val code: Int,

    @SerializedName("string")
    val string: String,

    @SerializedName("episodes")
    val episodes: Int,

    @SerializedName("length")
    val length: Int
)

data class Season(
    val string: String,
    val code: Int,
    val year: Int,
    val week_day: Int
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
data class Player(
    val list: Map<String, Episode>?
)
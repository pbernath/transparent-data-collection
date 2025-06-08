package com.thesisproject.thesis_vt25.data.model

//data class Contract(val id: Int, val title: String, val overview: String, val description: String, val reward: Int)

//Assuming that a data class would be kinda similar to that of a DTO, simply an object used to transfer data
//defaulted id to 0
data class Contract(
    val id: String,
    val organization:String,
    val interactionId: String? = null,
    val title: String,
    val overview: String,
    val reward: Int,
    val description: String? = null,
    val permissions: List<String>,
    val start: String,
    val end: String,
    val created: String? = null,
    val finished: String? = null,
    val accepted: Boolean? = null,
    val interrupted: Boolean? = null,
    val ignored: Boolean? = null,
    val status: String?= null

)

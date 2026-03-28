package com.umschool.umtasktracker.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(val access: String, val refresh: String)

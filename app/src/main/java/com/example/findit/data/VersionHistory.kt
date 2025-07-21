package com.example.findit.data

data class VersionHistory(
    val message : String,
    val versionName : String,
    val versionCode : Int,
    val forceUpdate : Boolean,
    val url : String
)

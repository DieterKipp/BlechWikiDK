package com.example.blechwikidk.model

data class FundstellenMod (
    var buch : String,
    var nr : Int,
    var titel : String,
    var zus : String?,
    var komponist : String?,
    var besetzung : String?,
    var vorzeichen : String?,
    var titelZ : String?,
    var buchId : Int,
    var quellekurz : String
)

data class FundstellenRecyclerMod (
    var buch : String,
    var nr : Int,
    var titel : String,
    var zus : String?,
    var komponist : String?,
    var besetzung : String?,
    var vorzeichen : String?,
    var titelZ : String?,
    var buchId : Int,
    var quellekurz : String,
    var vorhanden: String
)
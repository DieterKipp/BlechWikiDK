package com.example.blechwikidk.model

data class BuchRCMod   (
    var buchId : Int,
    var buchkurz : String,
    var buch : String,
    var untertitel : String,
    var vorhanden : String
)

data class BuchFromURL (
    var buchId : Int,
    var buchkurz : String,
    var buch : String,
    var untertitel : String,
    var erscheinjahr : String,
    var herausgeber : String,
    var herausgvorname : String,
    var verlag : String,
    var verlagsnummer : String,
    var zulieferung : String,
    var relevanz : Int,
    val vorhanden: String,
    val change : String,
    val changecounter : Int
)



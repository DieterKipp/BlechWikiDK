package com.example.blechwikidk.model

data class EGLiedMod(
    var nr: Int,
    var nr2: Int,
    var lied: String,
    var anlass: String,
    var ix: Int,
    var ixUr: Int,
    var egTeil: String
)

data class EGLKAMod(

    var lka: String,
    var lkalang: String
)

data class EGTextsearchMod(

    var rank: Int,
    var lied: String,
    var ix: Int,
    var ixUr: Int
)
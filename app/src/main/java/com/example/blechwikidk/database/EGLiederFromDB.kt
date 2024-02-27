package com.example.blechwikidk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class EGLiederFromDB(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val ix: Int,
    @ColumnInfo val ixUr: Int,
    @ColumnInfo val nr: Int,
    @ColumnInfo val nr2: Int,
    @ColumnInfo val lied: String,
    @ColumnInfo val anlass: String,
    @ColumnInfo val egTeil: String
)

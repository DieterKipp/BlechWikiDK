package com.example.blechwikidk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class KomponistFromDB (
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val id: Int,
    @ColumnInfo val kurz : String,
    @ColumnInfo val komponist : String,
    @ColumnInfo val friendlyKomponistName : String
)
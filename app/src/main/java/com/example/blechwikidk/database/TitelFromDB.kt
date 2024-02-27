package com.example.blechwikidk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TitelFromDB (
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val titelohneKomma: String,
    @ColumnInfo val titel : String,
    @ColumnInfo val ix : Int

)
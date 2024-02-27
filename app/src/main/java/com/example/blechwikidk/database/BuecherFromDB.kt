package com.example.blechwikidk.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class BuecherFromDB (
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo val buchId : Int,
    @ColumnInfo val buchkurz : String,
    @ColumnInfo val buch : String,
    @ColumnInfo val untertitel : String,
    @ColumnInfo val erscheinjahr : String,
    @ColumnInfo val herausgeber : String,
    @ColumnInfo val herausgvorname : String,
    @ColumnInfo val verlag : String,
    @ColumnInfo val verlagsnummer : String,
    @ColumnInfo val zulieferung : String,
    @ColumnInfo val relevanz : Int,
    @ColumnInfo val vorhanden : String
)
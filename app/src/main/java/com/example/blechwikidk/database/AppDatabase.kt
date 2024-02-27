package com.example.blechwikidk.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        EGLiederFromDB::class,
        KomponistFromDB::class,
        TitelFromDB::class,
        BuecherFromDB::class],
    version = 13,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun egliederDAO(): EGLiederDAO
    abstract fun komponistDAO(): KomponistDAO
    abstract fun titelDAO(): TitelDAO
    abstract fun buecherDAO(): BuecherDAO
}

//https://medium.com/@kirillsuslov/how-to-add-more-that-one-entity-in-room-5cc3743219c0

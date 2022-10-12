package com.example.currencycalculator.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities= [CurrencyEntity::class], version = 1)
abstract class Db: RoomDatabase() {
    abstract fun DataDao(): DataDao

    companion object {
        @Volatile
        private var instance: Db? = null
        fun getInstance(context: Context): Db? {
            if (instance == null) {
                synchronized(this) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        Db::class.java, "database"
                    ).build()
                }
            }
            return instance
        }
    }
}
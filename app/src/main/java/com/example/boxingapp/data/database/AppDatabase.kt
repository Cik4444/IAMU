package com.example.boxingapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.boxingapp.data.dao.DivisionDao
import com.example.boxingapp.data.dao.FighterDao
import com.example.boxingapp.data.entity.DivisionEntity
import com.example.boxingapp.data.entity.FighterEntity

@Database(
    entities = [FighterEntity::class, DivisionEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun fighterDao(): FighterDao
    abstract fun divisionDao(): DivisionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "boxing_app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

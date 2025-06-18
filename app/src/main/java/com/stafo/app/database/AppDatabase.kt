package com.stafo.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stafo.app.database.dao.LocationDao
import com.stafo.app.database.dataClass.LocationEntity

@Database(entities = [LocationEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val MIGRATION_2_3= object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                try {
                    database.execSQL("ALTER TABLE location_table ADD COLUMN isGpsTurn INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("ALTER TABLE location_table ADD COLUMN deviceName TEXT NOT NULL DEFAULT ''")
                    database.execSQL("ALTER TABLE location_table ADD COLUMN batteryPercentage INTEGER NOT NULL DEFAULT 0")
                    database.execSQL("ALTER TABLE location_table ADD COLUMN androidVersion TEXT NOT NULL DEFAULT ''")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "geo_location_db"
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

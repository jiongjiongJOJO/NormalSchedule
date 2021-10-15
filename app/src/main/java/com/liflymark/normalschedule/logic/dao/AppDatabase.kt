package com.liflymark.normalschedule.logic.dao

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.HomeworkBean
import com.liflymark.normalschedule.logic.bean.UserBackgroundBean

@Database(
    version = 4,
    entities = [CourseBean::class, UserBackgroundBean::class, HomeworkBean::class],
    autoMigrations = [AutoMigration(from = 2, to = 3), AutoMigration(from = 3, to = 4, spec = AppDatabase.DeleteId::class)]
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseOriginalDao
    abstract fun backgroundDao(): BackgroundDao
    abstract fun homeworkDao(): HomeworkDao

    @DeleteColumn(columnName = "id", tableName = "CourseBean")
    class DeleteId : AutoMigrationSpec { }

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2){
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("create table UserBackgroundBean (id integer primary key autoincrement not null, userBackground text not null)")
            }
        }
        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java, "app_database.db")
                    .addMigrations(MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build().apply {
                    instance = this
                    }
        }
    }
}

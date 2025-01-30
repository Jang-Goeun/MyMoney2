package com.mobile.myMoney.data.database.ExpenseBudget

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExpenseBudget::class], version = 1)
abstract class ExpenseBudgetDatabase : RoomDatabase() {
    abstract fun expenseBudgetDao(): ExpenseBudgetDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseBudgetDatabase? = null

        fun getDatabase(context: Context): ExpenseBudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseBudgetDatabase::class.java,
                    "expenseBudget_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
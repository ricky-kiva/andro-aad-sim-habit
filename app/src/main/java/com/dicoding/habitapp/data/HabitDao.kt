package com.dicoding.habitapp.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.sqlite.db.SupportSQLiteQuery

// XTODO 2 : Define data access object (DAO)
interface HabitDao {

    @Query("SELECT * FROM habit")
    fun getHabits(query: SupportSQLiteQuery): DataSource.Factory<Int, Habit>

    @Query("SELECT * FROM habit WHERE id = :habitId")
    fun getHabitById(habitId: Int): LiveData<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg habits: Habit)

    @Query("DELETE FROM habit WHERE id = :habitId")
    fun deleteHabit(habitId: Int)

    @Query("SELECT * FROM habits WHERE priorityLevel = :level ORDER BY RANDOM() LIMIT 1")
    fun getRandomHabitByPriorityLevel(level: String): LiveData<Habit>
}

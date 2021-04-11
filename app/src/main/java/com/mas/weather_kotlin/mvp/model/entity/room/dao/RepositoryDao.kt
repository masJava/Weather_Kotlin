package com.mas.weather_kotlin.mvp.model.entity.room.dao

import androidx.room.*
import com.mas.weather_kotlin.mvp.model.entity.room.RoomGithubRepository

@Dao
interface RepositoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repository: RoomGithubRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg repository: RoomGithubRepository)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repositories: List<RoomGithubRepository>)

    @Update
    fun update(repository: RoomGithubRepository)

    @Update
    fun update(vararg repository: RoomGithubRepository)

    @Update
    fun update(repositories: List<RoomGithubRepository>)

    @Delete
    fun delete(repository: RoomGithubRepository)

    @Delete
    fun delete(vararg repository: RoomGithubRepository)

    @Delete
    fun delete(repositories: List<RoomGithubRepository>)

    @Query("SELECT * FROM RoomGithubRepository")
    fun getAll(): List<RoomGithubRepository>

    @Query("SELECT * FROM RoomGithubRepository WHERE userId = :userId")
    fun findByUser(userId: String): List<RoomGithubRepository>


}
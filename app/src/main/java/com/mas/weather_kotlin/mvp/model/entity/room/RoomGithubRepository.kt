package com.mas.weather_kotlin.mvp.model.entity.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = RoomGithubUser::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class RoomGithubRepository(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val forks_count: Int,
    val userId: String
)

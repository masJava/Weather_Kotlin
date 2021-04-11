package com.mas.weather_kotlin.mvp.model.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RoomGithubUser(
    @PrimaryKey val id: String,
    val login: String,
    val avatarUrl: String,
    val reposUrl: String
)

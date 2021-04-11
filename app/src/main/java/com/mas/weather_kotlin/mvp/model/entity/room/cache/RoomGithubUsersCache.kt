package com.mas.weather_kotlin.mvp.model.entity.room.cache

import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.room.RoomGithubUser
import com.mas.weather_kotlin.mvp.model.entity.room.db.Database

class RoomGithubUsersCache(val db: Database) : IGithubUsersCache {
    override fun insert(users: List<GithubUser>) {
        val roomUser = users.map { user ->
            RoomGithubUser(user.id, user.login, user.avatarUrl, user.reposUrl)
        }
        db.userDao.insert(roomUser)
    }

    override fun getAll(): List<GithubUser> {
        return db.userDao.getAll().map { roomUser ->
            GithubUser(roomUser.id, roomUser.login, roomUser.avatarUrl, roomUser.reposUrl)
        }
    }
}
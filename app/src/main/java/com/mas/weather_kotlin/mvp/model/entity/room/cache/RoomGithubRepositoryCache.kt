//package com.mas.weather_kotlin.mvp.model.entity.room.cache
//
//import com.mas.weather_kotlin.mvp.model.entity.GithubUser
//import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
//import com.mas.weather_kotlin.mvp.model.entity.room.RoomGithubRepository
//import com.mas.weather_kotlin.mvp.model.entity.room.db.Database
//
//class RoomGithubRepositoryCache(val db: Database) : IGithubRepositoryCache {
//    override fun insert(
//        repositories: List<GithubUserRepository>,
//        user: GithubUser
//    ) {
//        val roomRepos = repositories.map { repo ->
//            val roomUser = db.weatherDao.findByLogin(user.login)
//                ?: throw RuntimeException("Нет пользователя в БД")
//            RoomGithubRepository(
//                repo.id,
//                repo.name,
//                repo.description,
//                repo.htmlUrl,
//                repo.forks_count,
//                roomUser.id
//            )
//        }
//        db.repositoryDao.insert(roomRepos)
//    }
//
//    override fun getByUser(user: GithubUser): List<GithubUserRepository> {
//        val roomUser = db.weatherDao.findByLogin(user.login)
//            ?: throw RuntimeException("Нет пользователя в БД")
//        return db.repositoryDao.findByUser(roomUser.id).map { roomRepo ->
//            GithubUserRepository(
//                roomRepo.id,
//                roomRepo.name,
//                roomRepo.description,
//                roomRepo.htmlUrl,
//                roomRepo.forks_count
//            )
//        }
//    }
//}
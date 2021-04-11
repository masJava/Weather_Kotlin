package com.mas.weather_kotlin.mvp.presenter

import android.util.Log
import com.github.terrakok.cicerone.Router
import com.mas.weather_kotlin.mvp.model.entity.GithubUser
import com.mas.weather_kotlin.mvp.model.entity.GithubUserRepository
import com.mas.weather_kotlin.mvp.navigation.IScreens
import com.mas.weather_kotlin.mvp.presenter.list.IRepoListPresenterDaily
import com.mas.weather_kotlin.mvp.repo.IGithubUsersRepo
import com.mas.weather_kotlin.mvp.view.UsersInfoView
import com.mas.weather_kotlin.mvp.view.list.IRepoItemView
import io.reactivex.rxjava3.core.Scheduler
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class UsersInfoPresenter(private val user: GithubUser) :
    MvpPresenter<UsersInfoView>() {

    @Inject
    lateinit var router: Router
    @Inject
    lateinit var screens: IScreens
    @Inject
    lateinit var repos: IGithubUsersRepo
    @field:Named("mainThread")
    @Inject
    lateinit var uiScheduler: Scheduler

    class ReposListPresenterDaily : IRepoListPresenterDaily {
        val repos = mutableListOf<GithubUserRepository>()
        override var itemClickListener: ((IRepoItemView) -> Unit)? = null

        override fun bindView(view: IRepoItemView) {
            val repo = repos[view.pos]
            view.setName(repo.name)
        }

        override fun getCount() = repos.size
    }

    val reposListPresenter = ReposListPresenterDaily()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setLogin(user.login)
        viewState.setAvatar(user.avatarUrl)
        viewState.init()
        loadData(user)

//        reposListPresenter.itemClickListener = { view ->
//            val repo = reposListPresenter.repos[view.pos]
//            Log.d(
//                "my", "name: ${repo.name} " +
//                        "\ndescription:${repo.description} " +
//                        "\nforks_count:${repo.forks_count}"
//            )
//            router.navigateTo(screens.repoInfo(repo))
//        }
    }

    private fun loadData(user: GithubUser) {

        repos.getUsersRepository(user)
            .observeOn(uiScheduler)
            .subscribe(
                { repos ->
                    reposListPresenter.repos.clear()
                    reposListPresenter.repos.addAll(repos)
                    viewState.updateList()
                },
                { t -> Log.d("my", t.message.toString()) })

    }


    fun backClick(): Boolean {
        Log.d("my", "UserInfoPres")
        router.exit()
        return true
    }
}
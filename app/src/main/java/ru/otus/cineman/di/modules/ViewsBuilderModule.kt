package ru.otus.cineman.di.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.otus.cineman.presentation.view.activity.MainActivity
import ru.otus.cineman.presentation.view.fragment.MoviesListFavoriteFragment
import ru.otus.cineman.presentation.view.fragment.MoviesListFragment
import ru.otus.cineman.presentation.view.fragment.WatchLaterFragment
import ru.otus.cineman.service.ImageLoader

@Module
abstract class ViewsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeMoviesListFragment(): MoviesListFragment

    @ContributesAndroidInjector
    abstract fun contributeMoviesListFavoriteFragment(): MoviesListFavoriteFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchLaterListFragment(): WatchLaterFragment

    @ContributesAndroidInjector
    abstract fun contributeImageLoader(): ImageLoader
}
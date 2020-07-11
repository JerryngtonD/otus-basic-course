package ru.otus.cineman.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.otus.cineman.presentation.viewmodel.MovieListViewModel
import ru.otus.cineman.presentation.viewmodel.NavigationDrawerViewModel

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MovieListViewModel::class)
    internal abstract fun movieListViewModelProvider(filmsListViewModel: MovieListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NavigationDrawerViewModel::class)
    internal abstract fun navigationDrawerViewModelProvider(navigationDrawerViewModel: NavigationDrawerViewModel): ViewModel
}
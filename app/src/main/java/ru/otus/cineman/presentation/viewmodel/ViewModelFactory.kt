package ru.otus.cineman.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    val context: Context?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NavigationDrawerViewModel::class.java) -> NavigationDrawerViewModel(context!!) as T
            modelClass.isAssignableFrom(MovieListViewModel::class.java) -> MovieListViewModel() as T
            else -> {
                throw Exception("Unknown class")
            }
        }
    }
}
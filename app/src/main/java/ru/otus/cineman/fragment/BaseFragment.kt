package ru.otus.cineman.fragment

import androidx.fragment.app.Fragment


open class BaseFragment : Fragment(), OnBackPressed {
    override fun onBackPressed() {}
}

interface OnBackPressed {
    fun onBackPressed()
}
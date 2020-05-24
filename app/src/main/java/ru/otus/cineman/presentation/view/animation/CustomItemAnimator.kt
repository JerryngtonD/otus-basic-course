package ru.otus.cineman.presentation.view.animation

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import ru.otus.cineman.R

class CustomItemAnimator() : DefaultItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.itemView?.animation = AnimationUtils.loadAnimation(holder?.itemView?.context, R.anim.viewholder_add_anim)
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        return super.animateRemove(holder)
    }
}
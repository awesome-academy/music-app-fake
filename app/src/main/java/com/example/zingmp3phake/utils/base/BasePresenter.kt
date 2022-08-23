package com.example.zingmp3phake.utils.base

interface BasePresenter<T> {
    fun onStart()
    fun onStop()
    fun setView(view: T?)
}

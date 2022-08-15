package com.example.zingmp3phake.data.repo.resource

interface Listener<T> {
    fun onSuccess(list: T)
    fun onFail(msg: String)
}

package com.aaronseaton.accounts.domain.model

data class LoadingState(val status: Status, val msg: String? = null) {
    companion object {
        val LOADED = LoadingState(Status.SUCCESS)
        val IDLE = LoadingState(Status.IDLE)
        val LOADING = LoadingState(Status.LOADING)
        val LOGGED_IN = LoadingState(Status.LOGGED_IN)
        val LOGGED_OUT = LoadingState(Status.LOGGED_OUT)
        fun error(msg: String?) = LoadingState(Status.FAILED, msg)
    }

    enum class Status {
        LOADING,
        SUCCESS,
        FAILED,
        IDLE,
        LOGGED_IN,
        LOGGED_OUT
    }
}
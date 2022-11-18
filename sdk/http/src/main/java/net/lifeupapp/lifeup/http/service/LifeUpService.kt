package net.lifeupapp.lifeup.http.service

import kotlinx.coroutines.flow.StateFlow

interface LifeUpService {

    enum class RunningState(val value: Int) {
        NOT_RUNNING(0),
        STARTING(1),
        RUNNING(2)
    }

    val isRunning: StateFlow<RunningState>

    val errorMessage: StateFlow<Throwable?>

    fun start()

    fun stop()
}
package com.reditus.novelcia.global.util

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


@Component
class AsyncTaskExecutor {
    @Async
    operator fun invoke(function: ()-> Unit) {
        return function()
    }
}

@Component
class AsyncHelper(
    private val asyncTaskExecutor: AsyncTaskExecutor
) {
    init {
        _asyncTaskExecutor = asyncTaskExecutor
    }
    companion object{
        lateinit var _asyncTaskExecutor: AsyncTaskExecutor
        fun  executeAsync(function: ()-> Unit) {
            return _asyncTaskExecutor(function)
        }
    }
}

fun executeAsync(function: ()-> Unit) {
    return AsyncHelper.executeAsync(function)
}
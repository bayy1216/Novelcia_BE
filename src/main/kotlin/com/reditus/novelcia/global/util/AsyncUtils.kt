package com.reditus.novelcia.global.util

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

interface AsyncTaskExecutor {
    operator fun invoke(function: ()-> Unit)
}

@Component
class SpringAsyncTaskExecutor: AsyncTaskExecutor {
    @Async
    override operator fun invoke(function: ()-> Unit) {
        return function()
    }
}

@Component
class AsyncHelper(
    private val _asyncTaskExecutor: AsyncTaskExecutor
) {
    init {
        asyncTaskExecutorInit = _asyncTaskExecutor
    }
    companion object{
        private lateinit var asyncTaskExecutorInit: AsyncTaskExecutor
        val asyncTaskExecutor by lazy { asyncTaskExecutorInit }
    }
}

fun executeAsync(
    asyncExecutor: AsyncTaskExecutor = AsyncHelper.asyncTaskExecutor,
    function: ()-> Unit
) {
    asyncExecutor(function)
}
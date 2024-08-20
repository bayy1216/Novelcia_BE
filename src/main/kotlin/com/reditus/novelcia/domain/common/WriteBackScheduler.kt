package com.reditus.novelcia.domain.common

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WriteBackScheduler(
    private val writeBackManagers : List<WriteBackManager<*>>
) {
    @Scheduled(fixedRate = 1000)
    fun scheduleFlush() {
        writeBackManagers.forEach {
            it.flush()
        }
    }

}

package com.reditus.novelcia.common.domain

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class WriteBackScheduler(
    private val writeBackManagers : List<WriteBackManager<*>>
) {
    @Scheduled(fixedRateString = "\${write-back.scheduler.interval:3000}")
    fun scheduleFlush() {
        writeBackManagers.forEach {
            it.flush()
        }
    }

}

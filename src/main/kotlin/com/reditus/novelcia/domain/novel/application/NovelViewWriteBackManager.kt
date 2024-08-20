package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.common.WriteBackManager
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.novel.port.NovelWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component


@Component
class NovelViewWriteBackManager(
    @Value("\${write-back.flush-size.novel:100}")
    override val flushSize: Int,
    private val novelWriter: NovelWriter,
) : WriteBackManager<EpisodeView> {
    /**
     * 1. `entity`를 `writeBackNovelIdCountMap`에 추가한다.
     * 2. 해당 `novelId`의 count가 `flushSize`를 넘으면 `flush`를 호출한다.
     *
     * `synchronized block`이 있으므로 `Async`로 처리한다.
     */
    @Async
    override fun save(entity: EpisodeView) {
        val shouldFlush: Boolean
        synchronized(writeBackNovelIdCountMap) {
            val count = writeBackNovelIdCountMap.getOrDefault(entity.novelId, 0) + 1
            writeBackNovelIdCountMap[entity.novelId] = count

            shouldFlush = count >= flushSize
        }
        if (shouldFlush) {
            flush()
        }
    }

    /**
     * 1. lock 내부에서 방어적복사로 `writeBackNovelIdCountMap`을 복사한다.
     * 2. 복사 후, lock이 해제되고 snapshot을 이용해 `novelWriter.addViewCount`를 호출한다.
     */
    override fun flush() {
        val writeMapSnapshot = synchronized(writeBackNovelIdCountMap) {
            writeBackNovelIdCountMap.toMap().also { writeBackNovelIdCountMap.clear() }
        }

        writeMapSnapshot.forEach { (novelId, count) ->
            novelWriter.addViewCount(novelId, PositiveInt(count))
        }
    }


    companion object {
        val writeBackNovelIdCountMap = mutableMapOf<Long, Int>() // Map<NovelId, Count>
    }

}
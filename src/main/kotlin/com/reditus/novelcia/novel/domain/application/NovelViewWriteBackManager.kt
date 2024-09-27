package com.reditus.novelcia.novel.domain.application

import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.common.domain.WriteBackManager
import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.novel.domain.port.NovelWriter
import com.reditus.novelcia.global.util.executeAsync
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


/**
 * `EpisodeView`의 조회수를 `NovelWriter`를 통해 `flushSize`만큼 쌓아두었다가 `flush`를 호출하는 클래스
 * - save : 비동기로 작동하며, novelId를 key로 조회수를 쌓아둔다.
 *   해당 key에 대한 조회수가 `flushSize`를 넘으면 `flush`를 호출한다.
 * - flush : 조회수를 `NovelWriter`를 통해 저장하고, `writeBackNovelIdCountMap`을 비운다.
 *   스케줄러와 같은 방법으로 주기적으로 호출되어야 한다.
 */
@Component
class NovelViewWriteBackManager(
    @Value("\${write-back.flush-size.novel:100}")
    override val flushSize: Int,
    private val novelWriter: NovelWriter,
) : WriteBackManager<EpisodeView> {
    private val writeBackNovelIdCountMap = mutableMapOf<Long, Int>() // Map<NovelId, Count>

    /**
     * 1. `entity`를 `writeBackNovelIdCountMap`에 추가한다.
     * 2. 해당 `novelId`의 count가 `flushSize`를 넘으면 `flush`를 호출한다.
     *
     */
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
        if (writeBackNovelIdCountMap.isEmpty()) return // 비어있으면 early return

        log.info("NovelViewWriteBackManager flush triggered")
        val writeMapSnapshot = synchronized(writeBackNovelIdCountMap) {
            writeBackNovelIdCountMap.toMap().also { writeBackNovelIdCountMap.clear() }
        }

        writeMapSnapshot.forEach { (novelId, count) ->
            novelWriter.addViewCount(novelId, PositiveInt(count))
        }
    }


    companion object {
        val log = LoggerFactory.getLogger(this::class.java)
    }

}
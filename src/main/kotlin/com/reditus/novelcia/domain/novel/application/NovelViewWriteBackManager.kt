package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.common.WriteBackManager
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.novel.port.NovelWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class NovelViewWriteBackManager(
    @Value("\${novel.write-back.flush-interval-millis:1000}")
    override val flushIntervalMillis: Long,
    @Value("\${novel.write-back.flush-size:100}")
    override val flushSize: Int,
    private val novelWriter: NovelWriter,
) : WriteBackManager<EpisodeView> {
    /**
     * 1. `entity`를 `writeBackList`에 추가한다.
     * 2. `writeBackList`의 크기가 `flushSize`보다 크거나 같으면 `flush`를 호출한다.
     */
    override fun save(entity: EpisodeView) {
        val shouldFlush: Boolean
        synchronized(writeBackList) {
            lastSaveMillis = System.currentTimeMillis()
            writeBackList.add(entity)
            shouldFlush = writeBackList.size >= flushSize
        }
        if(shouldFlush) {
            flush(force = true)
        }
    }

    /**
     * 1. flush 조건을 확인한다. force는 통과, 그 이외에는 시간 확인
     * 2. `writeBackList`에 lock를 걸고 novelId별고 count를 DB에 저장한다.
     */
    override fun flush(force: Boolean) {
        if(!checkFlush(force)) {
            return
        }

        lastFlushMillis =  System.currentTimeMillis()
        synchronized(writeBackList) {
            val novelIdAndCount = writeBackList.groupingBy { it.novelId }.eachCount()
            novelIdAndCount.forEach { (novelId, count) ->
                novelWriter.addViewCount(novelId, PositiveInt(count))
            }
            writeBackList.clear()
        }
    }

    private fun checkFlush(force: Boolean) :Boolean =
        force || lastFlushMillis + flushIntervalMillis > System.currentTimeMillis()

    companion object {
        val writeBackList = mutableListOf<EpisodeView>()
        var lastFlushMillis: Long = System.currentTimeMillis()
        var lastSaveMillis: Long = System.currentTimeMillis()
    }

}
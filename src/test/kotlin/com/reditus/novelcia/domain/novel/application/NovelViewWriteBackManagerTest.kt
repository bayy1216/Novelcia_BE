package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.port.NovelWriter
import com.reditus.novelcia.domain.user.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.atomic.AtomicInteger

class NovelViewWriteBackManagerTest {
    val novelViewWriteBackManager = NovelViewWriteBackManager(
        100,
        NovelWriterSpy()
    )
    val user = User.fixture()

    val epList = (1..10).map {

        val novel = Novel.fixture(id= it.toLong(), author = user)
        val episode = Episode.fixture(novel = novel)
        EpisodeView(user = user, episode = episode, novel = novel)
    }

    @Test
    fun `정상적으로 save와 flush가 호출된다`() {
        (0..999).forEach {
            novelViewWriteBackManager.save(epList[it % 10])
        }

        NovelViewWriteBackManager.writeBackNovelIdCountMap.forEach { (novelId, count) ->
            println("novelId: $novelId, count: $count")
        }
        novelViewWriteBackManager.flush()
        NovelViewWriteBackManager.writeBackNovelIdCountMap.forEach { (novelId, count) ->
            println("after flush novelId: $novelId, count: $count")
        }
        assertEquals(10, NovelWriterSpy.addCountTriggered.size)
        assertEquals(1000, NovelWriterSpy.addCountTriggered.values.sum())


    }


    @Test
    fun flush() {
    }
}

class NovelWriterSpy : NovelWriter {
    override fun save(novel: Novel): Novel {
        return novel
    }

    override fun delete(novelId: Long) {

    }

    override fun addViewCount(novelId: Long, count: PositiveInt) {
        synchronized(addCountTriggered){
            addCountTriggered[novelId] = addCountTriggered.getOrDefault(novelId, 0) + count.value
        }
    }

    companion object {
        var addCountTriggered = mutableMapOf<Long, Int>()
    }
}
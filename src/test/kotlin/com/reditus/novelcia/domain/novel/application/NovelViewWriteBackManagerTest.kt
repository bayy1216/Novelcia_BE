package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.port.NovelWriter
import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.global.util.AsyncHelper
import com.reditus.novelcia.global.util.AsyncTaskExecutor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NovelViewWriteBackManagerTest {
    @BeforeEach
    fun setup() {
        // SpringContext에 등록된 Bean을 사용하지 않고, 직접 주입하여 테스트
        AsyncHelper.asyncTaskExecutor = TestSyncExecutor()
    }

    private val novelViewWriteBackManager = NovelViewWriteBackManager(
        100,
        TestNovelWriterSpy()
    )
    private val user = User.fixture()

    private val epList = (1..10).map {
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
        assertEquals(10, TestNovelWriterSpy.addCountTriggered.size)
        assertEquals(1000, TestNovelWriterSpy.addCountTriggered.values.sum())


    }

}

class TestNovelWriterSpy : NovelWriter {
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

class TestSyncExecutor : AsyncTaskExecutor {
    override fun invoke(function: () -> Unit) {
        function()
    }

}

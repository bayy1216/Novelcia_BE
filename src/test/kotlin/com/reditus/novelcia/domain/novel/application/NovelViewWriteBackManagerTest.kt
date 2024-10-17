//package com.reditus.novelcia.domain.novel.application
//
//import com.reditus.novelcia.common.domain.PositiveInt
//import com.reditus.novelcia.episode.domain.Episode
//import com.reditus.novelcia.episode.domain.EpisodeView
//import com.reditus.novelcia.novel.domain.Novel
//import com.reditus.novelcia.novel.application.port.NovelWriter
//import com.reditus.novelcia.user.domain.User
//import com.reditus.novelcia.global.util.AsyncHelper
//import com.reditus.novelcia.global.util.AsyncTaskExecutor
//import com.reditus.novelcia.novel.application.NovelViewWriteBackManager
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Tag
//import org.junit.jupiter.api.Test
//import org.slf4j.LoggerFactory
//import java.util.concurrent.atomic.AtomicInteger
//
//@Tag("small")
//class NovelViewWriteBackManagerTest {
//    private lateinit var asyncHelper: AsyncHelper
//
//    private val syncExecutor = object : AsyncTaskExecutor {
//        override fun invoke(function: () -> Unit) {
//            function()
//        }
//    }
//
//    @BeforeEach
//    fun setUp() {
//        asyncHelper = AsyncHelper(syncExecutor)
//    }
//
//    private val novelViewWriteBackManager = NovelViewWriteBackManager(
//        100,
////        TestNovelWriterSpy()
//    )
//    private val user = User.fixture()
//
//    private val epList = (1..10).map {
//        val novel = Novel.fixture(id= it.toLong(), author = user)
//        val episode = Episode.fixture(novel = novel)
//        EpisodeView(user = user, episode = episode, novel = novel)
//    }
//
//
//
//    @Test
//    fun `정상적으로 save와 flush가 호출된다`() {
//        (0..999).forEach {
//            novelViewWriteBackManager.save(epList[it % 10])
//        }
//
//        novelViewWriteBackManager.flush()
//
//
////        assertEquals(1000, TestNovelWriterSpy.addCountTriggered.get(), {"write back이 정상적으로 1000번 호출"})
//    }
//
//}

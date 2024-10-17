package com.reditus.novelcia.domain.episode.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.episode.domain.EpisodeCommand
import com.reditus.novelcia.episode.application.EpisodePagingSort
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.ReadAuthority
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.episode.application.model.EpisodeModel
import com.reditus.novelcia.episode.application.EpisodeQueryService
import com.reditus.novelcia.episode.application.EpisodeService
import com.reditus.novelcia.global.util.AsyncHelper
import com.reditus.novelcia.global.util.AsyncTaskExecutor
import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeViewRepository
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest

@Tag("medium")
@SpringBootTest
class EpisodeQueryServiceTest @Autowired constructor(
    private val episodeService: EpisodeService,
    private val episodeQueryService: EpisodeQueryService,
    private val userRepository: UserRepository,
    private val novelRepository: NovelRepository,
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
    private val episodeRepository: EpisodeRepository,
    private val episodeViewRepository: EpisodeViewRepository,
) {
    private lateinit var asyncHelper: AsyncHelper

    private val syncExecutor = object : AsyncTaskExecutor {
        override fun invoke(function: () -> Unit) {
            function()
        }
    }
    @BeforeEach
    fun setUp() {
        asyncHelper = AsyncHelper(syncExecutor)
    }

    @AfterTest
    fun cleanUp() {
        episodeViewRepository.deleteAllInBatch()
        episodeRepository.deleteAllInBatch()
        novelRepository.deleteAllInBatch()
        userRepository.deleteAllInBatch()
    }

    @Test
    fun `에피소드 상세조회를 n번 할시, EpisodeModel_Meta의 조회수가 정상적으로 n만큼 증가한다`(){
        // given

            val user = userRepository.save(User.fixture())
            val novel = novelRepository.save(
                Novel.fixture(
                author = user,
            ))
            val episodeId = episodeService.createEpisode(
                userId = LoginUserId(user.id),
                novelId = novel.id,
                command = EpisodeCommand.Create(
                    title = "title",
                    content = "content",
                    authorComment = "authorComment",
                    readAuthority = ReadAuthority.FREE,
                )
            )
            val episode = episodeRepository.findByIdOrThrow(episodeId)


        // when
        repeat(5) {
            episodeQueryService.getEpisodeDetail(
                novel.id,
                episode.episodeNumber,
                LoginUserId(user.id)
            )
        }

        // Async 작업이 끝날때까지 대기
        val executor = threadPoolTaskExecutor.threadPoolExecutor
        executor.awaitTermination(1, TimeUnit.SECONDS)

        // then
        val episodePaging = episodeQueryService.getEpisodeModelsByOffsetPaging(
            userId = LoginUserId(user.id),
            novelId = novel.id,
            pageRequest = PageRequest.of(0, 10),
            sort = EpisodePagingSort.EPISODE_NUMBER_DESC,
        )


        val episodeMetaModel: EpisodeModel.Meta = episodePaging.first()

        assertEquals(5, episodeMetaModel.viewsCount, {"5번 상세 조회하면 조회수가 5가 된다"})
    }
}
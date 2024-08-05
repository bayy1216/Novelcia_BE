package com.reditus.novelcia.domain.episode.application

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.episode.EpisodeCommand
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.ReadAuthority
import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.infrastructure.novel.NovelRepository
import com.reditus.novelcia.infrastructure.user.UserRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.TimeUnit

@SpringBootTest
class EpisodeQueryServiceTest @Autowired constructor(
    private val episodeService: EpisodeService,
    private val episodeQueryService: EpisodeQueryService,
    private val userRepository: UserRepository,
    private val novelRepository: NovelRepository,
    private val threadPoolTaskExecutor: ThreadPoolTaskExecutor,
) {

    @Test
    fun `에피소드 상세조회를 n번 할시, EpisodeModel_Meta의 조회수가 정상적으로 n만큼 증가한다`() {
        // given
        val user = userRepository.save(User.fixture())
        val novel = novelRepository.save(Novel.fixture(
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

        // when
        repeat(5) {
            episodeQueryService.getEpisodeDetail(episodeId, LoginUserId(user.id))
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

        assertEquals(episodeMetaModel.viewsCount, 5)

    }
}
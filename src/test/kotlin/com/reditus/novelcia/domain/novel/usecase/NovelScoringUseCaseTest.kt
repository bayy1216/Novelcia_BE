package com.reditus.novelcia.domain.novel.usecase

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.episode.infrastructure.EpisodeLikeRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeViewRepository
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import com.reditus.novelcia.novel.application.usecase.NovelScoringUseCase
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Tag("medium")
@SpringBootTest
@Transactional
class NovelScoringUseCaseTest @Autowired constructor(
    private val novelScoringUseCase: NovelScoringUseCase,
    private val novelRepository: NovelRepository,
    private val episodeRepository: EpisodeRepository,
    private val userRepository: UserRepository,
    private val episodeLikeRepository: EpisodeLikeRepository,
    private val episodeViewRepository: EpisodeViewRepository,
) {

    @Test
    fun `NovelScoringUseCase 호출시에 오류가 발생하지 않는다`() {
        // given
        val user = userRepository.save(User.fixture(email = "test@user.com"))
        val novel = novelRepository.save(Novel.fixture(author = user))
        val episode = episodeRepository.save(Episode.fixture(novel = novel))
        val episodeView = episodeViewRepository.save(EpisodeView(user = user, episode = episode, novel = novel))

        // when
        val novelAndScores = novelScoringUseCase(1)

        // then
        assertAll(
            { assertEquals(1, novelAndScores.size, {"스코어링 결과로 novel이 정상적으로 리턴"}) },
            { assertEquals(novel.id, novelAndScores.first().novelId, {"novelId가 정상적으로 리턴"}) }
        )
    }
}
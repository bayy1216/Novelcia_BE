package com.reditus.novelcia.domain.novel.usecase

import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeView
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.infrastructure.episode.EpisodeLikeRepository
import com.reditus.novelcia.infrastructure.episode.EpisodeRepository
import com.reditus.novelcia.infrastructure.episode.EpisodeViewRepository
import com.reditus.novelcia.infrastructure.novel.NovelRepository
import com.reditus.novelcia.infrastructure.user.UserRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Tag("medium")
@SpringBootTest
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
            { assertEquals(1, novelAndScores.size) },
            { assertEquals(novel.id, novelAndScores.first().novelId) }
        )
    }
}
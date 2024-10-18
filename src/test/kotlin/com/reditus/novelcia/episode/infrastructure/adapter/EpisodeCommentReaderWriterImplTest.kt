package com.reditus.novelcia.episode.infrastructure.adapter

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episodecomment.domain.EpisodeComment
import com.reditus.novelcia.episodecomment.infrastructure.EpisodeCommentQueryRepository
import com.reditus.novelcia.episodecomment.infrastructure.EpisodeCommentRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.user.infrastructure.UserRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class EpisodeCommentReaderWriterImplTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val novelRepository: NovelRepository,
    private val episodeRepository: EpisodeRepository,
    private val episodeCommentRepository: EpisodeCommentRepository,
    private val episodeCommentQueryRepository: EpisodeCommentQueryRepository,
    private val em: EntityManager,
) {

    @Test
    @Transactional
    fun findByEpisodeIdPagingOrderByPath() {
        // given
        val user = userRepository.save(User.fixture())
        val novel = novelRepository.save(Novel.fixture(author = user))
        val episode = episodeRepository.save(Episode.fixture(novel = novel))

        val comment1 = EpisodeComment.fixture(episode = episode, user = user)
        val comment2 = EpisodeComment.fixture(episode = episode, user = user)

        val comment2_1 = EpisodeComment.fixture(episode = episode, user = user, parent = comment2)
        val comment2_2 = EpisodeComment.fixture(episode = episode, user = user, parent = comment2)

        val comment3 = EpisodeComment.fixture(episode = episode, user = user)
        val comment3_1 = EpisodeComment.fixture(episode = episode, user = user, parent = comment3)

        episodeCommentRepository.saveAll(listOf(comment1, comment2, comment3, comment2_1, comment2_2, comment3_1))
        em.flush()
        em.clear()

        // when
        val comments = episodeCommentQueryRepository.findByEpisodeIdPagingOrderByPath(episode.id, PageRequest.of(0, 10))

        // then
        assertAll(
            { assertEquals(6, comments.size) },
            { assertEquals(
                listOf(comment1.id, comment2.id, comment2_1.id, comment2_2.id, comment3.id, comment3_1.id),
                comments.map { it.id })
            },
        )
    }
}
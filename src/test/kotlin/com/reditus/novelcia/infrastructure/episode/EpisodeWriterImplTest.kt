package com.reditus.novelcia.infrastructure.episode

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.port.EpisodeWriter
import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Tag("small")
@SpringBootTest
@Transactional
class EpisodeWriterImplTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val novelRepository: NovelRepository,
    private val episodeRepository: EpisodeRepository,
    private val episodeWriter: EpisodeWriter,
    private val em: EntityManager,
) {

    @Test
    fun `에피소드 SoftDelete가 적상적으로 작동한다`() {
        val user = userRepository.save(User.fixture())
        val novel = novelRepository.save(Novel.fixture(author = user))
        val episodes = (1..10).map {
            Episode.fixture(novel = novel, title = "Episode $it", episodeNumber = it)
        }
        episodeRepository.saveAll(episodes)
        em.flush()
        em.clear()

        episodeWriter.deleteAllByNovelId(novel.id)

        val deletedEpisodes = episodeRepository.findAll()
        assertTrue(deletedEpisodes.all { it.isDeleted }, { "삭제 마크가 작동한다." })
    }
}
package com.reditus.novelcia.novel.application.usecase

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.infrastructure.EpisodeLikeQueryRepository
import com.reditus.novelcia.episode.infrastructure.EpisodeQueryRepository
import com.reditus.novelcia.episodecomment.domain.EpisodeComment
import com.reditus.novelcia.episodecomment.infrastructure.EpisodeCommentQueryRepository
import com.reditus.novelcia.episodeview.EpisodeView
import com.reditus.novelcia.episodeview.EpisodeViewRepository
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Component
import java.time.LocalDate


@Component
class ScoreDataSourceExtractor(
    private val episodeQueryRepository: EpisodeQueryRepository,
    private val episodeLikeQueryRepository: EpisodeLikeQueryRepository,
    private val episodeViewRepository: EpisodeViewRepository,
    private val episodeCommentQueryRepository: EpisodeCommentQueryRepository,
) {
    fun getByLocalDate(days: Int): ScoringMetaData = readOnly {
        val episodesAll = episodeQueryRepository.findEpisodesDaysBetweenByCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val likesAll = episodeLikeQueryRepository.findAllWithEpisodeByDaysBetweenCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val viewsAll = episodeViewRepository.findAllByDaysBetweenCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val commentsAll = episodeCommentQueryRepository.findAllWithEpisodeByDaysBetweenCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        return@readOnly ScoringMetaData(
            episodes = episodesAll,
            likes = likesAll,
            views = viewsAll,
            comments = commentsAll,
        )
    }

    data class ScoringMetaData(
        val episodes: List<Episode>,
        val likes: List<EpisodeLike>,
        val views: List<EpisodeView>,
        val comments: List<EpisodeComment>,
    ){
        operator fun component5() = totalNovelIds()

        private fun totalNovelIds(): Set<Long> {
            val novelIds = episodes.map { it.novelId }.toSet()
            val likesNovelIds = likes.map { it.episode.novelId }.toSet()
            val viewsNovelIds = views.map { it.novelId }.toSet()
            val commentsNovelIds = comments.map { it.episode.novelId }.toSet()
            return novelIds + likesNovelIds + viewsNovelIds + commentsNovelIds
        }
    }
}

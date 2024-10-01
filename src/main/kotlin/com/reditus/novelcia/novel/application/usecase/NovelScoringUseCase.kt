package com.reditus.novelcia.novel.application.usecase

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeComment
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episode.domain.EpisodeView
import com.reditus.novelcia.episode.application.port.EpisodeCommentReader
import com.reditus.novelcia.episode.application.port.EpisodeLikeReader
import com.reditus.novelcia.episode.application.port.EpisodeReader
import com.reditus.novelcia.episode.application.port.EpisodeViewReader
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Component
class NovelScoringUseCase(
    private val episodeReader: EpisodeReader,
    private val episodeLikeReader: EpisodeLikeReader,
    private val episodeViewReader: EpisodeViewReader,
    private val episodeCommentReader: EpisodeCommentReader,
) {
    /**
     * 최근 n일간의 소설을 스코어링하여 반환한다.
     * 1. 최근 n일간의 에피소드, 좋아요, 조회수, 댓글을 조회한다.
     * 2. 각 소설의 스코어를 계산한다.
     * 3. 스코어가 높은 순으로 정렬하여 반환한다.
     * @param days: 최근 n일간의 데이터를 조회한다.
     * @return List<NovelAndScore>: 소설 ID와 스코어를 담은 리스트를 score가 높은 순으로 정렬하여 반환한다.
     */
    operator fun invoke(
        days: Int,
    ): List<NovelIdAndScore> = readOnly {
        val (episodesAll, likesAll, viewsAll, commentsAll, totalNovelIdSet) = getScoringMetaByLocalDate(days)


        val globalAverageLikes = likesAll.groupBy { it.user.id }.size.toDouble() // 전체의 좋아요의 평균 점수

        val globalAverageViews = viewsAll.groupBy { it.user.id }.size.toDouble() // 전체의 조회수의 평균 점수
        val globalAverageComments = commentsAll.groupBy { it.user.id }.size.toDouble() // 전체의 댓글수의 평균 점수
        val regularizationFactor = likesAll.size + viewsAll.size + commentsAll.size

        val novelIdAndScorePair = totalNovelIdSet.map { novelId ->
            val episodes = episodesAll.filter { episode -> episode.novelId == novelId }
            val likes = likesAll.filter { like -> like.episode.novelId == novelId }
            val views = viewsAll.filter { view -> view.novelId == novelId }
            val comments = commentsAll.filter { comment -> comment.episode.novelId == novelId }

            // 시간 가중치 계산
            val timeDecay = if (episodes.isNotEmpty()) {
                val lastEpisode = episodes.maxByOrNull { it.createdAt }!!
                1.0 / (ChronoUnit.DAYS.between(lastEpisode.createdAt.toLocalDate(), LocalDate.now()).toDouble())
            } else {
                1.0 / (days * 2).toDouble() // 새로운 에피소드가 없는 경우의 기본 시간 가중치
            }

            // 각 항목의 합산 값 계산
            val likesCount = likes.size
            val viewsCount = views.size
            val commentsCount = comments.size

            val likesRatingSum = 1 * likes.groupBy { it.user.id }.size //sum(평가수*점수) 좋아요를 누른 유니크 유저의 수
            val viewsRatingSum = 1 * views.groupBy { it.user.id }.size //sum(평가수*점수) 조회수의 유니크 유저의 수
            val commentsRatingSum = 1 * comments.groupBy { it.user.id }.size //sum(평가수*점수) 댓글을 단 유니크 유저의 수


            // Bayesian Average 계산
            val likesScore =
                calculateBayesianAverage(likesRatingSum, likesCount, globalAverageLikes, regularizationFactor)
            val viewsScore =
                calculateBayesianAverage(viewsRatingSum, viewsCount, globalAverageViews, regularizationFactor)
            val commentsScore =
                calculateBayesianAverage(commentsRatingSum, commentsCount, globalAverageComments, regularizationFactor)

            // 최종 스코어 계산 (예시: 각 점수의 가중합)
            val finalScore = (likesScore * 0.3 + viewsScore * 0.5 + commentsScore * 0.2) * timeDecay

            return@map NovelIdAndScore(novelId, finalScore)
        }
        return@readOnly novelIdAndScorePair.sortedByDescending { it.score }
    }


    private fun getScoringMetaByLocalDate(days: Int): ScoringMetaData = readOnly {
        val episodesAll = episodeReader.findEpisodesDaysBetweenByCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val likesAll = episodeLikeReader.findAllWithEpisodeByDaysBetweenCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val viewsAll = episodeViewReader.findAllByDaysBetweenCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val commentsAll = episodeCommentReader.findAllWithEpisodeByDaysBetweenCreatedAt(
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

    private data class ScoringMetaData(
        val episodes: List<Episode>,
        val likes: List<EpisodeLike>,
        val views: List<EpisodeView>,
        val comments: List<EpisodeComment>,
    ) {
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


data class NovelIdAndScore(
    val novelId: Long,
    val score: Double,
)

/** Bayesian Average 계산 함수
 * Bayesian Average는 평가 수가 적을 때, 전체 평균에 가까운 값을 가지도록 하는 방법이다.
 * 이를 통해 평가 수가 적은 경우에도 전체 평균에 가까운 값을 가지도록 한다.
 * (극단적인 예로, 평가 수가 1개이고 점수가 5점인 경우, 전체 평균이 3점이라면 3점으로 계산되게 된다.)
 * (같은 유저가 여러 번 평가를 한 경우, ratingSum으로 인해 걸러지는 효과도 있다.)
 * @param ratingSum: (특정 평가수*특정 점수)의 합
 * @param ratingCount: 평가 횟수(특정 평가수의 총 합)
 * @param globalAverage: 전체 평균 점수
 * @param regularizationFactor: 전체에서 평가가 이루어진 총 평가수
 */
fun calculateBayesianAverage(
    ratingSum: Int,
    ratingCount: Int,
    globalAverage: Double,
    regularizationFactor: Int,
): Double {
    return (ratingSum + regularizationFactor * globalAverage) / (ratingCount + regularizationFactor)
}


package com.reditus.novelcia.novel.application.usecase

import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Component

@Component
class NovelScoringUseCase(
    private val scoreMetaDataExtractor: ScoreMetaDataExtractor,
    private val scoreCalculator: ScoreCalculator,
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
        val (episodesAll, likesAll, viewsAll, commentsAll, totalNovelIdSet)
            = scoreMetaDataExtractor.getScoringMetaByLocalDate(days)


        val globalAverageLikes = likesAll.groupBy { it.user.id }.size.toDouble() // 전체의 좋아요의 평균 점수(유니크한 유저 수)
        val globalAverageViews = viewsAll.groupBy { it.userId }.size.toDouble() // 전체의 조회수의 평균 점수(유니크한 유저 수)
        val globalAverageComments = commentsAll.groupBy { it.user.id }.size.toDouble() // 전체의 댓글수의 평균 점수(유니크한 유저 수)

        val globalAverage = ScoreCalculator.GlobalAverage(
            likes = globalAverageLikes,
            views = globalAverageViews,
            comments = globalAverageComments,
        )

        val regularizationFactor = likesAll.size + viewsAll.size + commentsAll.size

        val novelIdAndScorePair = totalNovelIdSet.map { novelId ->
            val episodes = episodesAll.filter { episode -> episode.novelId == novelId }
            val likes = likesAll.filter { like -> like.episode.novelId == novelId }
            val views = viewsAll.filter { view -> view.novelId == novelId }
            val comments = commentsAll.filter { comment -> comment.episode.novelId == novelId }

            val finalScore = scoreCalculator.calculate(
                episodes = episodes,
                likes = likes,
                views = views,
                comments = comments,
                days = days,
                globalAverage = globalAverage,
                regularizationFactor = regularizationFactor,
            )

            return@map NovelIdAndScore(novelId, finalScore)
        }
        return@readOnly novelIdAndScorePair.sortedByDescending { it.score }
    }


}


data class NovelIdAndScore(
    val novelId: Long,
    val score: Double,
)

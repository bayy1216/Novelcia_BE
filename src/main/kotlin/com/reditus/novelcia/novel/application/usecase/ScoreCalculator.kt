package com.reditus.novelcia.novel.application.usecase

import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.EpisodeLike
import com.reditus.novelcia.episodecomment.domain.EpisodeComment
import com.reditus.novelcia.episodeview.EpisodeView
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Component
class ScoreCalculator {
    fun calculate(
        episodes: List<Episode>,
        likes: List<EpisodeLike>,
        views: List<EpisodeView>,
        comments: List<EpisodeComment>,
        days: Int,
        globalAverageData: GlobalAverageData,
    ): Double {
        val timeDecay = calcTimeDecay(episodes, days)

        val likesCount = likes.size
        val viewsCount = views.size
        val commentsCount = comments.size

        val likesRatingSum = likes.groupBy { it.user.id }.size
        val viewsRatingSum = views.groupBy { it.userId }.size
        val commentsRatingSum = comments.groupBy { it.user.id }.size
        // Bayesian Average 계산
        val likesScore =
            calculateBayesianAverage(likesRatingSum, likesCount, globalAverageData.likes, globalAverageData.regularizationFactor)
        val viewsScore =
            calculateBayesianAverage(viewsRatingSum, viewsCount, globalAverageData.views, globalAverageData.regularizationFactor)
        val commentsScore =
            calculateBayesianAverage(commentsRatingSum, commentsCount, globalAverageData.comments, globalAverageData.regularizationFactor)

        val finalScore = (likesScore * 0.3 + viewsScore * 0.5 + commentsScore * 0.2) * timeDecay
        return finalScore
    }

    private fun calcTimeDecay(episodes: List<Episode>, days: Int): Double {
        return if (episodes.isNotEmpty()) {
            val lastEpisode = episodes.maxByOrNull { it.createdAt }!!
            1.0 / (ChronoUnit.DAYS.between(lastEpisode.createdAt.toLocalDate(), LocalDate.now()).toDouble())
        } else {
            1.0 / (days * 2).toDouble() // 새로운 에피소드가 없는 경우의 기본 시간 가중치
        }
    }

    data class GlobalAverageData(
        val likes: Double,
        val views: Double,
        val comments: Double,
        val regularizationFactor: Int,
    )
}


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
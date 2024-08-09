package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.episode.port.EpisodeCommentReader
import com.reditus.novelcia.domain.episode.port.EpisodeLikeReader
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.domain.episode.port.EpisodeViewReader
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class NovelQueryService(
    private val novelReader: NovelReader,
    private val episodeReader: EpisodeReader,
    private val episodeLikeReader: EpisodeLikeReader,
    private val episodeViewReader: EpisodeViewReader,
    private val episodeCommentReader: EpisodeCommentReader,
) {

    fun getNovelModelsByCursor(cursorRequest: CursorRequest): List<NovelModel.Main> = readOnly {
        val novels = novelReader.getNovelsByCursorOrderByCreatedAt(cursorRequest)
        return@readOnly novels.map { NovelModel.Main.from(it)(this) }
    }

    fun getNovelModelsByRanking(days: Int, size: Int): List<NovelModel.Main> = readOnly{
        val episodesAll = episodeReader.getEpisodesDaysBetweenByCreatedAt(
            startDate = LocalDate.now().minusDays(days.toLong()),
            endDate = LocalDate.now()
        )
        val likesAll = episodeLikeReader.findAllByEpisodeIds(episodesAll.map { it.id })
        val viewsAll = episodeViewReader.getAllByEpisodeIds(episodesAll.map { it.id })
        val commentsAll = episodeCommentReader.getAllByEpisodeIds(episodesAll.map { it.id })

        val novelAndScore = episodesAll.map {

            val likes = likesAll.filter { like -> like.episode.id == it.id }.size
            val views = viewsAll.filter { view -> view.episode.id == it.id }.size
            val comments = commentsAll.filter { comment -> comment.episode.id == it.id }.size


            val score = calcScoreByEpisode(
                viewsCount = views,
                likesCount = likes,
                commentsCount = comments,
                daysSincePosted = LocalDate.now().compareTo(it.createdAt.toLocalDate()),
            )
            return@map it.novel to score
        }
        val scoresByGroupNovel = novelAndScore.groupBy({ it.first }, { it.second })

        val novels = novelReader.getNovelsByIdsIn(scoresByGroupNovel.keys.map { it.id })


        return@readOnly novels.map { NovelModel.Main.from(it)() }
    }
}


fun calcScoreByEpisode(
    viewsCount: Int,
    likesCount: Int,
    commentsCount: Int,
    daysSincePosted: Int,
    globalAvg: Double = 3.5,//TODO FIX
    a: Int = 1,
    b: Int = 2,
    c: Int = 3,
    decayFactor: Double = 0.85,
    m: Int = 50,
): Double {
    val decay = Math.pow(decayFactor, daysSincePosted.toDouble())
    val weightedScore = (a * viewsCount + b * likesCount + c * commentsCount) * decay

    val totalVotes = viewsCount + likesCount + commentsCount +1
    val individualAvg = (viewsCount + 2 * likesCount + 3 * commentsCount) / totalVotes
    val bayesianScore = (totalVotes / (totalVotes + m)) * individualAvg + (m / (totalVotes + m)) * globalAvg

    val combinedScore = weightedScore + bayesianScore
    return combinedScore
}


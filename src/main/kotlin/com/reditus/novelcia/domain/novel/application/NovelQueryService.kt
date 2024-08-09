package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.episode.port.EpisodeLikeReader
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.global.util.readOnly
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class NovelQueryService(
    private val novelReader: NovelReader,
    private val episodeReader: EpisodeReader,
    private val episodeLikeReader: EpisodeLikeReader,
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
        val novelAndScore = episodesAll.map {

            val likes = episodeLikeReader.countByEpisodeId(it.id).toInt()


            val score = calcScoreByEpisode(
                views = 0,
                likes = likes,
                comments = 0,
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
    views: Int,
    likes: Int,
    comments: Int,
    daysSincePosted: Int,
    globalAvg: Double = 3.5,//TODO FIX
    a: Int = 1,
    b: Int = 2,
    c: Int = 3,
    decayFactor: Double = 0.85,
    m: Int = 50,
): Double {
    val decay = Math.pow(decayFactor, daysSincePosted.toDouble())
    val weightedScore = (a * views + b * likes + c * comments) * decay

    val totalVotes = views + likes + comments +1
    val individualAvg = (views + 2 * likes + 3 * comments) / totalVotes
    val bayesianScore = (totalVotes / (totalVotes + m)) * individualAvg + (m / (totalVotes + m)) * globalAvg

    val combinedScore = weightedScore + bayesianScore
    return combinedScore
}


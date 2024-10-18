package com.reditus.novelcia.novelfavorite.infrastructure

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.QEpisode
import com.reditus.novelcia.global.util.TxScope
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.novel.domain.QNovelAndSpecies
import com.reditus.novelcia.novel.domain.QNovelAndTag
import com.reditus.novelcia.novelmeta.application.SpeciesModel
import com.reditus.novelcia.novelmeta.application.TagModel
import com.reditus.novelcia.novelfavorite.application.NovelFavoriteModel
import com.reditus.novelcia.novelfavorite.domain.NovelFavorite
import com.reditus.novelcia.novelfavorite.domain.QNovelFavorite
import com.reditus.novelcia.novelmeta.domain.*
import com.reditus.novelcia.user.application.UserModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NovelFavoriteQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
)  {

    /**
     * 1. count 쿼리로 novelFavorite 개수 조회
     * 2. novelFavorite 페이징 조회 쿼리 - novel, author fetch join
     * 3. novel in절로 최대 에피소드 번호 조회
     */
    fun getUserFavoriteNovelPage(
        userId: Long,
        offsetRequest: OffsetRequest,
    ): Page<NovelFavoriteModel.UserFavorite> = readOnly {
        val count = jpaQueryFactory
            .select(QNovelFavorite.novelFavorite.count())
            .from(QNovelFavorite.novelFavorite)
            .where(QNovelFavorite.novelFavorite.user.id.eq(userId))
            .fetchOne()

        val novelFavorites = jpaQueryFactory
            .select(QNovelFavorite.novelFavorite)
            .from(QNovelFavorite.novelFavorite)
            .innerJoin(QNovelFavorite.novelFavorite.novel).fetchJoin()
            .innerJoin(QNovelFavorite.novelFavorite.novel.author).fetchJoin()
            .where(QNovelFavorite.novelFavorite.user.id.eq(userId))
            .offset(offsetRequest.page.toLong() * offsetRequest.size.toLong())
            .limit(offsetRequest.size.toLong())
            .fetch()

        val novelIds = novelFavorites.map { it.novel.id }


        val maxEpisodeNumbers = jpaQueryFactory
            .select(
                Projections.constructor(
                    EpisodeMaxNumberQuery::class.java,
                    QEpisode.episode.novel.id,
                    QEpisode.episode.episodeNumber.max()
                )
            )
            .from(QEpisode.episode)
            .where(QEpisode.episode.novel.id.`in`(novelIds))
            .groupBy(QEpisode.episode.novel.id)
            .fetch()


        val novelModels = novelFavorites.map {
            it.toModel(maxEpisodeNumbers)()
        }

        return@readOnly PageImpl(novelModels, Pageable.ofSize(offsetRequest.size), count ?: 0L)
    }
}

fun NovelFavorite.toModel(
    maxEpisodeNumbers: List<EpisodeMaxNumberQuery>,
): TxScope.()-> NovelFavoriteModel.UserFavorite = {
    NovelFavoriteModel.UserFavorite(
        id = novel.id,
        author = UserModel.from(novel.author)(),
        title = novel.title,
        thumbnailImageUrl = novel.thumbnailImageUrl,
        viewCount = novel.viewCount,
        likeCount = novel.likeCount,
        favoriteCount = novel.favoriteCount,
        episodeCount = novel.episodeCount,
        species = novel.novelMeta.speciesList.map { SpeciesModel.from(it)() },
        tags = novel.novelMeta.tags.map { TagModel.from(it)() },
        userLastReadEpisodeNumber = lastViewedEpisodeNumber,
        maxEpisodeNumber = maxEpisodeNumbers
            .find { query -> query.novelId == novel.id }?.maxEpisodeNumber
            ?: Episode.INITIAL_EPISODE_NUMBER,
    )
}


data class EpisodeMaxNumberQuery(
    val novelId: Long,
    val maxEpisodeNumber: Int,
)
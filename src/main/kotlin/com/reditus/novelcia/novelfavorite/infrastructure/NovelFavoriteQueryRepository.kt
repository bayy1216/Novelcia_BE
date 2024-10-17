package com.reditus.novelcia.novelfavorite.infrastructure

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.domain.QEpisode
import com.reditus.novelcia.global.util.TxScope
import com.reditus.novelcia.global.util.readOnly
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
     * 3. novelAndTag, novelAndSpecies in절 조회 쿼리 - tag, species fetch join
     * 4. novel in절로 최대 에피소드 번호 조회
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

        val novelTags = jpaQueryFactory
            .select(QNovelAndTag.novelAndTag)
            .from(QNovelAndTag.novelAndTag)
            .join(QNovelFavorite.novelFavorite)
            .on(
                QNovelAndTag.novelAndTag.novel.id.eq(QNovelFavorite.novelFavorite.novel.id),
                QNovelFavorite.novelFavorite.novel.id.`in`(novelIds)
            )
            .innerJoin(QNovelAndTag.novelAndTag.tag, QTag.tag).fetchJoin()
            .fetch()

        val novelSpecies = jpaQueryFactory
            .select(QNovelAndSpecies.novelAndSpecies)
            .from(QNovelAndSpecies.novelAndSpecies)
            .join(QNovelFavorite.novelFavorite)
            .on(
                QNovelAndSpecies.novelAndSpecies.novel.id.eq(QNovelFavorite.novelFavorite.novel.id),
                QNovelFavorite.novelFavorite.novel.id.`in`(novelIds)
            )
            .innerJoin(QNovelAndSpecies.novelAndSpecies.species).fetchJoin()
            .fetch()

        val novelTagMap = novelTags
            .groupBy({ it.novel.id }, { it.tag })
        val novelSpeciesMap = novelSpecies
            .groupBy({ it.novel.id }, { it.species })


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
            it.toModel(
                novelSpeciesMap,
                novelTagMap,
                maxEpisodeNumbers,
            )()
        }

        return@readOnly PageImpl(novelModels, Pageable.ofSize(offsetRequest.size), count ?: 0L)
    }
}

fun NovelFavorite.toModel(
    novelSpeciesMap: Map<Long, List<Species>>,
    novelTagMap: Map<Long, List<Tag>>,
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
        species = novelSpeciesMap[novel.id]?.map { species -> SpeciesModel.from(species)() }
            ?: emptyList(),
        tags = novelTagMap[novel.id]?.map { tag -> TagModel.from(tag)() } ?: emptyList(),
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
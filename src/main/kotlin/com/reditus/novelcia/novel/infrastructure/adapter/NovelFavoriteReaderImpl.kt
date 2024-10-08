package com.reditus.novelcia.novel.infrastructure.adapter

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.common.domain.OffsetRequest
import com.reditus.novelcia.episode.domain.Episode

import com.reditus.novelcia.episode.domain.QEpisode
import com.reditus.novelcia.episode.domain.QEpisodeView
import com.reditus.novelcia.novel.domain.application.NovelModel
import com.reditus.novelcia.novel.domain.application.SpeciesModel
import com.reditus.novelcia.novel.domain.application.TagModel
import com.reditus.novelcia.novel.domain.port.NovelFavoriteReader
import com.reditus.novelcia.user.domain.UserModel
import com.reditus.novelcia.global.util.TxScope
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.novel.domain.*
import com.reditus.novelcia.novel.infrastructure.NovelFavoriteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NovelFavoriteReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory,
    private val novelFavoriteRepository: NovelFavoriteRepository,
) : NovelFavoriteReader {
    override fun findByUserIdAndNovelId(userId: Long, novelId: Long): NovelFavorite? {
        return novelFavoriteRepository.findByUserIdAndNovelId(userId, novelId)
    }

    /**
     * 1. count 쿼리로 novelFavorite 개수 조회
     * 2. novelFavorite 페이징 조회 쿼리 - novel, author fetch join
     * 3. novelAndTag, novelAndSpecies in절 조회 쿼리 - tag, species fetch join
     * 4. episodeView 테이블을 이용하여 마지막으로 본 에피소드 조회
     * 5. novel in절로 최대 에피소드 번호 조회
     */
    override fun getUserFavoriteNovelPage(
        userId: Long,
        offsetRequest: OffsetRequest,
    ): Page<NovelModel.UserFavorite> = readOnly {
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


        val lastTimeViewEpisode = jpaQueryFactory
            .select(
                Projections.constructor(
                    EpisodeLastViewQuery::class.java,
                    QEpisodeView.episodeView.novel.id,
                    QEpisodeView.episodeView.episode.id,
                    QEpisode.episode.episodeNumber,
                )
            )
            .from(QEpisodeView.episodeView)
            .join(QEpisode.episode).on(QEpisodeView.episodeView.episode.id.eq(QEpisode.episode.id))
            .where(
                QEpisodeView.episodeView.user.id.eq(userId),
                QEpisodeView.episodeView.novel.id.`in`(novelIds),
                QEpisodeView.episodeView.createdAt.eq(
                    JPAExpressions
                        .select(QEpisodeView.episodeView.createdAt.max())
                        .from(QEpisodeView.episodeView)
                        .where(
                            QEpisodeView.episodeView.user.id.eq(userId),
                            QEpisodeView.episodeView.novel.id.`in`(novelIds)
                        )
                        .groupBy(QEpisodeView.episodeView.novel.id)
                )
            )
            .fetch()

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
                lastTimeViewEpisode,
                maxEpisodeNumbers,
            )()
        }

        return@readOnly PageImpl(novelModels, Pageable.ofSize(offsetRequest.size), count ?: 0L)
    }
}

fun NovelFavorite.toModel(
    novelSpeciesMap: Map<Long, List<Species>>,
    novelTagMap: Map<Long, List<Tag>>,
    lastTimeViewEpisode: List<EpisodeLastViewQuery>,
    maxEpisodeNumbers: List<EpisodeMaxNumberQuery>,
): TxScope.()-> NovelModel.UserFavorite = {
    NovelModel.UserFavorite(
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
        userLastReadEpisodeNumber = lastTimeViewEpisode
            .find { query -> query.novelId == novel.id }?.lastReadEpisodeNumber
            ?: Episode.INITIAL_EPISODE_NUMBER,
        maxEpisodeNumber = maxEpisodeNumbers
            .find { query -> query.novelId == novel.id }?.maxEpisodeNumber
            ?: Episode.INITIAL_EPISODE_NUMBER,
    )
}

data class EpisodeLastViewQuery(
    val novelId: Long,
    val lastReadEpisodeId: Long,
    val lastReadEpisodeNumber: Int,
)

data class EpisodeMaxNumberQuery(
    val novelId: Long,
    val maxEpisodeNumber: Int,
)
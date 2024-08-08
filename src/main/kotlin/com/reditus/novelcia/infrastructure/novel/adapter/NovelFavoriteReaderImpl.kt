package com.reditus.novelcia.infrastructure.novel.adapter

import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.OffsetRequest
import com.reditus.novelcia.domain.episode.QEpisode
import com.reditus.novelcia.domain.episode.QEpisodeView
import com.reditus.novelcia.domain.novel.*
import com.reditus.novelcia.domain.novel.application.NovelModel
import com.reditus.novelcia.domain.novel.application.SpeciesModel
import com.reditus.novelcia.domain.novel.application.TagModel
import com.reditus.novelcia.domain.novel.port.NovelFavoriteReader
import com.reditus.novelcia.domain.user.UserModel
import com.reditus.novelcia.global.util.readOnly
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NovelFavoriteReaderImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : NovelFavoriteReader {
    /**
     * 1. count 쿼리로 novelFavorite 개수 조회
     * 2. novelFavorite 페이징 조회 쿼리 - novel, author fetch join
     * 3. novelAndTag, novelAndSpecies 페이징 조회 쿼리 - tag, species fetch join
     * 4. episodeView 테이블을 이용하여 마지막으로 본 에피소드 조회
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
            .from(QNovelAndTag.novelAndTag, QNovelFavorite.novelFavorite)
            .join(QNovelAndTag.novelAndTag.tag, QTag.tag).fetchJoin()
            .join(QNovelAndTag.novelAndTag.novel, QNovel.novel).fetchJoin()
            .where(QNovelFavorite.novelFavorite.novel.id.`in`(novelIds))
            .fetch()

        val novelSpecies = jpaQueryFactory
            .select(QNovelAndSpecies.novelAndSpecies)
            .from(QNovelAndSpecies.novelAndSpecies, QNovelFavorite.novelFavorite)
            .join(QNovelAndSpecies.novelAndSpecies.species).fetchJoin()
            .join(QNovelAndSpecies.novelAndSpecies.novel, QNovel.novel).fetchJoin()
            .where(QNovelFavorite.novelFavorite.novel.id.`in`(novelIds))
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

            NovelModel.UserFavorite(
                id = it.novel.id,
                author = UserModel.from(it.novel.author)(),
                title = it.novel.title,
                thumbnailImageUrl = it.novel.thumbnailImageUrl,
                viewCount = it.novel.viewCount,
                likeCount = it.novel.likeCount,
                favoriteCount = it.novel.favoriteCount,
                episodeCount = it.novel.episodeCount,
                species = novelSpeciesMap[it.novel.id]?.map { species -> SpeciesModel.from(species)() }
                    ?: emptyList(),
                tags = novelTagMap[it.novel.id]?.map { tag -> TagModel.from(tag)() } ?: emptyList(),
                userLastReadEpisodeNumber = lastTimeViewEpisode
                    .find { query -> query.novelId == it.novel.id }?.lastReadEpisodeNumber
                    ?: 1,
                maxEpisodeNumber = maxEpisodeNumbers
                    .find { query -> query.novelId == it.novel.id }!!.maxEpisodeNumber,
            )
        }

        return@readOnly PageImpl(novelModels, Pageable.ofSize(offsetRequest.size), count ?: 0L)
    }
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
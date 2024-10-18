package com.reditus.novelcia.episode.infrastructure

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.episode.domain.Episode
import com.reditus.novelcia.episode.application.model.EpisodeModel
import com.reditus.novelcia.episode.domain.QEpisode
import com.reditus.novelcia.episode.domain.QEpisodeComment
import com.reditus.novelcia.episode.domain.EpisodePagingSort
import com.reditus.novelcia.novel.domain.ReadAuthority
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class EpisodeQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory,
)  {
    fun getEpisodeModelsByOffsetPaging(
        userId: Long,
        novelId: Long,
        pageRequest: PageRequest,
        sort: EpisodePagingSort
    ): List<EpisodeModel.Meta> {
        val episodeProjections =
            jpaQueryFactory.select(
                Projections.constructor(
                    EpisodeProjection::class.java,
                    QEpisode.episode.id,
                    QEpisode.episode.title,
                    QEpisode.episode.episodeNumber,
                    QEpisode.episode.createdAt,
                    QEpisode.episode.readAuthority,
                    QEpisode.episode.viewsCount,
                    QEpisodeComment.episodeComment.count().`as`("commentsCount"),
                )
            )
                .from(QEpisode.episode)
                .leftJoin(QEpisodeComment.episodeComment)
                .on(QEpisodeComment.episodeComment.episode.id.eq(QEpisode.episode.id))
                .where(
                    QEpisode.episode.novel.id.eq(novelId),
                    QEpisode.episode.isDeleted.eq(false),
                )
                .groupBy(QEpisode.episode.id)
                .orderBy(
                    when (sort) {
                        EpisodePagingSort.EPISODE_NUMBER_DESC -> QEpisode.episode.episodeNumber.desc()
                        EpisodePagingSort.EPISODE_NUMBER_ASC -> QEpisode.episode.episodeNumber.asc()
                    }
                )
                .offset(pageRequest.offset)
                .limit(pageRequest.pageSize.toLong())
                .fetch()

        return episodeProjections.map(EpisodeProjection::toMetaModel)
    }


    fun getByIdWithNovel(episodeId: Long): Episode {
        val episode = jpaQueryFactory
            .select(QEpisode.episode)
            .from(QEpisode.episode)
            .innerJoin(QEpisode.episode.novel).fetchJoin()
            .where(
                QEpisode.episode.id.eq(episodeId),
                QEpisode.episode.isDeleted.eq(false),
            ).fetchOne() ?: throw NoSuchElementException("해당 에피소드가 존재하지 않습니다.")
        return episode
    }

    fun getByEpisodeNumberAndNovelIdWithNovel(novelId: Long, episodeNumber: Int): Episode {
        return jpaQueryFactory
            .select(QEpisode.episode)
            .from(QEpisode.episode)
            .innerJoin(QEpisode.episode.novel).fetchJoin()
            .where(
                QEpisode.episode.novel.id.eq(novelId),
                QEpisode.episode.episodeNumber.eq(episodeNumber),
                QEpisode.episode.isDeleted.eq(false),
            ).fetchOne() ?: throw NoSuchElementException("해당 에피소드가 존재하지 않습니다.")
    }

    fun findLastEpisodeNumberByNovelId(novelId: Long): Int? {
        val query = jpaQueryFactory.select(QEpisode.episode.episodeNumber.max())
            .from(QEpisode.episode)
            .where(
                QEpisode.episode.novel.id.eq(novelId),
                QEpisode.episode.isDeleted.eq(false),
            ).fetchOne()
        return query
    }


    fun findEpisodesDaysBetweenByCreatedAt(startDate: LocalDate, endDate: LocalDate): List<Episode> {
        return jpaQueryFactory.select(QEpisode.episode)
            .from(QEpisode.episode)
            .where(
                QEpisode.episode.createdAt.between(
                    startDate.atStartOfDay(),
                    endDate.atStartOfDay().plusDays(1)
                ),
                QEpisode.episode.isDeleted.eq(false),
            ).fetch()
    }
}
internal data class EpisodeProjection(
    val id: Long,
    val title: String,
    val episodeNumber: Int,
    val createdAt: LocalDateTime,
    val readAuthority: ReadAuthority,
    val viewsCount: Int,
    val commentsCount: Long,
) {
    fun toMetaModel() = EpisodeModel.Meta(
        id = id,
        title = title,
        episodeNumber = episodeNumber,
        commentsCount = commentsCount.toInt(),
        viewsCount = viewsCount,
        createdAt = createdAt,
        readAuthority = readAuthority,
    )
}
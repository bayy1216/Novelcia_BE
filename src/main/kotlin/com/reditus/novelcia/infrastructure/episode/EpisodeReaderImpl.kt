package com.reditus.novelcia.infrastructure.episode

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.EpisodeModel
import com.reditus.novelcia.domain.episode.QEpisode
import com.reditus.novelcia.domain.episode.QEpisodeComment
import com.reditus.novelcia.domain.episode.QEpisodeView
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.domain.novel.ReadAuthority
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class EpisodeReaderImpl(
    private val episodeRepository: EpisodeRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeReader {

    data class EpisodeProjection(
        val id: Long,
        val title: String,
        val episodeNumber: Int,
        val createdAt: LocalDateTime,
        val readAuthority: ReadAuthority,
        val viewsCount: Long,
        val commentsCount: Long,
    ) {
        fun toMetaModel(isRead: Boolean) = EpisodeModel.Meta(
            id = id,
            title = title,
            episodeNumber = episodeNumber,
            commentsCount = commentsCount.toInt(),
            viewsCount = viewsCount.toInt(),
            createdAt = createdAt,
            readAuthority = readAuthority,
            isRead = isRead,
        )
    }

    override fun getEpisodeModelsByOffsetPaging(
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
                    QEpisodeView.episodeView.count().`as`("viewsCount"),
                    QEpisodeComment.episodeComment.count().`as`("commentsCount"),
                )
            )
                .from(QEpisode.episode)
                .leftJoin(QEpisodeView.episodeView)
                .on(QEpisodeView.episodeView.episode.id.eq(QEpisode.episode.id))
                .leftJoin(QEpisodeComment.episodeComment)
                .on(QEpisodeComment.episodeComment.episode.id.eq(QEpisode.episode.id))
                .where(
                    QEpisode.episode.novel.id.eq(novelId),
                )
                .groupBy(QEpisode.episode.id)
                .orderBy(
                    when (sort) {
                        EpisodePagingSort.CREATED_AT_DESC -> QEpisode.episode.createdAt.desc()
                        EpisodePagingSort.CREATED_AT_ASC -> QEpisode.episode.createdAt.asc()
                    }
                )
                .offset(pageRequest.offset)
                .limit(pageRequest.pageSize.toLong())
                .fetch()
        val episodeIds = episodeProjections.map { it.id }
        val readEpisodeIds = jpaQueryFactory.select(QEpisodeView.episodeView.episode.id)
            .from(QEpisodeView.episodeView)
            .where(
                QEpisodeView.episodeView.episode.id.`in`(episodeIds),
                QEpisodeView.episodeView.user.id.eq(userId)
            )
            .fetch()
        return episodeProjections.map {
            it.toMetaModel(it.id in readEpisodeIds)
        }

    }

    override fun getById(episodeId: Long): Episode {
        return episodeRepository.findByIdOrThrow(episodeId)
    }

    override fun getByIdWithNovel(episodeId: Long): Episode {
        return episodeRepository.findByIdWithNovel(episodeId) ?: throw NoSuchElementException("Episode not found")
    }

    override fun getLastEpisodeNumberByNovelId(novelId: Long): Int? {
        val query = jpaQueryFactory.selectFrom(QEpisode.episode)
            .where(QEpisode.episode.novel.id.eq(novelId))
            .orderBy(QEpisode.episode.episodeNumber.desc())
            .fetchFirst()
        return query?.episodeNumber
    }
}
package com.reditus.novelcia.infrastructure.episode.adapter

import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.reditus.novelcia.domain.episode.Episode
import com.reditus.novelcia.domain.episode.application.EpisodeModel
import com.reditus.novelcia.domain.episode.QEpisode
import com.reditus.novelcia.domain.episode.QEpisodeComment
import com.reditus.novelcia.domain.episode.QEpisodeView
import com.reditus.novelcia.domain.episode.port.EpisodePagingSort
import com.reditus.novelcia.domain.episode.port.EpisodeReader
import com.reditus.novelcia.domain.novel.ReadAuthority
import com.reditus.novelcia.infrastructure.episode.EpisodeRepository
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class EpisodeReaderImpl(
    private val episodeRepository: EpisodeRepository,
    private val jpaQueryFactory: JPAQueryFactory,
) : EpisodeReader {


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
                    Expressions.booleanTemplate(
                        "exists (select 1 from EpisodeView ev where ev.episode.id = {0} and ev.user.id = {1})",
                        QEpisode.episode.id, userId
                    ).`as`("isRead")
                )
            )
                .from(QEpisode.episode)
                .leftJoin(QEpisodeView.episodeView)
                .on(QEpisodeView.episodeView.episode.id.eq(QEpisode.episode.id))
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

    override fun getById(episodeId: Long): Episode {
        return episodeRepository.findByIdOrThrow(episodeId)
    }

    override fun getByIdWithNovel(episodeId: Long): Episode {
        val episode =  episodeRepository.findByIdWithNovel(episodeId) ?: throw NoSuchElementException("Episode not found")
        if(episode.isDeleted){
            throw NoSuchElementException("Episode not found")
        }
        return episode
    }

    override fun getLastEpisodeNumberByNovelId(novelId: Long): Int? {
        val query = jpaQueryFactory.select(QEpisode.episode.episodeNumber.max())
            .from(QEpisode.episode)
            .where(
                QEpisode.episode.novel.id.eq(novelId),
                QEpisode.episode.isDeleted.eq(false),
            ).fetchOne()
        return query
    }
}
internal data class EpisodeProjection(
    val id: Long,
    val title: String,
    val episodeNumber: Int,
    val createdAt: LocalDateTime,
    val readAuthority: ReadAuthority,
    val viewsCount: Long,
    val commentsCount: Long,
    val isRead: Boolean,
) {
    fun toMetaModel() = EpisodeModel.Meta(
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
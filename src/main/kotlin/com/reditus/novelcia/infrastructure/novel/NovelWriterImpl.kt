package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.port.NovelWriter
import com.reditus.novelcia.domain.novel.Tag
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class NovelWriterImpl(
    private val novelRepository: NovelRepository,
    private val tagRepository: TagRepository,
) : NovelWriter {
    override fun save(novel: Novel): Novel {
        return novelRepository.save(novel)
    }

    override fun delete(novelId: Long) {
        val affected = novelRepository.softDelete(novelId)
        if (affected == 0) {
            throw NoSuchElementException("해당 소설이 존재하지 않습니다.")
        }
    }

    override fun saveTag(tag: Tag): Tag {
        return tagRepository.save(tag)
    }

    override fun saveTags(tags: List<Tag>): List<Tag> {
        return tagRepository.saveAll(tags)
    }
}
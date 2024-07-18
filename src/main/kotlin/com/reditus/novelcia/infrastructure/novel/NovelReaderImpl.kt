package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelReader
import com.reditus.novelcia.domain.novel.Tag
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.stereotype.Repository

@Repository
class NovelReaderImpl(
    private val novelRepository: NovelRepository,
    private val tagRepository: TagRepository
) : NovelReader {
    override fun getNovelById(id: Long): Novel {
        return novelRepository.findByIdOrThrow(id)
    }

    override fun getTagsByTagNamesIn(tagNames: List<String>): List<Tag> {
        return tagRepository.findAllByNameIn(tagNames)
    }

    override fun getAllTags(): List<Tag> {
        return tagRepository.findAll()
    }
}
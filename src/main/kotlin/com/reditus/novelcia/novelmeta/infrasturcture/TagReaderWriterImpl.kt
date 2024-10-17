package com.reditus.novelcia.novelmeta.infrasturcture

import com.reditus.novelcia.novelmeta.domain.Tag
import com.reditus.novelcia.novelmeta.application.TagReader
import com.reditus.novelcia.novelmeta.application.TagWriter
import org.springframework.stereotype.Repository

// 단순 조회, 저장 기능 수행이라, Reader, Writer Implement를 하나로 통합
@Repository
class TagReaderWriterImpl(
    private val tagRepository: TagRepository,
) : TagReader, TagWriter {
    override fun findTagsByTagNamesIn(tagNames: List<String>): List<Tag> {
        return tagRepository.findAllByNameIn(tagNames)
    }

    override fun findAllTags(): List<Tag> {
        return tagRepository.findAll()
    }

    override fun saveTag(tag: Tag): Tag {
        return tagRepository.save(tag)
    }

    override fun saveTags(tags: List<Tag>): List<Tag> {
        return tagRepository.saveAll(tags)
    }
}
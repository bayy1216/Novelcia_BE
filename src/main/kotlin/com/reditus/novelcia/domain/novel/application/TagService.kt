package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.UpsertResult
import com.reditus.novelcia.domain.novel.Tag
import com.reditus.novelcia.domain.novel.TagCommand
import com.reditus.novelcia.domain.novel.port.TagReader
import com.reditus.novelcia.domain.novel.port.TagWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TagService(
    private val tagReader: TagReader,
    private val tagWriter: TagWriter
){
    @Transactional
    fun upsertTags(commands: List<TagCommand.Upsert>): UpsertResult<String> {
        val existTags = tagReader.getTagsByTagNamesIn(commands.map { it.name })
        val newTags = mutableListOf<Tag>()
        commands.forEach { upsertCommand ->
            val exitsTag = existTags.find { tag -> tag.name == upsertCommand.name }
            if(exitsTag != null) {
                exitsTag.update(upsertCommand)
            } else {
                newTags.add(Tag.create(upsertCommand))
            }
        }
        tagWriter.saveTags(newTags)
        return UpsertResult(
            insertedCount = newTags.size,
            insertedIds = newTags.map(Tag::name),
            updatedCount = existTags.size
        )
    }

    @Transactional(readOnly = true)
    fun getAllTags(): List<TagModel> {
        val tags = tagReader.getAllTags()
        return tags.map(TagModel::from)
    }
}
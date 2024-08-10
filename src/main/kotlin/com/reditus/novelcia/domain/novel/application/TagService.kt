package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.UpsertResult
import com.reditus.novelcia.domain.novel.Tag
import com.reditus.novelcia.domain.novel.TagCommand
import com.reditus.novelcia.domain.novel.port.TagReader
import com.reditus.novelcia.domain.novel.port.TagWriter
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Service


@Service
class TagService(
    private val tagReader: TagReader,
    private val tagWriter: TagWriter,
) {
    fun upsertTags(commands: List<TagCommand.Upsert>): UpsertResult<String> = transactional {
        val existTags = tagReader.findTagsByTagNamesIn(commands.map { it.name })
        val newTags = mutableListOf<Tag>()
        commands.forEach { upsertCommand ->
            val exitsTag = existTags.find { tag -> tag.name == upsertCommand.name }
            if (exitsTag != null) {
                exitsTag.update(upsertCommand)
            } else {
                newTags.add(Tag.create(upsertCommand))
            }
        }
        tagWriter.saveTags(newTags)
        return@transactional UpsertResult(
            insertedCount = newTags.size,
            insertedIds = newTags.map(Tag::name),
            updatedCount = existTags.size
        )
    }

    fun getAllTags(): List<TagModel> = readOnly {
        val tags = tagReader.findAllTags()
        return@readOnly tags.map { TagModel.from(it)(this) }
    }
}
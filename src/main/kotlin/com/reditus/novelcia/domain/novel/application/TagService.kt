package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.UpsertResult
import com.reditus.novelcia.domain.novel.Tag
import com.reditus.novelcia.domain.novel.TagCommand
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.novel.port.NovelWriter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TagService(
    private val novelReader: NovelReader,
    private val novelWriter: NovelWriter,
){
    @Transactional
    fun upsertTags(commands: List<TagCommand.Upsert>): UpsertResult<String> {
        val existTags = novelReader.getTagsByTagNamesIn(commands.map { it.name })
        val newTags = mutableListOf<Tag>()
        commands.forEach { upsertCommand ->
            val exitsTag = existTags.find { tag -> tag.name == upsertCommand.name }
            if(exitsTag != null) {
                exitsTag.update(upsertCommand)
            } else {
                newTags.add(Tag.create(upsertCommand))
            }
        }
        novelWriter.saveTags(newTags)
        return UpsertResult(
            insertedCount = newTags.size,
            insertedIds = newTags.map { it.name },
            updatedCount = existTags.size
        )
    }

    @Transactional(readOnly = true)
    fun getAllTags(): List<TagModel> {
        return novelReader.getAllTags().map { TagModel.from(it) }
    }
}
package com.reditus.novelcia.domain.novel

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TagService(
    private val novelReader: NovelReader,
    private val novelWriter: NovelWriter,
){
    @Transactional
    fun upsertTags(commands: List<TagCommand.Upsert>) {
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
    }

    @Transactional(readOnly = true)
    fun getAllTags(): List<TagModel> {
        return novelReader.getAllTags().map { TagModel.from(it) }
    }
}
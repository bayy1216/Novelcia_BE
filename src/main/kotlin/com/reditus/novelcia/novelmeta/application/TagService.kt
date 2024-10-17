package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.common.domain.UpsertResult
import com.reditus.novelcia.novelmeta.domain.Tag
import com.reditus.novelcia.novelmeta.domain.TagCommand
import com.reditus.novelcia.global.util.readOnly
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novelmeta.infrasturcture.TagRepository
import org.springframework.stereotype.Service


@Service
class TagService(
    private val tagRepository: TagRepository,
) {
    fun upsertTags(commands: List<TagCommand.Upsert>): UpsertResult<String> = transactional {
        val existTags = tagRepository.findAllByNameIn(commands.map { it.name })
        val newTags = mutableListOf<Tag>()
        commands.forEach { upsertCommand ->
            val exitsTag = existTags.find { tag -> tag.name == upsertCommand.name }
            if (exitsTag != null) {
                exitsTag.update(upsertCommand)
            } else {
                newTags.add(Tag.create(upsertCommand))
            }
        }
        tagRepository.saveAll(newTags)
        return@transactional UpsertResult(
            insertedCount = newTags.size,
            insertedIds = newTags.map(Tag::name),
            updatedCount = existTags.size
        )
    }

    fun getAllTags(): List<TagModel> = readOnly {
        val tags = tagRepository.findAll()
        return@readOnly tags.map { TagModel.from(it)(this) }
    }
}
package com.reditus.novelcia.domain.novel

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class TagService(
    private val novelWriter: NovelWriter,
){
    @Transactional
    fun saveTags(commands: List<TagCommand.Create>) {
        val tags = commands.map { Tag.create(it) }
        novelWriter.saveTags(tags)
    }
}
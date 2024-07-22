package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.Tag

interface NovelWriter {
    fun save(novel: Novel): Novel
    
    fun delete(novelId: Long)

    fun saveTag(tag: Tag): Tag
    fun saveTags(tags: List<Tag>): List<Tag>
}
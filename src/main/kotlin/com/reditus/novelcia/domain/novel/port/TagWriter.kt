package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.novel.Tag

interface TagWriter {

    fun saveTag(tag: Tag): Tag
    fun saveTags(tags: List<Tag>): List<Tag>
}
package com.reditus.novelcia.novel.domain.port

import com.reditus.novelcia.novel.domain.Tag

interface TagWriter {

    fun saveTag(tag: Tag): Tag
    fun saveTags(tags: List<Tag>): List<Tag>
}
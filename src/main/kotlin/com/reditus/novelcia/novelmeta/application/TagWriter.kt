package com.reditus.novelcia.novelmeta.application

import com.reditus.novelcia.novelmeta.domain.Tag

interface TagWriter {

    fun saveTag(tag: Tag): Tag
    fun saveTags(tags: List<Tag>): List<Tag>
}
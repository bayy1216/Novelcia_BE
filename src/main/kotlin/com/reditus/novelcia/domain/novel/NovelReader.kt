package com.reditus.novelcia.domain.novel

interface NovelReader {
    fun getNovelById(id: Long): Novel
    fun getTagsByTagNamesIn(tagNames: List<String>): List<Tag>
}
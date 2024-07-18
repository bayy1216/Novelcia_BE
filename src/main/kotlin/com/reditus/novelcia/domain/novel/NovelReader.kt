package com.reditus.novelcia.domain.novel

interface NovelReader {
    fun getNovelById(id: Long): Novel
}
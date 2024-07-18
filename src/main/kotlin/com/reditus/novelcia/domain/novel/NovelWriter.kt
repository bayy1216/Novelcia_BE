package com.reditus.novelcia.domain.novel

interface NovelWriter {
    fun save(novel: Novel): Novel
    
    fun delete(novelId: Long)
}
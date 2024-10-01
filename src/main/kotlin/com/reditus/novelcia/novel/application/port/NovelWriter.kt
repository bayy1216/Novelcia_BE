package com.reditus.novelcia.novel.application.port

import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.novel.domain.Novel

interface NovelWriter {
    fun save(novel: Novel): Novel
    
    fun delete(novelId: Long)
    fun addViewCount(novelId: Long, count: PositiveInt)

}
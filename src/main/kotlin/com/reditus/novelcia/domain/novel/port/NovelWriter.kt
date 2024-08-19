package com.reditus.novelcia.domain.novel.port

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.novel.Novel

interface NovelWriter {
    fun save(novel: Novel): Novel
    
    fun delete(novelId: Long)
    fun addViewCount(novelId: Long, count: PositiveInt)

}
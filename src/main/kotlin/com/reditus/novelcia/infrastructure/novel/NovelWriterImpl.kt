package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelWriter
import org.springframework.stereotype.Repository

@Repository
class NovelWriterImpl(
    private val novelRepository: NovelRepository
) : NovelWriter {
    override fun save(novel: Novel): Novel {
        return novelRepository.save(novel)
    }

    override fun delete(novelId: Long) {
        val affected = novelRepository.softDelete(novelId)
        if (affected == 0) {
            throw NoSuchElementException("해당 소설이 존재하지 않습니다.")
        }
    }
}
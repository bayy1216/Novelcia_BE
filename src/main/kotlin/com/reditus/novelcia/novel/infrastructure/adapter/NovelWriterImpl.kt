package com.reditus.novelcia.novel.infrastructure.adapter

import com.reditus.novelcia.common.domain.PositiveInt
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.application.port.NovelWriter
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class NovelWriterImpl(
    private val novelRepository: NovelRepository,
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

    override fun addViewCount(novelId: Long, count: PositiveInt) {
        val affected = novelRepository.addViewCount(novelId, count.value)
        if (affected == 0) {
            throw NoSuchElementException("해당 소설이 존재하지 않습니다.")
        }
    }
}
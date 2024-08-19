package com.reditus.novelcia.infrastructure.novel.adapter

import com.reditus.novelcia.domain.common.PositiveInt
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.port.NovelWriter
import com.reditus.novelcia.infrastructure.novel.NovelRepository
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
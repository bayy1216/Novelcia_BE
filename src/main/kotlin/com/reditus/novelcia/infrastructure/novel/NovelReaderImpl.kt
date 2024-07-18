package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelReader
import com.reditus.novelcia.infrastructure.findByIdOrThrow
import org.springframework.stereotype.Repository

@Repository
class NovelReaderImpl(
    private val novelRepository: NovelRepository
) : NovelReader {
    override fun getNovelById(id: Long): Novel {
        return novelRepository.findByIdOrThrow(id)
    }
}
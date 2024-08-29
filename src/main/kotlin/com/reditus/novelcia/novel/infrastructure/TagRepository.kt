package com.reditus.novelcia.novel.infrastructure

import com.reditus.novelcia.novel.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository :JpaRepository<Tag, Long> {
    fun findAllByNameIn(names: List<String>): List<Tag>
}
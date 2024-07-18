package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.novel.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository :JpaRepository<Tag, Long> {
    fun findAllByNameIn(names: List<String>): List<Tag>
}
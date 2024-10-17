package com.reditus.novelcia.novelmeta.infrasturcture

import com.reditus.novelcia.novelmeta.domain.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository :JpaRepository<Tag, Long> {
    fun findAllByNameIn(names: List<String>): List<Tag>
}
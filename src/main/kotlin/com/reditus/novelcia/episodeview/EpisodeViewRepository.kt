package com.reditus.novelcia.episodeview

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.time.LocalDate

interface EpisodeViewRepository: MongoRepository<EpisodeView, ObjectId> {

    @Query("{'createdAt': {'\$gte': ?0, '\$lte': ?1}}")
    fun findAllByDaysBetweenCreatedAt(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<EpisodeView>
}
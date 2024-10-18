package com.reditus.novelcia.episodeview

import jakarta.persistence.Id
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "episode_view")
class EpisodeView(
    @Id
    val id: ObjectId= ObjectId.get(),
    val episodeId: Long,
    val novelId: Long,
    val userId: Long,
) {
}
package com.reditus.novelcia.domain.episode.port

import com.reditus.novelcia.domain.episode.EpisodeReadEvent


interface EpisodeReadEventProducer{
    fun publish(event: EpisodeReadEvent)
}


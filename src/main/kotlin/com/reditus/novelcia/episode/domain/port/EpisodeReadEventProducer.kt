package com.reditus.novelcia.episode.domain.port

import com.reditus.novelcia.episode.domain.EpisodeReadEvent


interface EpisodeReadEventProducer{
    fun publish(event: EpisodeReadEvent)
}


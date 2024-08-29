package com.reditus.novelcia.common.domain

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling


@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@Configuration
class AppConfig(
    private val em: EntityManager,
) {

    @Bean
    fun querydsl(): JPAQueryFactory = JPAQueryFactory(em)
}
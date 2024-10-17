package com.reditus.novelcia.novel.application

import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.NovelAndSpecies
import com.reditus.novelcia.novel.domain.NovelAndTag
import com.reditus.novelcia.novel.infrastructure.NovelAndSpeciesRepository
import com.reditus.novelcia.novel.infrastructure.NovelAndTagRepository
import com.reditus.novelcia.novelmeta.domain.Species
import com.reditus.novelcia.novelmeta.domain.Tag
import org.springframework.stereotype.Component


@Component
class NovelMetaRelationManager(
    private val novelAndTagRepository: NovelAndTagRepository,
    private val novelAndSpeciesRepository: NovelAndSpeciesRepository,
) {

    fun addNovelRelations(
        novel: Novel,
        tags: List<Tag>,
        speciesList: List<Species>,
    ): Unit = transactional {
        val novelAndTags = tags.map { NovelAndTag.create(novel, it) }
        val novelAndSpecies = speciesList.map { NovelAndSpecies.create(novel, it) }

        novelAndTagRepository.saveAll(novelAndTags)
        novelAndSpeciesRepository.saveAll(novelAndSpecies)
    }

    fun putNovelRelations(
        novelId: Long,
        novel: Novel,
        tags: List<Tag>,
        speciesList: List<Species>,
    ): Unit = transactional {

        val existingNovelAndTagsMap = novelAndTagRepository.findAllByNovelId(novelId).associateBy { it.tag.name }
        val existingNovelAndSpeciesMap = novelAndSpeciesRepository.findAllByNovelId(novelId).associateBy { it.species.name }


        val tagNames = tags.map { it.name }.toSet()
        val newNovelAndTags = tags.filter { !existingNovelAndTagsMap.containsKey(it.name) }
            .map { NovelAndTag.create(novel, it) }
        val removeNovelAndTags = existingNovelAndTagsMap.filterKeys { it !in tagNames }.values

        // 종 처리
        val speciesNames = speciesList.map { it.name }.toSet()
        val newNovelAndSpecies = speciesList.filter { !existingNovelAndSpeciesMap.containsKey(it.name) }
            .map { NovelAndSpecies.create(novel, it) }
        val removeNovelAndSpecies = existingNovelAndSpeciesMap.filterKeys { it !in speciesNames }.values


        // 태그 처리
        // novelAndTag에 없던 tag들을 추가
        novelAndTagRepository.saveAll(newNovelAndTags)

        // novelAndTag에 있던 tag들 중에 tags에 없는 것들을 삭제
        novelAndTagRepository.deleteAll(removeNovelAndTags)

        // 종 처리
        // novelAndSpecies에 없던 종들을 추가
        novelAndSpeciesRepository.saveAll(newNovelAndSpecies)

        // novelAndSpecies에 있던 종들 중에 speciesList에 없는 것들을 삭제
        novelAndSpeciesRepository.deleteAll(removeNovelAndSpecies)
    }
}
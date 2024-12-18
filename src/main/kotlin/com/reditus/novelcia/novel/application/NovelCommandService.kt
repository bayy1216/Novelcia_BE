package com.reditus.novelcia.novel.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.application.usecase.NovelDeleteUseCase
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.NovelCommand
import com.reditus.novelcia.novel.domain.authAsAuthor
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.novelmeta.domain.Species
import com.reditus.novelcia.novelmeta.domain.Tag
import com.reditus.novelcia.novelmeta.infrasturcture.SpeciesRepository
import com.reditus.novelcia.novelmeta.infrasturcture.TagRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.stereotype.Service

@Service
class NovelCommandService(
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val speciesRepository: SpeciesRepository,
    private val novelRepository: NovelRepository,
    private val novelDeleteUseCase: NovelDeleteUseCase,
    private val novelMetaRelationManager: NovelMetaRelationManager,
) {

    fun registerNovel(loginUserId: LoginUserId, command: NovelCommand.Create): Long = transactional {
        val author = userRepository.getReferenceById(loginUserId.value)

        val tags = tagRepository.findAllByNameIn(command.tagNames)
            .checkTagSizeConsistency(command.tagNames)
        val speciesList = speciesRepository.findByNameIn(command.speciesNames)
            .checkSpeciesSizeConsistency(command.speciesNames)

        val novel = Novel.create(author, command, tags, speciesList)
        novelRepository.save(novel)

        novelMetaRelationManager.addNovelRelations(novel, tags, speciesList)
        return@transactional novel.id
    }


    fun updateNovel(
        loginUserId: LoginUserId,
        novelId: Long,
        command: NovelCommand.Update,
    ) = transactional {
        val novel = novelRepository.findByIdOrThrow(novelId)

        novel.authAsAuthor(loginUserId.value) {
            val tags = tagRepository.findAllByNameIn(command.tagNames)
                .checkTagSizeConsistency(command.tagNames)

            val speciesList = speciesRepository.findByNameIn(command.speciesNames)
                .checkSpeciesSizeConsistency(command.speciesNames)

            update(command, tags, speciesList)

            novelMetaRelationManager.putNovelRelations(novelId, novel, tags, speciesList)
        }

    }

    fun deleteNovel(loginUserId: LoginUserId, novelId: Long) = transactional {
        val novel = novelRepository.findByIdOrThrow(novelId)

        novel.authAsAuthor(loginUserId.value) {
            novelDeleteUseCase(novel)
        }

    }
}


private fun List<Tag>.checkTagSizeConsistency(tagNames: List<String>): List<Tag> {
    if (this.size != tagNames.size) {
        throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
    }
    return this
}

private fun List<Species>.checkSpeciesSizeConsistency(speciesNames: List<String>): List<Species> {
    if (this.size != speciesNames.size) {
        throw IllegalArgumentException("분류 이름이 잘못되었습니다.")
    }
    return this
}


package com.reditus.novelcia.novel.application

import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.NovelCommand
import com.reditus.novelcia.novel.application.usecase.NovelDeleteUseCase
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.util.transactional
import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.novelmeta.infrasturcture.SpeciesRepository
import com.reditus.novelcia.novelmeta.infrasturcture.TagRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
import org.springframework.stereotype.Service

@Service
class NovelService(
    private val userRepository: UserRepository,
    private val tagRepository: TagRepository,
    private val speciesRepository: SpeciesRepository,
    private val novelRepository: NovelRepository,
    private val novelDeleteUseCase: NovelDeleteUseCase,
) {

    fun registerNovel(loginUserId: LoginUserId, command: NovelCommand.Create): Long = transactional {
        val author = userRepository.getReferenceById(loginUserId.value)
        val tags = tagRepository.findAllByNameIn(command.tagNames).apply {
            if (this.size != command.tagNames.size) {
                throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
            }
        }
        val speciesList = speciesRepository.findByNameIn(command.speciesNames).apply {
            if (this.size != command.speciesNames.size) {
                throw IllegalArgumentException("분류 이름이 잘못되었습니다.")
            }
        }
        val novel = Novel.create(author, command, tags, speciesList)
        novelRepository.save(novel)
        return@transactional novel.id
    }


    fun updateNovel(
        loginUserId: LoginUserId,
        novelId: Long,
        command: NovelCommand.Update,
    ) = transactional {
        val novel = novelRepository.findByIdOrThrow(novelId)
        if (!novel.isAuthor(loginUserId.value)) {
            throw NoPermissionException("해당 소설을 수정할 권한이 없습니다.")
        }
        val tags = tagRepository.findAllByNameIn(command.tagNames)
            .toSet()
            .apply {  // tagNames에 중복이 있거나, 존재하지 않는 태그 이름이 있을 경우
                if (this.size != command.tagNames.size) {
                    throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
                }
            }
        val speciesList = speciesRepository.findByNameIn(command.speciesNames)
            .toSet()
            .apply {  // speciesNames에 중복이 있거나, 존재하지 않는 종 이름이 있을 경우
                if (this.size != command.speciesNames.size) {
                    throw IllegalArgumentException("분류 이름이 잘못되었습니다.")
                }
            }
        novel.update(command, tags, speciesList)
    }

    fun deleteNovel(loginUserId: LoginUserId, novelId: Long) = transactional {
        val novel = novelRepository.findByIdOrThrow(novelId)
        if (!novel.isAuthor(loginUserId.value)) {
            throw NoPermissionException("해당 소설을 삭제할 권한이 없습니다.")
        }
        novelDeleteUseCase(novel)
    }
}

package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.common.LoginUserId
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelCommand
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.novel.port.NovelWriter
import com.reditus.novelcia.domain.novel.port.SpeciesReader
import com.reditus.novelcia.domain.novel.port.TagReader
import com.reditus.novelcia.domain.novel.usecase.NovelDeleteUseCase
import com.reditus.novelcia.domain.user.port.UserReader
import com.reditus.novelcia.global.exception.NoPermissionException
import com.reditus.novelcia.global.util.transactional
import org.springframework.stereotype.Service

@Service
class NovelService(
    private val userReader: UserReader,
    private val tagReader: TagReader,
    private val speciesReader: SpeciesReader,
    private val novelReader: NovelReader,
    private val novelWriter: NovelWriter,
    private val novelDeleteUseCase: NovelDeleteUseCase,
) {

    fun registerNovel(loginUserId: LoginUserId, command: NovelCommand.Create): Long = transactional {
        val author = userReader.getReferenceById(loginUserId.value)
        val tags = tagReader.findTagsByTagNamesIn(command.tagNames).apply {
            if (this.size != command.tagNames.size) {
                throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
            }
        }
        val speciesList = speciesReader.findSpeciesByNamesIn(command.speciesNames).apply {
            if (this.size != command.speciesNames.size) {
                throw IllegalArgumentException("분류 이름이 잘못되었습니다.")
            }
        }
        val novel = Novel.create(author, command, tags, speciesList)
        novelWriter.save(novel)
        return@transactional novel.id
    }


    fun updateNovel(
        loginUserId: LoginUserId,
        novelId: Long,
        command: NovelCommand.Update,
    ) = transactional {
        val novel = novelReader.getNovelById(novelId)
        if (!novel.isAuthor(loginUserId.value)) {
            throw NoPermissionException("해당 소설을 수정할 권한이 없습니다.")
        }
        val tags = tagReader.findTagsByTagNamesIn(command.tagNames)
            .toSet()
            .apply {  // tagNames에 중복이 있거나, 존재하지 않는 태그 이름이 있을 경우
                if (this.size != command.tagNames.size) {
                    throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
                }
            }
        val speciesList = speciesReader.findSpeciesByNamesIn(command.speciesNames)
            .toSet()
            .apply {  // speciesNames에 중복이 있거나, 존재하지 않는 종 이름이 있을 경우
                if (this.size != command.speciesNames.size) {
                    throw IllegalArgumentException("분류 이름이 잘못되었습니다.")
                }
            }
        novel.update(command, tags, speciesList)
    }

    fun deleteNovel(loginUserId: LoginUserId, novelId: Long) = transactional {
        val novel = novelReader.getNovelById(novelId)
        if (!novel.isAuthor(loginUserId.value)) {
            throw NoPermissionException("해당 소설을 삭제할 권한이 없습니다.")
        }
        novelDeleteUseCase(novel)
    }
}

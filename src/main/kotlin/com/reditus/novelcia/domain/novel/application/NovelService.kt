package com.reditus.novelcia.domain.novel.application

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelCommand
import com.reditus.novelcia.domain.novel.port.NovelReader
import com.reditus.novelcia.domain.novel.port.NovelWriter
import com.reditus.novelcia.domain.novel.usecase.NovelDeleteUseCase
import com.reditus.novelcia.domain.user.port.UserReader
import com.reditus.novelcia.global.exception.NoPermissionException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NovelService(
    private val userReader: UserReader,
    private val novelReader: NovelReader,
    private val novelWriter: NovelWriter,
    private val novelDeleteUseCase: NovelDeleteUseCase,
) {
    @Transactional
    fun registerNovel(loginUserId: LoginUserId, command: NovelCommand.Create): Long {
        val author = userReader.getReferenceById(loginUserId.value)
        val tags = novelReader.getTagsByTagNamesIn(command.tagNames).let {
            if(it.size != command.tagNames.size) {
                throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
            }
            it
        }
        val novel = Novel.create(author, command, tags)
        novelWriter.save(novel)
        return novel.id
    }

    @Transactional
    fun updateNovel(
        loginUserId: LoginUserId,
        novelId: Long,
        command: NovelCommand.Update,
    ) {
        val novel = novelReader.getNovelById(novelId)
        if(novel.authorId != loginUserId.value) {
            throw NoPermissionException("해당 소설을 수정할 권한이 없습니다.")
        }
        val tags = novelReader.getTagsByTagNamesIn(command.tagNames)
            .toSet()
            .let {
            if(it.size != command.tagNames.size) {
                throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
            }
            it
        }
        novel.update(command, tags)
    }

    @Transactional
    fun deleteNovel(loginUserId: LoginUserId, novelId: Long) {
        val novel = novelReader.getNovelById(novelId)
        if(novel.authorId != loginUserId.value) {
            throw NoPermissionException("해당 소설을 삭제할 권한이 없습니다.")
        }
        novelDeleteUseCase(novel)
    }
}

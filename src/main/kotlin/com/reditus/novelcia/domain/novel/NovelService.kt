package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.user.UserReader
import com.reditus.novelcia.global.exception.NoPermissionException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NovelService(
    private val userReader: UserReader,
    private val novelReader: NovelReader,
    private val novelWriter: NovelWriter,
) {
    @Transactional
    fun registerNovel(loginUserId: LoginUserId, command: NovelCommand.Register): Novel {
        val author = userReader.getReferenceById(loginUserId.value)
        val novel = Novel.create(author, command)
        return novelWriter.save(novel)
    }

    @Transactional
    fun updateNovel(
        loginUserId: LoginUserId,
        novelId: Long,
        command: NovelCommand.Update,
    ): Novel {
        val novel = novelReader.getNovelById(novelId)
        if(novel.authorId != loginUserId.value) {
            throw NoPermissionException("해당 소설을 수정할 권한이 없습니다.")
        }
        novel.update(command)
        return novelWriter.save(novel)
    }

    @Transactional
    fun deleteNovel(loginUserId: LoginUserId, novelId: Long) {
        val novel = novelReader.getNovelById(novelId)
        if(novel.authorId != loginUserId.value) {
            throw NoPermissionException("해당 소설을 삭제할 권한이 없습니다.")
        }
        novelWriter.delete(novel.id)
    }
}
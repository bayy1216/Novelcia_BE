package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.episode.EpisodeWriter
import com.reditus.novelcia.domain.user.UserReader
import com.reditus.novelcia.global.exception.NoPermissionException
import org.springframework.stereotype.Component
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
    fun registerNovel(loginUserId: LoginUserId, command: NovelCommand.Create): Novel {
        val author = userReader.getReferenceById(loginUserId.value)
        val tags = novelReader.getTagsByTagNamesIn(command.tagNames).let {
            if(it.size != command.tagNames.size) {
                throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
            }
            it
        }
        val novel = Novel.create(author, command, tags)
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
        val tags = novelReader.getTagsByTagNamesIn(command.tagNames).let {
            if(it.size != command.tagNames.size) {
                throw IllegalArgumentException("태그 이름이 잘못되었습니다.")
            }
            it
        }
        novel.update(command, tags)
        return novelWriter.save(novel)
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

@Component
class NovelDeleteUseCase(
    private val novelWriter: NovelWriter,
    private val episodeWriter: EpisodeWriter,
) {
    /**
     * 소설과 소설의 모든 에피소드를 삭제합니다.
     * soft delete를 통해 삭제마크를 남깁니다.
     * - episode와 관련된 comment는 삭제하지 않습니다.
     */
    @Transactional
    operator fun invoke(novel: Novel) {
        novelWriter.delete(novel.id)
        episodeWriter.deleteAllByNovelId(novel.id)
    }
}
package com.reditus.novelcia.infrastructure.novel

import com.reditus.novelcia.domain.CursorRequest
import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelReader
import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.infrastructure.user.UserRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class NovelReaderImplTest @Autowired constructor(
    private val novelReaderImpl: NovelReaderImpl,
    private val novelRepository: NovelRepository,
    private val userRepository: UserRepository,
    private val em: EntityManager,
) {

    @Test
    fun `최근 생성된 게시글 cursor 페이징이 적상작동한다`() {
        val user = User.fixture()
        userRepository.save(user)
        var novel5Id: Long = 0
        for(i in 1..7) {
            val novel = Novel.fixture(author = user, title = "novel$i")
            novelRepository.save(novel)
            if (i == 5) {
                novel5Id = novel.id
            }
            em.flush()
        }
        em.clear()

        val cursorRequest = CursorRequest(
            cursorId = novel5Id,
            size = 10
        )
        val novels =
            novelReaderImpl.getNovelsByCursorOrderByCreatedAt(cursorRequest)
        assertEquals(4, novels.size) // 1, 2, 3, 4가 오래전이라 나와야함

        val novels2 = novelReaderImpl.getNovelsByCursorOrderByCreatedAt(
            CursorRequest(
                size = 10,
            )
        )
        assertEquals(7, novels2.size) // 처음 7개 다 나와야함
    }
}
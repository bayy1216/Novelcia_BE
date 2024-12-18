package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.NovelCommand
import com.reditus.novelcia.novelmeta.domain.Tag
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

@org.junit.jupiter.api.Tag("small")
class NovelTest {

    @Test
    fun `update시 tag들의 PUT 연산이 정상적으로 작동한다`() {
        // given (novel에는 tag1, tag2가 있고, command에는 tag1, tag3, tag4가 있다)
        val user = User.fixture()
        val tag1 = Tag.fixture("태그1")
        val tag2 = Tag.fixture("태그2")
        val tag3 = Tag.fixture("태그3")
        val tag4 = Tag.fixture("태그4")
        val novel = Novel.fixture(author = user)

        novel.addNovelAndTag(tag1)
        novel.addNovelAndTag(tag2)

        val command: NovelCommand.Update = NovelCommand.Update(
            title = "수정된 제목",
            description = "수정된 설명",
            thumbnailImageUrl = "수정된 이미지",
            tagNames = listOf(tag1.name, tag3.name, tag4.name),
            speciesNames = listOf()
        )
        val commandTags: Set<Tag> = setOf(tag1, tag3, tag4)

        // when
        novel.update(command, commandTags, setOf())

        // then
        assertAll(
            { assertEquals("수정된 제목", novel.title) },
            { assertEquals("수정된 설명", novel.description) },
            { assertEquals("수정된 이미지", novel.thumbnailImageUrl) },
            { assertEquals(3, novel.tags.size) },
            {
                assertEquals(
                    setOf(tag1.name, tag3.name, tag4.name),
                    novel.novelAndTags.map { it.tag.name }.toSet(),
                    {"PUT 연산에 의해서 tag2는 삭제되고, tag3, tag4가 추가되어," +
                            "결국 novel에는 tag1, tag3, tag4만 남아있어야 한다)"}
                )
            }
        )
    }

    @Test
    fun `isAuthor 메서드가 정상적으로 작동한다`() {
        // given
        val user = User.fixture()
        val novel = Novel.fixture(author = user)

        val notAuthorUser = User.fixture(id = 999L)

        // when
        val isAuthorOK = novel.isAuthor(user.id)
        val isAuthorFail = novel.isAuthor(notAuthorUser.id)

        // then
        assertTrue(isAuthorOK, {"작가와 novel의 작가가 같아야 함"})
        assertFalse(isAuthorFail, {"작가와 novel의 작가가 다르면 false여야 함"})
    }
}
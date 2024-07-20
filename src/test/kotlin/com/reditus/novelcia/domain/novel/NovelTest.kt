package com.reditus.novelcia.domain.novel

import com.reditus.novelcia.domain.user.User
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class NovelTest {

    @Test
    fun `update시 tag들의 PUT 연산이 정상적으로 작동한다`() {
        // given
        val user = User.fixture()
        val tag1 = Tag.fixture("태그1")
        val tag2 = Tag.fixture("태그2")
        val novel = Novel.fixture(author=user)

        novel.addNovelAndTag(tag1)
        novel.addNovelAndTag(tag2)

        // when

        val command: NovelCommand.Update = NovelCommand.Update(
            title = "수정된 제목",
            description = "수정된 설명",
            thumbnailImageUrl = "수정된 이미지",
            tagNames = listOf("태그1","태그3", "태그4")
        )
        val commandTags: Set<Tag> = setOf(tag1,Tag.fixture("태그3"), Tag.fixture("태그4"))
        novel.update(command, commandTags)

        // then
        assertEquals("수정된 제목", novel.title)
        assertEquals("수정된 설명", novel.description)
        assertEquals("수정된 이미지", novel.thumbnailImageUrl)
        assertEquals(3, novel.tags.size)
        assertEquals(novel.novelAndTags.map { it.tag.name }.toSet(), setOf("태그1","태그3", "태그4"))
    }
}
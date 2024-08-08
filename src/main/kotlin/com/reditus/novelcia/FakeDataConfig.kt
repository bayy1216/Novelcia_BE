package com.reditus.novelcia

 import com.reditus.novelcia.domain.LoginUserId
import com.reditus.novelcia.domain.auth.AuthService
import com.reditus.novelcia.domain.episode.EpisodeCommand
 import com.reditus.novelcia.domain.episode.application.EpisodeQueryService
 import com.reditus.novelcia.domain.episode.application.EpisodeService
 import com.reditus.novelcia.domain.episode.port.EpisodeReader
 import com.reditus.novelcia.domain.novel.Novel
import com.reditus.novelcia.domain.novel.NovelCommand
import com.reditus.novelcia.domain.novel.ReadAuthority
import com.reditus.novelcia.domain.novel.Tag
 import com.reditus.novelcia.domain.novel.application.NovelFavoriteService
 import com.reditus.novelcia.domain.novel.application.NovelService
import com.reditus.novelcia.domain.user.User
import com.reditus.novelcia.domain.user.UserCommand
 import com.reditus.novelcia.infrastructure.episode.EpisodeRepository
 import com.reditus.novelcia.infrastructure.findByIdOrThrow
 import com.reditus.novelcia.infrastructure.novel.NovelRepository
import com.reditus.novelcia.infrastructure.novel.TagRepository
import com.reditus.novelcia.infrastructure.user.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
 import org.springframework.context.annotation.Profile
 import org.springframework.core.env.Environment

@Configuration
class FakeDataConfig {
    @Profile("fake")
    @Bean
    fun `테스트용 더미데이터 생성`(
        env: Environment,
        tagRepository: TagRepository,
        userRepository: UserRepository,
        authService: AuthService,
        episodeService: EpisodeService,
        novelService: NovelService,
        novelRepository: NovelRepository,
        episodeQueryService: EpisodeQueryService,
        novelFavoriteService: NovelFavoriteService,
        episodeRepository: EpisodeRepository,
    ) : CommandLineRunner = CommandLineRunner {
        `태그 생성`(tagRepository)

        `회원 가입`(authService)
        val user = userRepository.findAll().first()

        `novel 생성`(novelService, user)

        val novel = novelRepository.findAll().first()
        val newId = createEpisode(episodeService, user, novel)


        novelFavoriteService.addFavoriteNovel(LoginUserId(user.id), novel.id)
        val episode = episodeRepository.findByIdOrThrow(newId)
        episodeQueryService.getEpisodeDetail(novel.id,episode.episodeNumber, LoginUserId(user.id))

    }

    private fun createEpisode(
        episodeService: EpisodeService,
        user: User,
        novel: Novel,
    ) :Long {
        val episodeCommand = EpisodeCommand.Create(
            title = "에피소드1",
            content = "에피소드1 내용",
            authorComment = "에피소드1 작가의 말",
            readAuthority = ReadAuthority.FREE
        )
        return episodeService.createEpisode(
            LoginUserId(user.id),
            novel.id,
            episodeCommand
        )
    }

    private fun `novel 생성`(
        novelService: NovelService,
        user: User?,
    ) {
        val novelCommand = NovelCommand.Create(
            title = "소설1",
            description = "소설1 설명",
            tagNames = listOf("태그1", "태그2"),
            thumbnailImageUrl = null,
        )
        novelService.registerNovel(LoginUserId(user!!.id), novelCommand)
    }

    private fun `회원 가입`(authService: AuthService) {
        val userCommand = UserCommand.Create(
            email = "test@a.c",
            password = "test",
            nickname = "test",
        )
        authService.emailSignup(userCommand)
    }

    private fun `태그 생성`(tagRepository: TagRepository) {
        val tags = listOf(
            Tag.fixture("태그1"),
            Tag.fixture("태그2"),
        )
        tagRepository.saveAll(tags)
    }


}

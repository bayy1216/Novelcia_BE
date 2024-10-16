package com.reditus.novelcia

 import com.reditus.novelcia.common.domain.LoginUserId
import com.reditus.novelcia.auth.domain.AuthService
import com.reditus.novelcia.episode.domain.EpisodeCommand
 import com.reditus.novelcia.episode.application.EpisodeQueryService
 import com.reditus.novelcia.episode.application.EpisodeCommandService
 import com.reditus.novelcia.novel.domain.Novel
import com.reditus.novelcia.novel.domain.NovelCommand
import com.reditus.novelcia.novel.domain.ReadAuthority
import com.reditus.novelcia.novelmeta.domain.Tag
 import com.reditus.novelcia.novelfavorite.application.NovelFavoriteService
 import com.reditus.novelcia.novel.application.NovelCommandService
import com.reditus.novelcia.user.domain.User
import com.reditus.novelcia.user.domain.UserCommand
 import com.reditus.novelcia.episode.infrastructure.EpisodeRepository
 import com.reditus.novelcia.common.infrastructure.findByIdOrThrow
 import com.reditus.novelcia.novel.infrastructure.NovelRepository
import com.reditus.novelcia.novelmeta.infrasturcture.TagRepository
import com.reditus.novelcia.user.infrastructure.UserRepository
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
        episodeCommandService: EpisodeCommandService,
        novelCommandService: NovelCommandService,
        novelRepository: NovelRepository,
        episodeQueryService: EpisodeQueryService,
        novelFavoriteService: NovelFavoriteService,
        episodeRepository: EpisodeRepository,
    ) : CommandLineRunner = CommandLineRunner {
        `태그 생성`(tagRepository)

        `회원 가입`(authService)
        val user = userRepository.findAll().first()

        `novel 생성`(novelCommandService, user)

        val novel = novelRepository.findAll().first()
        val newId = createEpisode(episodeCommandService, user, novel)


        novelFavoriteService.addFavoriteNovel(LoginUserId(user.id), novel.id)
        val episode = episodeRepository.findByIdOrThrow(newId)
        episodeQueryService.getEpisodeDetail(novel.id,episode.episodeNumber, LoginUserId(user.id))

    }

    private fun createEpisode(
        episodeCommandService: EpisodeCommandService,
        user: User,
        novel: Novel,
    ) :Long {
        val episodeCommand = EpisodeCommand.Create(
            title = "에피소드1",
            content = "에피소드1 내용",
            authorComment = "에피소드1 작가의 말",
            readAuthority = ReadAuthority.FREE
        )
        return episodeCommandService.createEpisode(
            LoginUserId(user.id),
            novel.id,
            episodeCommand
        )
    }

    private fun `novel 생성`(
        novelCommandService: NovelCommandService,
        user: User?,
    ) {
        val novelCommand = NovelCommand.Create(
            title = "소설1",
            description = "소설1 설명",
            tagNames = listOf("태그1", "태그2"),
            thumbnailImageUrl = null,
            speciesNames = listOf(),
        )
        novelCommandService.registerNovel(LoginUserId(user!!.id), novelCommand)
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

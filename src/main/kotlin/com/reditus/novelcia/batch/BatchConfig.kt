package com.reditus.novelcia.batch

import com.reditus.novelcia.episodeview.EpisodeView
import org.springframework.batch.core.*
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.MongoCursorItemReader
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@Configuration
class HelloWorldBatchConfig {
    @Bean
    fun job(
        jobRepository: JobRepository,
        platformTransactionManager: PlatformTransactionManager,
        tasklet: Tasklet,
        episodeViewReader: MongoCursorItemReader<EpisodeView>,
        itemProcessor: ItemProcessor<EpisodeView, EpisodeView>,
        itemWriter: ItemWriter<EpisodeView>,
    ): Job {
        return JobBuilder("job", jobRepository)
            .start(helloStep(jobRepository, platformTransactionManager, tasklet))
            .next(mongoStep(jobRepository, platformTransactionManager, episodeViewReader, itemProcessor, itemWriter))
            .build()
    }

    @Bean
    fun helloStep(
        jobRepository: JobRepository,
        platformTransactionManager: PlatformTransactionManager,
        tasklet: Tasklet,
    ): Step {
        return StepBuilder("helloStep", jobRepository)
            .tasklet(tasklet, platformTransactionManager)
            .build()
    }

    @Bean
    fun mongoStep(
        jobRepository: JobRepository,
        platformTransactionManager: PlatformTransactionManager,
        episodeViewReader: MongoCursorItemReader<EpisodeView>,
        itemProcessor: ItemProcessor<EpisodeView, EpisodeView>,
        itemWriter: ItemWriter<EpisodeView>,
    ): Step {
        return StepBuilder("mongoStep", jobRepository)
            .chunk<EpisodeView,EpisodeView>(10, platformTransactionManager)
            .reader(episodeViewReader)
            .processor(itemProcessor)
            .writer(itemWriter)
            .build()
    }


    @Bean
    fun episodeViewReader(mongoTemplate: MongoTemplate): MongoCursorItemReader<EpisodeView> {
        val reader = MongoCursorItemReader<EpisodeView>()
        reader.setTemplate(mongoTemplate)
        reader.setQuery(Query(Criteria())) // You can customize the query if needed
        reader.setTargetType(EpisodeView::class.java)
        reader.setBatchSize(100)
        reader.setCollection("episode_view")

        return reader
    }

    @Bean
    fun itemProcessor(): ItemProcessor<EpisodeView, EpisodeView> {
        return ItemProcessor { episodeView: EpisodeView ->
            println(episodeView)
            episodeView
        }
    }


    @Bean
    fun printItemWriter(): ItemWriter<EpisodeView> {
        return ItemWriter { items ->
            items.forEach { item ->
                println("Processing EpisodeView: $item")
            }
        }
    }


    @Bean
    fun tasklet(): Tasklet {
        return Tasklet { contribution: StepContribution, chunkContext: ChunkContext ->
            println("Hello, World!")
            RepeatStatus.FINISHED
        }
    }
}


@RestController
class HelloWorldController(
    private val job: Job,
    private val jobLauncher: JobLauncher,
) {
    @GetMapping("/api/job/hello-world/{id}")
    fun helloWorld(
        @PathVariable id: Long,
    ): String {
        val jobParameters: JobParameters = JobParametersBuilder()
            .addLong("id", id)
            .addLong("timestamp", System.currentTimeMillis()) // Optional: to make the job execution unique
            .toJobParameters()
        jobLauncher.run(job, jobParameters)

        return "start job: ${job.name}"
    }
}

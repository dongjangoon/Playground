package msa.content.job

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import msa.content.dto.SourceUrl
import msa.content.service.ContentCrawlerService
import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Configuration
class ContentCrawlerJobConfig(
    private val contentCrawlerService: ContentCrawlerService,
    private val blogSourceProvider: BlogSourceProvider,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val JOB_NAME = "contentCrawlerJob"
        const val STEP_NAME = "contentCrawlerStep"
        const val CONCURRENT_WORKERS = 10 // 동시에 처리할 크롤링 작업 수
        const val CHANNEL_CAPACITY = 20 // 크롤링 요청을 담을 채널 크기
    }

    @Bean
    fun contentCrawlerJob(
        jobRepository: JobRepository,
        contentCrawlerStep: Step,
    ): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .start(contentCrawlerStep)
            .build()
    }

    @Bean
    @OptIn(ExperimentalTime::class)
    fun contentCrawlerStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .tasklet({ _, _ ->
                val (processedCount, elapsedTime) = measureTimedValue { processSources() }
                log.info(
                    "컨텐츠 크롤링 작업이 완료되었습니다. 총 ${processedCount}개의 컨텐츠를 처리하였습니다. " +
                        "(소요시간: ${elapsedTime.inWholeSeconds}초)",
                )
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    private fun processSources(): Int =
        runBlocking {
            val processedCount = AtomicInteger()
            val channel = Channel<SourceUrl>(capacity = CHANNEL_CAPACITY)

            // 여러 워커를 동시에 실행하여 크롤링 처리
            val workers =
                List(CONCURRENT_WORKERS) {
                    launch {
                        for (sourceUrl in channel) {
                            tryToProcessUrl(sourceUrl)
                            processedCount.incrementAndGet()
                        }
                    }
                }

            // 크롤링할 소스 URL들을 채널로 전송
            launch {
                blogSourceProvider.getAllBlogResources().forEach { source ->
                    channel.send(source)
                }
                channel.close()
            }

            workers.joinAll()
            processedCount.get()
        }

    private suspend fun tryToProcessUrl(sourceUrl: SourceUrl) {
        try {
            val post = contentCrawlerService.crawlAndProcessContent(sourceUrl)
            log.info("새로운 컨텐츠 크롤링 완료: ${post.title} from ${sourceUrl.url}")
        } catch (e: Exception) {
            log.error("컨텐츠 크롤링 중 오류 발생: ${e.message}", e)
        }
    }
}

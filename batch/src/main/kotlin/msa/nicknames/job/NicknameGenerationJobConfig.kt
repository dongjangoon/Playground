package msa.nicknames.job

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import msa.nickname.service.NicknameService
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
class NicknameGenerationJobConfig(
    private val nicknameService: NicknameService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private companion object {
        const val JOB_NAME = "nicknameGenerationJob"
        const val STEP_NAME = "nicknameGenerationStep"
        const val CONCURRENT_WORKERS = 100
        const val CHANNEL_CAPACITY = 100
    }

    @Bean
    fun nicknameGenerationJob(
        jobRepository: JobRepository,
        nicknameGenerationStep: Step,
    ): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .start(nicknameGenerationStep)
            .build()
    }

    @Bean
    @OptIn(ExperimentalTime::class)
    fun nicknameGenerationStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
    ): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .tasklet({ _, _ ->
                val (generatedCount, elapsedTime) = measureTimedValue { generateNicknames() }
                log.info(
                    "닉네임 생성 작업이 완료되었습니다. 총 ${generatedCount}개의 닉네임을 생성했습니다. " +
                        "(소요시간: ${elapsedTime.inWholeSeconds}초)",
                )
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    private fun generateNicknames(): Int =
        runBlocking {
            val generatedCount = AtomicInteger()
            val channel = Channel<Unit>(capacity = CHANNEL_CAPACITY)

            // 여러 워커를 동시에 실행하여 닉네임 생성 처리
            val workers =
                List(CONCURRENT_WORKERS) {
                    launch {
                        for (unit in channel) {
                            tryToGenerateNickname()
                            generatedCount.incrementAndGet()
                        }
                    }
                }

            // 형용사와 명사 조합을 채널로 전송
            launch {
                repeat(1000) { // 생성할 닉네임 수
                    channel.send(Unit)
                }
                channel.close()
            }

            // 모든 워커가 완료될 때까지 대기
            workers.joinAll()
            generatedCount.get()
        }

    private suspend fun tryToGenerateNickname() {
        try {
            val nickname = nicknameService.generateNickname()
            log.info("새로운 닉네임 생성 완료: $nickname")
        } catch (e: Exception) {
            log.error("닉네임 생성 중 오류 발생: ${e.message}", e)
        }
    }
}

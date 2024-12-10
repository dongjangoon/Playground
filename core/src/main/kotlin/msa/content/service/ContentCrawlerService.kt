package msa.content.service

import msa.common.exception.RobotsNotAllowedException
import msa.common.exception.UrlAlreadyCrawledException
import msa.content.data.CrawledUrl
import msa.content.dto.SourceUrl
import msa.content.dto.SummarizedRequest
import msa.content.dto.SummarizedResponse
import msa.content.repository.CrawledUrlRepository
import msa.post.data.Post
import msa.post.service.PostService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

interface ContentCrawlerService {
    suspend fun crawlAndProcessContent(sourceUrl: SourceUrl): Post
}

@Service
class ContentCrawlerServiceImpl(
    private val webClient: WebClient,
    private val robotsValidator: RobotsValidator,
    private val postService: PostService,
    private val crawledUrlRepository: CrawledUrlRepository,
) : ContentCrawlerService {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val USER_AGENT = "TechBlogCrawlerBot"
    }

    override suspend fun crawlAndProcessContent(sourceUrl: SourceUrl): Post {
        // 이미 크롤링한 URL인지 확인
        if (crawledUrlRepository.existsByUrl(sourceUrl.url)) {
            log.info("이미 크롤링한 URL입니다. URL: ${sourceUrl.url}")
            throw UrlAlreadyCrawledException
        }

        val uri = URI(sourceUrl.url)
        val baseUrl = "${uri.scheme}://${uri.host}"
        val path = uri.path

        // robots.txt 검사
        if (!robotsValidator.isAllowedToCrawl(
                baseUrl = baseUrl,
                path = path,
                userAgent = USER_AGENT,
            )
        ) {
            throw RobotsNotAllowedException
        }

        // HTML 컨텐츠 크롤링
        val htmlContent =
            webClient.get()
                .uri(sourceUrl.url)
                .retrieve()
                .awaitBody<String>()

        // LLM 서비스를 통해 구조화된 데이터 받기
        val parsedContent =
            llmClient.post()
                .uri("/parse-and-summarize")
                .bodyValue(SummarizedRequest(htmlContent))
                .retrieve()
                .awaitBody<SummarizedResponse>()

        // Post 엔티티 생성 및 저장
        val post =
            postService.createPost(
                title = parsedContent.title,
                content = parsedContent.content,
                summary = parsedContent.summary,
                category = sourceUrl.category,
                authorId = "SYSTEM",
                tags = parsedContent.tags,
            )

        // 크롤링한 URL 저장
        crawledUrlRepository.save(
            CrawledUrl.create(
                url = sourceUrl.url,
                postId = post.id!!,
            ),
        )

        return post
    }
}

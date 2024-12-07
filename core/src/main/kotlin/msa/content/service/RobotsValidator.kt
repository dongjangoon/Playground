package msa.content.service

import msa.common.exception.RobotsNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI

@Service
class RobotsValidator(
    private val webClient: WebClient
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val ROBOTS_PATH = "/robots.txt"
    }

    /**
     * 주어진 URL에 대한 크롤링 허용 여부를 확인합니다.
     * @param baseUrl 확인할 웹사이트의 기본 URL (예: "https://example.com")
     * @param path 확인할 특정 경로 (예: "/blog/post-1")
     * @param userAgent 사용할 User-Agent 값
     * @return 크롤링 허용 여부
     */
    suspend fun isAllowedToCrawl(baseUrl: String, path: String, userAgent: String): Boolean {
        try {
            val robotsTxtUrl = URI(baseUrl).resolve(ROBOTS_PATH).toString()
            val robotsTxt = fetchRobotsTxt(robotsTxtUrl)

            return parseRobotsTxt(robotsTxt, path, userAgent)
        } catch (e: Exception) {
            log.warn("robots.txt 확인 중 오류 발생: ${e.message}. 기본적으로 크롤링 허용")
            return true // robots.txt를 읽을 수 없는 경우 기본적으로 허용
        }
    }

    private fun parseRobotsTxt(robotsTxt: String, path: String, userAgent: String): Boolean {
        var isUserAgentSection = false
        var foundUserAgent = false

        robotsTxt.lineSequence().forEach { line ->
            val trimmedLine = line.trim().lowercase()

            when {
                trimmedLine.startsWith("user-agent:") -> {
                    val agent = trimmedLine.substringAfter(":").trim()
                    isUserAgentSection = agent == "*" || agent == userAgent
                    if (isUserAgentSection) foundUserAgent = true
                }

                isUserAgentSection && trimmedLine.startsWith("disallow:") -> {
                    val disallowedPath = trimmedLine.substringAfter(":").trim()
                    if (path.startsWith(disallowedPath)) {
                        return false
                    }
                }

                isUserAgentSection && trimmedLine.startsWith("allow:") -> {
                    val allowedPath = trimmedLine.substringAfter(":").trim()
                    if (path.startsWith(allowedPath)) {
                        return true
                    }
                }

                // 빈 줄을 만나면 현재 User-Agent 섹션을 종료
                trimmedLine.isEmpty() && isUserAgentSection -> {
                    isUserAgentSection = false
                }
            }
        }

        // robots.txt에서 명시적으로 차단하지 않았다면 허용
        return true
    }

    private suspend fun fetchRobotsTxt(robotsUrl: String): String {
        return try {
            webClient.get()
                .uri(robotsUrl)
                .retrieve()
                .onStatus({ it.is4xxClientError || it.is5xxServerError }) { _ ->
                    throw RobotsNotFoundException
                }
                .awaitBody()
        } catch (e: WebClientResponseException) {
            throw RobotsNotFoundException
        }
    }
}

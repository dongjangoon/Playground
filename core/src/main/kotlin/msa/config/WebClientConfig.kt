package msa.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig {

    @Value("\${llm.service.url}")
    private lateinit var llmServiceUrl: String

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))
                .followRedirect(true)
            ))
            .defaultHeader("User-Agent", "TechBlogCrawlerBot")
            .build()
    }

    @Bean
    fun llmClient(): WebClient {
        return WebClient.builder()
            .baseUrl(llmServiceUrl)
            .clientConnector(ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(60))
            ))
            .defaultHeader("Content-Type", "application/json")
            .build()
    }
}

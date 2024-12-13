package msa.content.repository

import msa.content.data.CrawledUrl
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CrawledUrlRepository : CoroutineCrudRepository<CrawledUrl, String> {
    suspend fun existsByUrl(url: String): Boolean
}

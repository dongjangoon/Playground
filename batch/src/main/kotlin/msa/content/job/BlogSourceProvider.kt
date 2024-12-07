package msa.content.job

import msa.common.enum.PostCategory
import msa.content.dto.SourceUrl
import msa.content.enum.BlogType
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class BlogSourceProvider(
    @Value("classpath:blogs.txt")
    private val blogResource: Resource,
) {
    // lazy 초기화로 파일을 한 번만 읽어오도록 함
    private val blogSources: List<SourceUrl> by lazy { loadBlogSources() }

    private fun loadBlogSources(): List<SourceUrl> =
        blogResource.inputStream.bufferedReader().useLines { lines ->
            lines.filter { it.isNotBlank() }
                .map { line ->
                    val (url, category, type) = line.split(",").map { it.trim() }
                    SourceUrl(
                        url = url,
                        category = PostCategory.valueOf(category),
                        type = BlogType.valueOf(type),
                    )
                }.toList()
        }

    fun getBlogSources(): List<SourceUrl> = blogSources
}

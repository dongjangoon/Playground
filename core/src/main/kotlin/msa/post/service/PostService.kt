package msa.post.service

import kotlinx.coroutines.flow.toList
import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import msa.common.exception.PostNotFoundException
import msa.post.data.Post
import msa.post.repository.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface PostService {
    suspend fun createPost(
        title: String,
        content: String,
        summary: String,
        category: PostCategory,
        authorId: String,
        tags: List<String>
    ): Post

    suspend fun updatePost(
        id: String,
        title: String?,
        content: String?,
        summary: String?,
        category: PostCategory?,
        tags: List<String>?
    ): Post

    suspend fun publishPost(id: String): Post

    suspend fun getPost(id: String): Post

    suspend fun getPosts(category: PostCategory?, status: PostStatus?): List<Post>

    suspend fun getPostsByAuthorId(authorId: String): List<Post>
}

@Service
class PostServiceImpl(
    private val postRepository: PostRepository
) : PostService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun createPost(
        title: String,
        content: String,
        summary: String,
        category: PostCategory,
        authorId: String,
        tags: List<String>
    ): Post = Post.create(title, content, summary, category, authorId, tags)
            .let { postRepository.save(it) }


    override suspend fun updatePost(
        id: String,
        title: String?,
        content: String?,
        summary: String?,
        category: PostCategory?,
        tags: List<String>?
    ): Post = postRepository.findById(id)
            ?.let { post ->
                postRepository.save(
                    post.copy(
                        title = title ?: post.title,
                        content = content ?: post.content,
                        summary = summary ?: post.summary,
                        category = category ?: post.category,
                        tags = tags ?: post.tags,
                        updatedAt = LocalDateTime.now()
                    )
                )
            } ?: throw PostNotFoundException

    override suspend fun publishPost(id: String): Post =
        postRepository.findById(id)?.let { post ->
            postRepository.save(
                post.copy(
                    status = PostStatus.PUBLISHED,
                    updatedAt = LocalDateTime.now()
                )
            )
        } ?: throw PostNotFoundException

    override suspend fun getPost(id: String): Post =
        postRepository.findById(id) ?: throw PostNotFoundException

    override suspend fun getPosts(category: PostCategory?, status: PostStatus?): List<Post> =
        when {
            category != null && status != null -> postRepository.findByCategoryAndStatus(category, status)
            category != null -> postRepository.findByCategory(category)
            status != null -> postRepository.findByStatus(status)
            else -> postRepository.findAll().toList()
        }

    override suspend fun getPostsByAuthorId(authorId: String): List<Post> =
        postRepository.findByAuthorId(authorId)
}

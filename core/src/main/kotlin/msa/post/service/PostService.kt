package msa.post.service

import kotlinx.coroutines.flow.toList
import msa.common.cache.Cache
import msa.common.cache.CacheKey
import msa.common.cache.get
import msa.common.dto.CursorPage
import msa.common.enum.PostCategory
import msa.common.enum.PostStatus
import msa.common.exception.PostNotFoundException
import msa.post.data.Post
import msa.post.data.PostCursor
import msa.post.dto.CreatePostRequest
import msa.post.dto.UpdatePostRequest
import msa.post.repository.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface PostService {
    suspend fun createPost(createPostRequest: CreatePostRequest): Post

    suspend fun updatePost(
        id: String,
        updatePostRequest: UpdatePostRequest,
    ): Post

    suspend fun publishPost(id: String): Post

    suspend fun getPostAndIncrementViewCount(
        id: String,
        identifier: String,
    ): Post

    suspend fun recommend(
        id: String,
        identifier: String,
    ): Post

    suspend fun getPostsWithCursor(
        cursor: String?,
        category: PostCategory?,
        sortBy: String?,
        limit: Int,
    ): CursorPage<Post>
}

@Service
class PostServiceImpl(
    private val postRepository: PostRepository,
    private val cache: Cache,
) : PostService {
    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun createPost(createPostRequest: CreatePostRequest): Post =
        Post.create(
            createPostRequest.title,
            createPostRequest.sourceUrl,
            createPostRequest.content,
            createPostRequest.summary,
            createPostRequest.category,
            createPostRequest.authorId,
            createPostRequest.tags,
        ).let { postRepository.save(it) }

    override suspend fun updatePost(
        id: String,
        updatePostRequest: UpdatePostRequest,
    ): Post =
        postRepository.findById(id)
            ?.let { post ->
                postRepository.save(
                    post.copy(
                        title = updatePostRequest.title ?: post.title,
                        sourceUrl = updatePostRequest.sourceUrl ?: post.sourceUrl,
                        content = updatePostRequest.content ?: post.content,
                        summary = updatePostRequest.summary ?: post.summary,
                        category = updatePostRequest.category ?: post.category,
                        tags = updatePostRequest.tags ?: post.tags,
                        updatedAt = LocalDateTime.now(),
                    ),
                )
            } ?: throw PostNotFoundException

    override suspend fun publishPost(id: String): Post =
        postRepository.findById(id)?.let { post ->
            postRepository.save(
                post.copy(
                    status = PostStatus.PUBLISHED,
                    updatedAt = LocalDateTime.now(),
                ),
            )
        } ?: throw PostNotFoundException

    override suspend fun getPostAndIncrementViewCount(
        id: String,
        identifier: String,
    ): Post {
        val viewKey = CacheKey.Post.view(id, identifier)
        val post = getPost(id) ?: throw PostNotFoundException

        if (cache.get<Boolean>(viewKey) == true) {
            return post
        }

        val lockKey = CacheKey.Post.lock(id)
        try {
            if (cache.acquireLock(lockKey)) {
                val updatedPost =
                    postRepository.save(
                        post.copy(
                            viewCount = post.viewCount + 1,
                            updatedAt = LocalDateTime.now(),
                        ),
                    )
                cache.set(viewKey, true)
                return updatedPost
            }
        } finally {
            cache.releaseLock(lockKey)
        }

        return post
    }

    override suspend fun recommend(
        id: String,
        identifier: String,
    ): Post {
        val recommendKey = CacheKey.Post.recommend(id, identifier)
        val post = getPost(id) ?: throw PostNotFoundException

        if (cache.get<Boolean>(recommendKey) == true) {
            return post
        }

        val lockKey = CacheKey.Post.lock(id)
        try {
            if (cache.acquireLock(lockKey)) {
                val updatedPost =
                    postRepository.save(
                        post.copy(
                            recommendCount = post.recommendCount + 1,
                            updatedAt = LocalDateTime.now(),
                        ),
                    )
                cache.set(recommendKey, true)
                return updatedPost
            }
        } finally {
            cache.releaseLock(lockKey)
        }

        return post
    }

    private suspend fun getPost(id: String): Post = postRepository.findById(id) ?: throw PostNotFoundException

    override suspend fun getPostsWithCursor(
        cursor: String?,
        category: PostCategory?,
        sortBy: String?,
        limit: Int,
    ): CursorPage<Post> {
        val actualLimit = limit.coerceIn(1, 100)
        val initialCursor = cursor?.let { PostCursor.decode(it) }

        val posts =
            when {
                sortBy == "recommend" -> {
                    val recommendCount = initialCursor?.recommendCount ?: Long.MAX_VALUE
                    val createdAt = initialCursor?.createdAt ?: LocalDateTime.now()
                    postRepository.findByRecommendCountLessThanAndCreatedAtLessThanOrderByRecommendCountDescCreatedAtDesc(
                        recommendCount,
                        createdAt,
                        actualLimit + 1,
                    )
                }
                category != null -> {
                    val createdAt = initialCursor?.createdAt ?: LocalDateTime.now()
                    postRepository.findByCategoryAndCreatedAtLessThanOrderByCreatedAtDesc(
                        category,
                        createdAt,
                        actualLimit + 1,
                    )
                }
                else -> {
                    val createdAt = initialCursor?.createdAt ?: LocalDateTime.now()
                    postRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(
                        createdAt,
                        actualLimit + 1,
                    )
                }
            }.toList()

        val hasNext = posts.size > actualLimit
        val content = posts.take(actualLimit)

        val nextCursor =
            if (hasNext && content.isNotEmpty()) {
                val lastPost = content.last()
                PostCursor(
                    id = lastPost.id!!,
                    createdAt = lastPost.createdAt,
                    recommendCount = lastPost.recommendCount,
                ).encode()
            } else {
                null
            }

        return CursorPage(
            content = content,
            cursor = nextCursor,
            hasNext = hasNext,
        )
    }
}

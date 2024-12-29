package msa.common.cache

import java.time.Duration

interface BuiltCacheKey {
    val key: String
    val ttl: Duration
}

sealed class CacheKey {
    abstract fun build(vararg args: Any?): BuiltCacheKey

    protected fun buildKey(
        keyFormat: String,
        ttl: Duration,
        vararg args: Any?,
    ): BuiltCacheKey {
        val key = keyFormat.format(*args)
        return object : BuiltCacheKey {
            override val key: String = key
            override val ttl: Duration = ttl
        }
    }

    // Post
    object Post : CacheKey() {
        private const val PREFIX = "post"

        fun entity(id: String) =
            buildKey(
                keyFormat = "$PREFIX:entity:%s",
                ttl = Duration.ofMinutes(30),
                args = arrayOf(id),
            )

        fun view(
            id: String,
            identifier: String,
        ) = buildKey(
            keyFormat = "$PREFIX:view:%s:%s",
            ttl = Duration.ofHours(24),
            args = arrayOf(id, identifier),
        )

        fun recommend(
            id: String,
            identifier: String,
        ) = buildKey(
            keyFormat = "$PREFIX:recommend:%s:%s",
            ttl = Duration.ofDays(30),
            args = arrayOf(id, identifier),
        )

        fun lock(id: String) =
            buildKey(
                keyFormat = "$PREFIX:lock:%s",
                ttl = Duration.ofSeconds(3),
                args = arrayOf(id),
            )

        override fun build(vararg args: Any?) = entity(args[0].toString())
    }
}

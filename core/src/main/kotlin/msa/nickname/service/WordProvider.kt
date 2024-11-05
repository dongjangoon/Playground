package msa.nickname.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class WordProvider(
    @Value("classpath:adjectives.txt")
    private val adjectiveResource: Resource,
    @Value("classpath:nouns.txt")
    private val nounResource: Resource,
) {
    private val adjectives: List<String> by lazy { loadWords(adjectiveResource) }
    private val nouns: List<String> by lazy { loadWords(nounResource) }

    private fun loadWords(resource: Resource): List<String> =
        resource.inputStream.bufferedReader().useLines { lines ->
            lines.filter { it.isNotBlank() }
                .map { it.trim() }
                .toList()
        }

    fun getRandomAdjective(): String = adjectives.random()

    fun getRandomNoun(): String = nouns.random()
}

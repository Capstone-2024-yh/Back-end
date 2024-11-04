package com.capstone.backend.Service

import com.capstone.backend.Entity.SearchToken
import com.capstone.backend.Repository.SearchTokenRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchTokenService(
    private val searchTokenRepository: SearchTokenRepository,
    private val w2v: Word2VectorService
) {

    fun getTokensByUserId(userId: Int): List<SearchToken> {
        return searchTokenRepository.findByUserId(userId)
    }

    fun getTokensByTokenValue(token: String): List<SearchToken> {
        return searchTokenRepository.findByToken(token)
    }

    fun getTokensByUserIdAndToken(userId: Int, token: String): List<SearchToken> {
        return searchTokenRepository.findByUserIdAndToken(userId, token)
    }

    @Transactional
    fun saveSearchTokens(userId: Int, tokens :List<String>): List<SearchToken> {
        val vectors = runBlocking {
            w2v.getWord2Vector(tokens)
        }

        val searchTokens = tokens.map { token ->
            SearchToken(
                userId = userId,
                token = token,
                vector = vectors[token] ?: throw IllegalArgumentException("Word2Vec API returned no result")
            )
        }
        return searchTokenRepository.saveAll(searchTokens)
    }

    @Transactional
    fun deleteSearchTokenById(id: Int) {
        searchTokenRepository.deleteById(id)
    }

    @Transactional
    fun deleteTokensByUserId(userId: Int) {
        searchTokenRepository.deleteAll(getTokensByUserId(userId))
    }
}

package com.capstone.backend.Service

import com.capstone.backend.Entity.SearchToken
import com.capstone.backend.Repository.SearchTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SearchTokenService(private val searchTokenRepository: SearchTokenRepository) {

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
    fun saveSearchToken(searchToken: SearchToken): SearchToken {
        return searchTokenRepository.save(searchToken)
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

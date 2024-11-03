package com.capstone.backend.Repository

import com.capstone.backend.Entity.SearchToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchTokenRepository : JpaRepository<SearchToken, Int> {

    fun findByUserId(userId: Int): List<SearchToken>

    fun findByToken(token: String): List<SearchToken>

    fun findByUserIdAndToken(userId: Int, token: String): List<SearchToken>

    fun findAllByVectorIsNotNull(): List<SearchToken>
}

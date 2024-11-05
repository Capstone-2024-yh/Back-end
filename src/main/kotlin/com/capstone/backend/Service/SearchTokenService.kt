package com.capstone.backend.Service

import com.capstone.backend.Entity.SearchToken
import com.capstone.backend.Repository.SearchTokenRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import smile.clustering.DBSCAN

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

    @Transactional(readOnly = true)
    fun performDBSCANClustering(eps: Double = 0.5, minSamples: Int = 5): List<Pair<Int, Int>> {
        //eps: 이웃을 정의하는 거리, minSamples: 핵심 샘플로 간주되기 위한 이웃의 수

        // 모든 SearchToken 엔티티를 가져옴 (vector가 null이 아닌 경우)
        val searchTokens = searchTokenRepository.findAllByVectorIsNotNull()

        // 벡터 및 ID 리스트 생성
        val ids = mutableListOf<Int>()
        val vectors = mutableListOf<DoubleArray>()

        for (searchToken in searchTokens) {
            ids.add(searchToken.id!!)
            vectors.add(searchToken.vector.map { it.toDouble() }.toDoubleArray())
        }

        // DBSCAN 군집화 수행
        val dbscan = DBSCAN.fit(vectors.toTypedArray(), minSamples, eps)

        // 군집화 결과를 (ID, 군집 ID) 형태로 저장
        val clusteredResults = ids.mapIndexed { index, id -> id to dbscan.y[index] }

        // 결과를 로그로 출력하거나 다른 방식으로 사용
        clusteredResults.forEach { (id, cluster) ->
            println("Token ID: $id is in cluster $cluster")
        }

        return clusteredResults
    }
}

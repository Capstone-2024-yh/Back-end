package com.capstone.backend.Service

import com.capstone.backend.Entity.ClusterSummary
import com.capstone.backend.Entity.SearchToken
import com.capstone.backend.Repository.ClusterSummaryRepository
import com.capstone.backend.Repository.SearchTokenRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import smile.clustering.DBSCAN
import java.time.LocalDateTime
import kotlin.math.sqrt

@Service
class SearchTokenService(
    private val searchTokenRepository: SearchTokenRepository,
    private val w2v: Word2VectorService,
    private val clusterSummaryRepository: ClusterSummaryRepository
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

//    @Transactional(readOnly = true)
    fun performDBSCANClustering(eps: Double = 1.0, minSamples: Int = 2): List<Triple<Int, Int, String>> {
        // eps: 이웃을 정의하는 거리, minSamples: 핵심 샘플로 간주되기 위한 이웃의 수

        // 모든 SearchToken 엔티티를 가져옴 (vector가 null이 아닌 경우)
        val searchTokens = searchTokenRepository.findAllByVectorIsNotNull()

        // 벡터 및 ID, 단어 리스트 생성
        val ids = mutableListOf<Int>()
        val vectors = mutableListOf<DoubleArray>()
        val words = mutableListOf<String?>() // 단어 저장용 리스트

        for (searchToken in searchTokens) {
            ids.add(searchToken.id!!)
            vectors.add(searchToken.vector.map { it.toDouble() }.toDoubleArray())
            words.add(searchToken.token) // 단어를 저장 (필드 이름이 token이라고 가정)
        }

        // DBSCAN 군집화 수행
        val dbscan = DBSCAN.fit(vectors.toTypedArray(), minSamples, eps)

        // 클러스터 ID별 단어 그룹화 및 중심 계산
        val clusteredWords = mutableMapOf<Int, MutableList<Pair<DoubleArray, String>>>()

        ids.forEachIndexed { index, _ ->
            val clusterId = dbscan.y[index] // 클러스터 ID
            val word = words[index] ?: "Unknown" // 단어 가져오기
            if (clusterId != 2147483647) { // 노이즈 제외
                clusteredWords.computeIfAbsent(clusterId) { mutableListOf() }.add(vectors[index] to word)
            }
        }

        // 클러스터 중심과 가장 가까운 단어 찾기
        val clusterResults = mutableListOf<Triple<Int, Int, String>>() // (클러스터 ID, 단어 수, 가장 중심 단어)
        val currentTimestamp = LocalDateTime.now()

        clusteredWords.forEach { (clusterId, wordVectors) ->
            // 클러스터 중심 계산
            val clusterCentroid = calculateCentroid(wordVectors.map { it.first })

            // 가장 중심에 가까운 단어 찾기
            val closestWord = wordVectors.minByOrNull { (vector, _) ->
                euclideanDistance(vector, clusterCentroid)
            }?.second ?: "Unknown"

            // 클러스터 단어 묶어서 출력
            val wordsInCluster = wordVectors.joinToString(", ") { it.second }
            println("Cluster $clusterId:")
            println("Words: $wordsInCluster")
            println("Center Word: $closestWord")
            println()

            val clusterSummary = ClusterSummary(
                clusterId = clusterId,
                tokenText = closestWord,
                clusterSize = wordVectors.size,
                createdAt = currentTimestamp
            )
            clusterSummaryRepository.save(clusterSummary)
        }






        return clusteredWords.map { (clusterId, wordVectors) ->
            val clusterCentroid = calculateCentroid(wordVectors.map { it.first })
            val closestWord = wordVectors.minByOrNull { (vector, _) ->
                euclideanDistance(vector, clusterCentroid)
            }?.second ?: "Unknown"
            Triple(clusterId, wordVectors.size, closestWord)
        }
    }

    // 유클리드 거리 계산 함수
    fun euclideanDistance(vector1: DoubleArray, vector2: DoubleArray): Double {
        return sqrt(vector1.zip(vector2) { v1, v2 -> (v1 - v2) * (v1 - v2) }.sum())
    }

    // 클러스터 중심 계산 함수
    fun calculateCentroid(vectors: List<DoubleArray>): DoubleArray {
        val dimension = vectors[0].size
        val centroid = DoubleArray(dimension)

        for (vector in vectors) {
            for (i in 0 until dimension) {
                centroid[i] += vector[i]
            }
        }

        return centroid.map { it / vectors.size }.toDoubleArray()
    }
}

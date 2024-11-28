package com.capstone.backend.Service

import com.capstone.backend.Entity.ClusterSummary
import com.capstone.backend.Repository.ClusterSummaryRepository
import com.capstone.backend.Repository.SearchTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import smile.clustering.DBSCAN
import kotlin.math.pow
import kotlin.math.sqrt

@Service
class ClusterSummaryService(
    private val searchTokenRepository: SearchTokenRepository,
    private val clusterSummaryRepository: ClusterSummaryRepository
) {

    @Transactional
    fun performAndSaveClustering(eps: Double = 0.5, minSamples: Int = 5) {
        // 모든 SearchToken 엔티티를 가져옴 (vector가 null이 아닌 경우)
        val searchTokens = searchTokenRepository.findAllByVectorIsNotNull()

        val ids = searchTokens.map { it.id!! }
        val vectors = searchTokens.map { it.vector.map { it.toDouble() }.toDoubleArray() }

        // DBSCAN 군집화 수행
        val dbscan = DBSCAN.fit(vectors.toTypedArray(), minSamples, eps)


        // 각 군집을 순회하며 대표 토큰과 크기를 계산하여 저장
        dbscan.y.distinct().filter { it >= 0 }.forEach { clusterId ->
            val clusterIndices = dbscan.y.indices.filter { dbscan.y[it] == clusterId }
            val clusterVectors = clusterIndices.map { vectors[it] }
            val clusterTokenIds = clusterIndices.map { ids[it] }

            // 군집 중심 계산
            val centroid = calculateCentroid(clusterVectors)
            val representativeIndex = findNearestToCentroid(clusterVectors, centroid)
            val representativeTokenId = clusterTokenIds[representativeIndex]
            val tokenText = searchTokens.find { it.id == representativeTokenId }?.token ?: ""

            // ClusterSummary 엔티티 생성 및 저장
            val clusterSummary = ClusterSummary(
                clusterId = clusterId,
                tokenText = tokenText,
                clusterSize = clusterTokenIds.size
            )
            clusterSummaryRepository.save(clusterSummary)
        }
    }


    // 최신 날짜의 ClusterSummary 데이터 가져오기
    @Transactional(readOnly = true)
    fun getLatestClusterSummaries(): List<ClusterSummary> {
        val latestDate = clusterSummaryRepository.findLatestCreatedAt()
        return if (latestDate != null) {
            clusterSummaryRepository.findAllByLatestCreatedAt(latestDate)
        } else {
            emptyList()
        }
    }

    // 군집 크기 순으로 정렬된 최신 ClusterSummary 데이터 가져오기
    fun getLatestClustersOrderedBySize(): List<ClusterSummary> {
        return clusterSummaryRepository.findLatestClustersOrderedBySize()
    }

    private fun calculateCentroid(vectors: List<DoubleArray>): DoubleArray {
        val length = vectors[0].size
        return DoubleArray(length) { i -> vectors.sumOf { it[i] } / vectors.size }
    }

    private fun findNearestToCentroid(vectors: List<DoubleArray>, centroid: DoubleArray): Int {
        return vectors.indices.minByOrNull { index ->
            sqrt(vectors[index].zip(centroid) { a, b -> (a - b).pow(2) }.sum())
        } ?: 0
    }
}

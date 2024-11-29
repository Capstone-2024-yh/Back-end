package com.capstone.backend.Service

import com.capstone.backend.Repository.VenueTagByDescriptionRepository
import com.capstone.backend.Repository.VenueTagRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val venueTagRepository: VenueTagRepository,
    private val venueTagByDescriptionRepository: VenueTagByDescriptionRepository
) {

    suspend fun findTopVenuesBySimilarity(searchVectors: List<FloatArray>): List<VenueScoreResponse> {
        // 비동기로 쿼리 실행
        val results = coroutineScope {
            searchVectors.map { vector ->
                async {
                    coroutineScope {
                        // 두 쿼리를 병렬로 실행
                        val tagScoresDeferred = async { venueTagRepository.findSimilarTagsWithScore(vector) }
                        val descriptionScoresDeferred = async { venueTagByDescriptionRepository.findSimilarTagsByDescriptionWithScore(vector) }

                        // 두 결과를 병렬로 기다리고 합침
                        tagScoresDeferred.await() + descriptionScoresDeferred.await()
                    }
                }
            }.awaitAll() // 모든 async 작업이 완료될 때까지 대기
        }

        // 결과 합산
        val venueScores = mutableMapOf<Int, Double>()
        results.flatten().forEach { score ->
            // venueId 별로 점수를 합산
            venueScores[score.venueId] = venueScores.getOrDefault(score.venueId, 0.0) + score.similarity
        }

        // 점수에 따라 정렬
        return venueScores.entries.sortedByDescending { it.value }
            .map { VenueScoreResponse(it.key, it.value) }
    }




//
//    fun findTopVenuesBySimilarity(searchVectors: List<FloatArray>): List<VenueScoreResponse> {
//        val venueScores = mutableMapOf<Int, Double>()
//
//        searchVectors.forEach { vector ->
//            // VenueTag 테이블에서 유사도 점수 조회 및 누적
//            val tagScores = venueTagRepository.findSimilarTagsWithScore(vector)
//            tagScores.forEach { score ->
//                venueScores[score.venueId] = venueScores.getOrDefault(score.venueId, 0.0) + score.similarity
//            }
//
//            // VenueTagByDescription 테이블에서 유사도 점수 조회 및 누적
//            val descriptionScores = venueTagByDescriptionRepository.findSimilarTagsByDescriptionWithScore(vector)
//            descriptionScores.forEach { score ->
//                venueScores[score.venueId] = venueScores.getOrDefault(score.venueId, 0.0) + score.similarity
//            }
//        }
//
//        // venueScores 맵을 유사도 점수에 따라 내림차순 정렬 후 리스트로 변환
//        return venueScores.entries.sortedByDescending { it.value }
//            .map { VenueScoreResponse(it.key, it.value) }
//    }
}

data class VenueScoreResponse(
    val venueId: Int,
    val totalScore: Double
)


data class SearchRequest(
    val uid: Int,
    val keyword: String,
    val location: String?,
    val minCapacity: Int?,
    val maxCapacity: Int?,
    val minFee: Double?,
    val maxFee: Double?,
    val spaceType: String?
)
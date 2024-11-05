package com.capstone.backend.Service

import com.capstone.backend.Controller.VenueInfoResponse
import com.capstone.backend.Repository.VenueTagByDescriptionRepository
import com.capstone.backend.Repository.VenueTagRepository
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val venueTagRepository: VenueTagRepository,
    private val venueTagByDescriptionRepository: VenueTagByDescriptionRepository
) {

    fun findTopVenuesBySimilarity(searchVectors: List<FloatArray>): List<VenueScoreResponse> {
        val venueScores = mutableMapOf<Int, Double>()

        searchVectors.forEach { vector ->
            // VenueTag 테이블에서 유사도 점수 조회 및 누적
            val tagScores = venueTagRepository.findSimilarTagsWithScore(vector)
            tagScores.forEach { score ->
                venueScores[score.venueId] = venueScores.getOrDefault(score.venueId, 0.0) + score.similarity
            }

            // VenueTagByDescription 테이블에서 유사도 점수 조회 및 누적
            val descriptionScores = venueTagByDescriptionRepository.findSimilarTagsByDescriptionWithScore(vector)
            descriptionScores.forEach { score ->
                venueScores[score.venueId] = venueScores.getOrDefault(score.venueId, 0.0) + score.similarity
            }
        }

        // venueScores 맵을 유사도 점수에 따라 내림차순 정렬 후 리스트로 변환
        return venueScores.entries.sortedByDescending { it.value }
            .map { VenueScoreResponse(it.key, it.value) }
    }
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
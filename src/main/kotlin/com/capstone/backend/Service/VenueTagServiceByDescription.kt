package com.capstone.backend.Service

import com.capstone.backend.Entity.VenueTagByDescription
import com.capstone.backend.Repository.VenueTagByDescriptionRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VenueTagByDescriptionService(
    private val venueTagByDescriptionRepository: VenueTagByDescriptionRepository,
    private val w2v: Word2VectorService
) {
    // venueId로 VenueTagByDescription 목록 조회
    fun getVenueTagsByVenueId(venueId: Int): List<VenueTagByDescription> {
        return venueTagByDescriptionRepository.findByVenueId(venueId)
    }

    // tag로 VenueTagByDescription 목록 조회
    fun getVenueTagsByTag(tag: String): List<VenueTagByDescription> {
        return venueTagByDescriptionRepository.findByTag(tag)
    }

    // 유사한 태그로 VenueTagByDescription 목록 조회
    fun getVenueTagsBySimilarTag(tags: List<String>): List<VenueTagByDescription> {
        val vectors = runBlocking {
            w2v.getWord2Vector(tags)
        }
        return venueTagByDescriptionRepository.findBySimilarTag(vectors)
    }

    // 새로운 VenueTagByDescription 생성
    @Transactional
    fun createVenueTags(venueId: Int, tags: List<String>): List<VenueTagByDescription> {
        val vectors = runBlocking {
            // Word2Vec API에서 벡터를 얻어오는 부분
            w2v.getWord2Vector(tags)
        }

        tags.forEach { tag ->
            val vector = vectors[tag] ?: throw IllegalArgumentException("Word2Vec API returned no result")
            venueTagByDescriptionRepository.upsertVenueTagByDescription(venueId, tag, vector)
        }

        return tags.map { tag ->
            VenueTagByDescription(
                venueId = venueId,
                tag = tag,
                vector = vectors[tag] ?: throw IllegalArgumentException("Word2Vec API returned no result")
            )
        }
    }

    // VenueTagByDescription 삭제
    @Transactional
    fun deleteVenueTag(id: Long) {
        if (venueTagByDescriptionRepository.existsById(id)) {
            venueTagByDescriptionRepository.deleteById(id)
        } else {
            throw IllegalArgumentException("VenueTagByDescription with ID $id does not exist")
        }
    }
}

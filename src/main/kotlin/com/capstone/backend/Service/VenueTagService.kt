package com.capstone.backend.Service

import com.capstone.backend.Entity.VenueTag
import com.capstone.backend.Repository.VenueTagRepository
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VenueTagService(
    private val venueTagRepository: VenueTagRepository,
    private val w2v: Word2VectorService
) {
    // venueId로 VenueTag 목록 조회
    fun getVenueTagsByVenueId(venueId: Int): List<VenueTag> {
        return venueTagRepository.findByVenueId(venueId)
    }

    // tag로 VenueTag 목록 조회
    fun getVenueTagsByTag(tag: String): List<VenueTag> {
        return venueTagRepository.findByTag(tag)
    }

    fun getVenueTagsBySimilarTag(tags: List<String>): List<VenueTag> {
        val vectors = runBlocking {
            w2v.getWord2Vector(tags)
        }
        return venueTagRepository.findBySimilarTag(vectors)
    }

    fun getVenueTagByPopulation(population: Int): List<VenueTag> {
        val tags = venueTagRepository.findAll()
        return tags.subList(0, population)
    }

    //새로운 VenueTag 생성
    @Transactional
    fun createVenueTags(venueId: Int, tags: List<String>): List<VenueTag> {
        val vectors = runBlocking {
            // Word2Vec API에서 벡터를 얻어오는 부분
            w2v.getWord2Vector(tags)
        }

        tags.forEach { tag ->
            val vector = vectors[tag] ?: throw IllegalArgumentException("Word2Vec API returned no result")
            venueTagRepository.upsertVenueTag(venueId, tag, vector)
        }

        return tags.map { tag ->
            VenueTag(
                venueId = venueId,
                tag = tag,
                vector = vectors[tag] ?: throw IllegalArgumentException("Word2Vec API returned no result")
            )
        }
    }
    // VenueTag 삭제
    @Transactional
    fun deleteVenueTag(id: Long) {
        if (venueTagRepository.existsById(id)) {
            venueTagRepository.deleteById(id)
        } else {
            throw IllegalArgumentException("VenueTag with ID $id does not exist")
        }
    }
}

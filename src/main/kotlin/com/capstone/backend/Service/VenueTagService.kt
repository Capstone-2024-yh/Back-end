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
            val result = w2v.getWord2Vector(tags)
            result.values.flatten()
        }
        return venueTagRepository.findBySimilarTag(vectors)
    }

    // 새로운 VenueTag 생성
//    @Transactional
//    fun createVenueTag(venueId: Int, tag: String): VenueTag {
//        return venueTagRepository.save(VenueTag(
//            venueId = venueId,
//            tag = tag,
//            vector = runBlocking {
//                val result = w2v.getWord2Vector(listOf(tag))  // 코루틴 블록 내에서 suspend 함수 호출
//                result[tag] ?: throw IllegalArgumentException("Word2Vec API returned no result")
//            }
//        ))
//    }

    //새로운 VenueTag 생성
    @Transactional
    fun createVenueTags(venueId: Int, tags: List<String>): List<VenueTag> {
        val vectors = runBlocking {
            val result = w2v.getWord2Vector(tags)
            result
        }
        return venueTagRepository.saveAll(tags.map {
            VenueTag(
                venueId = venueId,
                tag = it,
                vector = vectors[it] ?: throw IllegalArgumentException("Word2Vec API returned no result")
            )
        })
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

package com.capstone.backend.Service

import com.capstone.backend.Entity.VenueTag
import com.capstone.backend.Repository.VenueTagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VenueTagService(
    private val venueTagRepository: VenueTagRepository
) {

    // 모든 VenueTag 레코드 조회
    fun getAllVenueTags(): List<VenueTag> {
        return venueTagRepository.findAll()
    }

    // venueId로 VenueTag 목록 조회
    fun getVenueTagsByVenueId(venueId: Int): List<VenueTag> {
        return venueTagRepository.findByVenueId(venueId)
    }

    // tag로 VenueTag 목록 조회
    fun getVenueTagsByTag(tag: String): List<VenueTag> {
        return venueTagRepository.findByTag(tag)
    }

    // 새로운 VenueTag 생성
    @Transactional
    fun createVenueTag(venueTag: VenueTag): VenueTag {
        return venueTagRepository.save(venueTag)
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

    // VenueTag 업데이트 (존재할 경우)
    @Transactional
    fun updateVenueTag(id: Long, updatedVenueTag: VenueTag): VenueTag {
        val existingVenueTag = venueTagRepository.findById(id)
            .orElseThrow { IllegalArgumentException("VenueTag with ID $id does not exist") }

        val updatedEntity = existingVenueTag.copy(
            venueId = updatedVenueTag.venueId,
            tag = updatedVenueTag.tag
        )

        return venueTagRepository.save(updatedEntity)
    }
}

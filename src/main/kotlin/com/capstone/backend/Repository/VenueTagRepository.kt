package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenueTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VenueTagRepository : JpaRepository<VenueTag, Long> {

    // venue_id로 검색하는 메서드
    fun findByVenueId(venueId: Int): List<VenueTag>

    // tag로 검색하는 메서드
    fun findByTag(tag: String): List<VenueTag>
}

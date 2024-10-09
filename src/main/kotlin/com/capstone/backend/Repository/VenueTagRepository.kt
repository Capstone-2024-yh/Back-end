package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenueTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface VenueTagRepository : JpaRepository<VenueTag, Long> {

    // venue_id로 검색하는 메서드
    fun findByVenueId(venueId: Int): List<VenueTag>

    // tag로 검색하는 메서드
    fun findByTag(tag: String): List<VenueTag>

//    벡터로 유사 검색하는 쿼리 나중에 구현 해야 함 지금 그냥 임시 쿼리임
    @Query("SELECT v FROM VenueTag v")  // 모든 VenueTag 반환 (임시 쿼리)
    fun findBySimilarTag(@Param("vector") vector: Map<String, FloatArray>): List<VenueTag>

}

package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenueTagByDescription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface VenueTagByDescriptionRepository : JpaRepository<VenueTagByDescription, Long> {

    // venue_id로 검색하는 메서드
    fun findByVenueId(venueId: Int): List<VenueTagByDescription>

    // tag로 검색하는 메서드
    fun findByTag(tag: String): List<VenueTagByDescription>

    @Modifying
    @Transactional
    @Query("""
        INSERT INTO venue_tag_by_description (venue_id, tag, vector, created_at)
        VALUES (:venueId, :tag, :vector, CURRENT_TIMESTAMP)
        ON CONFLICT (venue_id, tag)
        DO UPDATE SET vector = :vector, created_at = CURRENT_TIMESTAMP
    """, nativeQuery = true)
    fun upsertVenueTagByDescription(venueId: Int, tag: String, vector: FloatArray)

    // 벡터로 유사 검색하는 쿼리 (임시 쿼리)
    @Query("SELECT v FROM VenueTagByDescription v")  // 모든 VenueTagByDescription 반환 (임시 쿼리)
    fun findBySimilarTag(@Param("vector") vector: Map<String, FloatArray>): List<VenueTagByDescription>

    @Query(
        value = """
        SELECT vtd.venue_id AS venueId, 1 - (vtd.vector <-> :vector) AS similarity
        FROM venue_tag_by_description vtd
        ORDER BY similarity DESC
        LIMIT 50
       """, nativeQuery = true
    )
    fun findSimilarTagsByDescriptionWithScore(@Param("vector") vector: FloatArray): List<SimilarityScore>

}

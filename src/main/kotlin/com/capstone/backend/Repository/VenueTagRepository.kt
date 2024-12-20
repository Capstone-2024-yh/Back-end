package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenueTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
interface VenueTagRepository : JpaRepository<VenueTag, Long> {

    // venue_id로 검색하는 메서드
    fun findByVenueId(venueId: Int): List<VenueTag>

    // tag로 검색하는 메서드
    fun findByTag(tag: String): List<VenueTag>

    @Modifying
    @Transactional
    @Query("""
        INSERT INTO venue_tag (venue_id, tag, vector, created_at)
        VALUES (:venueId, :tag, :vector, CURRENT_TIMESTAMP)
        ON CONFLICT (venue_id, tag)
        DO UPDATE SET vector = :vector, created_at = CURRENT_TIMESTAMP
    """, nativeQuery = true)
    fun upsertVenueTag(venueId: Int, tag: String, vector: FloatArray)

//    벡터로 유사 검색하는 쿼리 나중에 구현 해야 함 지금 그냥 임시 쿼리임
    @Query("SELECT v FROM VenueTag v")  // 모든 VenueTag 반환 (임시 쿼리)
    fun findBySimilarTag(@Param("vector") vector: Map<String, FloatArray>): List<VenueTag>


    @Query(
        value = """
        SELECT vt.venue_id AS venueId, 1 - (vt.vector <-> ?::vector) AS similarity
        FROM venue_tag vt
        ORDER BY similarity DESC
        LIMIT 50
       """, nativeQuery = true
    )
    fun findSimilarTagsWithScore(@Param("vector") vector: FloatArray): List<SimilarityScore>

}

interface SimilarityScore {
    val venueId: Int
    val similarity: Double
}

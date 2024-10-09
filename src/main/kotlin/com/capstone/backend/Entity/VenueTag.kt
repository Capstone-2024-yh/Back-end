package com.capstone.backend.Entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime
import com.pgvector.PGvector


@Entity
@Table(
    name = "venue_tag",
    indexes = [
        Index(name = "idx_venue_tag_id", columnList = "id"),
        Index(name = "idx_venue_tag_venue_id", columnList = "venue_id"),
        Index(name = "idx_venue_tag_tag", columnList = "tag")
    ]
)
data class VenueTag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "venue_id", nullable = false)
    val venueId: Int,

    @Column(name = "tag", length = 255)
    val tag: String,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "vector", columnDefinition = "vector")
    val vector: PGvector


) {
    // PGvector를 FloatArray 로 변환하여 반환하는 메서드
    fun getListFromPGVector(): FloatArray {
        return this.vector.toArray()
    }
}



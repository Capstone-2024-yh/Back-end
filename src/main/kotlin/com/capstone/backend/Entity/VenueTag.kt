package com.capstone.backend.Entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

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

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "vector", columnDefinition = "vector")
    val vector: FloatArray

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VenueTag

        if (id != other.id) return false
        if (venueId != other.venueId) return false
        if (tag != other.tag) return false
        if (createdAt != other.createdAt) return false
        if (!vector.contentEquals(other.vector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + venueId
        result = 31 * result + tag.hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + vector.contentHashCode()
        return result
    }
}



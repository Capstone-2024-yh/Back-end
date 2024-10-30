package com.capstone.backend.Entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "search_token",
    indexes = [
        Index(name = "idx_search_token_id", columnList = "id"),
        Index(name = "idx_search_token_user_id", columnList = "user_id"),
        Index(name = "idx_search_token_token", columnList = "token")
    ]
)
data class SearchToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "user_id", nullable = true)
    val userId: Int? = null,

    @Column(name = "token", length = 255)
    val token: String,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "vector", columnDefinition = "vector")
    val vector: FloatArray

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchToken

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (token != other.token) return false
        if (createdAt != other.createdAt) return false
        if (!vector.contentEquals(other.vector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + (userId ?: 0)
        result = 31 * result + token.hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + vector.contentHashCode()
        return result
    }
}

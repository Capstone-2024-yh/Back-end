package com.capstone.backend.Entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "search_record")
data class SearchRecord (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Int? = 0,

    @Column(name = "user_id")
    val userId: Int? = null,

    @Column(name = "search_string")
    val searchString: String? = null,

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    val createAt: LocalDateTime = LocalDateTime.now()
)
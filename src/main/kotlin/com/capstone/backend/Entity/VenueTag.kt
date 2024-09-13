package com.capstone.backend.Entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "venue_tag")
data class VenueTag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "venue_id", nullable = false)
    val venueId: Int,

    @Column(name = "tag")
    val tag: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

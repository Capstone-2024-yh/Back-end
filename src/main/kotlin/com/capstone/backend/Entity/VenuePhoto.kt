package com.capstone.backend.Entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "venue_photos")
data class VenuePhoto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    val photoId: Int = 0,

    @Column(name = "venue_id", nullable = false)
    val venueId: Int,

    @Column(name = "photo_base64", nullable = false)
    val photoBase64: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now()
)


package com.capstone.backend.Entity

import java.time.LocalDateTime
import jakarta.persistence.*

@Entity
@Table(name = "reservations")
data class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @JoinColumn(name = "user_id", nullable = false)
    val userId: Int,

    @JoinColumn(name = "venue_id", nullable = false)
    val venueId: Int,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @Column(name = "end_time", nullable = false)
    val endTime: LocalDateTime,

    @Column(name = "status", nullable = false)
    var status: String = "pending",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
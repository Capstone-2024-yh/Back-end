package com.capstone.backend.Entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "equipment", indexes = [
    Index(name = "idx_venue_id", columnList = "venue_id"),
    Index(name = "idx_equipment", columnList = "equipment")
])
data class Equipment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,  // 자동 증가하는 기본 ID

    @Column(name = "venue_id", nullable = false)
    val venueId: Int,  // 장소 ID

    @Column(name = "equipment_type_id", nullable = false)
    val equipmentId: Int,  // 기자재 정보

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),  // 레코드 생성 시간

    @Column(name = "has_equipment", nullable = false)
    val hasEquipment: Int
)

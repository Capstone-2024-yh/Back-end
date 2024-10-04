package com.capstone.backend.Entity

import jakarta.persistence.*

@Entity
@Table(name = "equipment_type")
data class EquipmentType (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // 자동 증가하는 기본 ID

    @Column(name = "equipment", nullable = false, length = 255)
    val equipment : String
)
package com.capstone.backend.Entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cluster_summary")
data class ClusterSummary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Column(name = "cluster_id", nullable = false)
    val clusterId: Int,

    @Column(name = "token_text", nullable = false)
    val tokenText: String,  // 군집 중심에 위치한 토큰의 텍스트

    @Column(name = "cluster_size", nullable = false)
    val clusterSize: Int,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
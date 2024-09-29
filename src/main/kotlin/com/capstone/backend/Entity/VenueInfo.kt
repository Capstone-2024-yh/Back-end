package com.capstone.backend.Entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import org.locationtech.jts.geom.Point  // PostGIS에서 Point를 사용하기 위한 클래스

@Entity
@Table(name = "venue_info")
data class VenueInfo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    val venueId: Int = 0,  // 장소 ID

    @Column(name = "owner_id", nullable = false)
    val ownerId: Int,  // 소유자 회원 ID

    @Column(name = "address", nullable = false)
    val address: String,  // 주소

    @Column(name = "rental_fee", precision = 10, columnDefinition = "numeric")
    val rentalFee: Double?,  // 대관비

    @Column(name = "capacity")
    val capacity: Int?,  // 수용 인원

    @Column(name = "area", precision = 10, columnDefinition = "numeric")
    val area: Double?,  // 면적

    @Column(name = "space_type")
    val spaceType: String?,  // 공간 유형

    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    val location: Point?,  // 좌표 정보 (PostGIS Point 타입)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),  // 생성 시간

    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()  // 수정 시간
)

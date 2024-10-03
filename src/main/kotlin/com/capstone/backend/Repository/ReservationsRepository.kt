package com.capstone.backend.Repository

import com.capstone.backend.Entity.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ReservationRepository : JpaRepository<Reservation, Int> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있습니다.

    // 예시: 특정 유저의 예약을 찾는 메서드
    fun findByUserId(userId: Int): List<Reservation>

    // 예시: 특정 장소의 예약을 찾는 메서드
    fun findByVenueId(venueId: Int): List<Reservation>

    // 예시: 특정 상태의 예약을 찾는 메서드
    fun findByStatus(status: String): List<Reservation>

    @Query(value = """
        SElECT * FROM reservations r
        where r.venue_id = :venueid
            and ((r.start_Time <= :startTime and r.end_Time > :startTime) 
            or (r.end_Time >= :endTime and r.start_Time < :endTime))""",
        nativeQuery = true)
    fun findByVenueAndTime(
        @Param("venueid") venueId: Int,
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<Reservation>
}
package com.capstone.backend.Repository

import com.capstone.backend.Entity.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<Reservation, Int> {
    // 필요한 경우 추가적인 쿼리 메서드를 정의할 수 있습니다.

    // 예시: 특정 유저의 예약을 찾는 메서드
    fun findByUserId(userId: Int): List<Reservation>

    // 예시: 특정 장소의 예약을 찾는 메서드
    fun findByVenueId(venueId: Int): List<Reservation>

    // 예시: 특정 상태의 예약을 찾는 메서드
    fun findByStatus(status: String): List<Reservation>
}
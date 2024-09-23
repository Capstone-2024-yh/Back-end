package com.capstone.backend.controllers

import com.capstone.backend.Entity.Reservation
import com.capstone.backend.Service.ReservationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/reservation")
class ReservationController(private val reservationService: ReservationService) {

    // 모든 예약 조회
    @GetMapping
    fun getAllReservations(): ResponseEntity<List<Reservation>> {
        val reservations = reservationService.getAllReservations()
        return ResponseEntity.ok(reservations)
    }

    // 특정 사용자의 예약 조회
    @GetMapping("/user/{userId}")
    fun getReservationsByUserId(@PathVariable userId: Int): ResponseEntity<List<Reservation>> {
        val reservations = reservationService.getReservationsByUserId(userId)
        return if (reservations.isNotEmpty()) {
            ResponseEntity.ok(reservations)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    // 특정 장소의 예약 조회
    @GetMapping("/venue/{venueId}")
    fun getReservationsByVenueId(@PathVariable venueId: Int): ResponseEntity<List<Reservation>> {
        val reservations = reservationService.getReservationsByVenueId(venueId)
        return if (reservations.isNotEmpty()) {
            ResponseEntity.ok(reservations)
        } else {
            ResponseEntity.noContent().build()
        }
    }

    // 새로운 예약 생성
    @PostMapping
    fun createReservation(@RequestBody reservationRequest: ReservationRequest): ResponseEntity<Reservation> {
        val reservation = Reservation(
            userId = reservationRequest.userId,
            venueId = reservationRequest.venueId,
            startTime = reservationRequest.startTime,
            endTime = reservationRequest.endTime,
            status = "pending",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val savedReservation = reservationService.saveReservation(reservation)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation)
    }
}

// DTO 정의
data class ReservationRequest(
    val userId: Int,
    val venueId: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
)
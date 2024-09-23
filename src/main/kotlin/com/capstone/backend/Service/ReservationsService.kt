package com.capstone.backend.Service

import com.capstone.backend.Entity.Reservation
import com.capstone.backend.Repository.ReservationRepository
import org.springframework.stereotype.Service

@Service
class ReservationService(private val reservationRepository: ReservationRepository) {

    fun getAllReservations(): List<Reservation> {
        return reservationRepository.findAll()
    }

    fun getReservationsByUserId(userId: Int): List<Reservation> {
        return reservationRepository.findByUserId(userId)
    }

    fun getReservationsByVenueId(venueId: Int): List<Reservation> {
        return reservationRepository.findByVenueId(venueId)
    }

    fun saveReservation(reservation: Reservation): Reservation {
        return reservationRepository.save(reservation)
    }
}
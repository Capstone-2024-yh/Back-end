package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenuePhoto
import org.springframework.data.jpa.repository.JpaRepository

interface VenuePhotoRepository : JpaRepository<VenuePhoto, Int> {
    fun findByVenueId(venueId: Int): List<VenuePhoto>

    fun deleteVenuePhotoByVenueId(venueId: Int)
}

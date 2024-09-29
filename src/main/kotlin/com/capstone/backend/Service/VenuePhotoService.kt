package com.capstone.backend.Service

import com.capstone.backend.Entity.VenuePhoto
import com.capstone.backend.Repository.VenuePhotoRepository
import org.springframework.stereotype.Service

@Service
class VenuePhotoService(
    private val venuePhotoRepository: VenuePhotoRepository
) {

    fun getPhotosByVenueId(venueId: Int): List<VenuePhoto> {
        return venuePhotoRepository.findByVenueId(venueId)
    }

    fun saveVenuePhoto(venuePhoto: VenuePhoto): VenuePhoto {
        return venuePhotoRepository.save(venuePhoto)
    }

    fun deleteVenuePhoto(photoId: Int) {
        venuePhotoRepository.deleteById(photoId)
    }
}

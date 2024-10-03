package com.capstone.backend.Service

import com.capstone.backend.Entity.VenuePhoto
import com.capstone.backend.Repository.VenuePhotoRepository
import org.springframework.stereotype.Service
import java.util.*

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

    fun updateVenuePhoto(photoId : Int, updatePhoto: String) : Optional<VenuePhoto> {
        val photo = venuePhotoRepository.findById(photoId)
        if(photo.isPresent){
            val updatedPhoto = photo.get().copy(
                photoBase64 = updatePhoto
            )
            return Optional.of(venuePhotoRepository.save(updatedPhoto))
        }
        else{
            return Optional.empty()
        }
    }

    fun deleteVenuePhoto(photoId: Int) {
        venuePhotoRepository.deleteById(photoId)
    }

    fun deleteVenuePhotoByVenueId(venueId: Int) {
        venuePhotoRepository.deleteById(venueId)
    }
}

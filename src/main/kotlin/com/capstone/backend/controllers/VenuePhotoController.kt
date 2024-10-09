package com.capstone.backend.controllers

import com.capstone.backend.Entity.VenuePhoto
import com.capstone.backend.Service.VenuePhotoService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/venuePhoto")
class VenuePhotoController(
    private val venuePhotoService: VenuePhotoService
) {
    // 장소 사진 조회
    @GetMapping("/{venueId}")
    fun getPhotosByVenueId(@PathVariable venueId: Int): ResponseEntity<List<VenuePhoto>> {
        val photos = venuePhotoService.getPhotosByVenueId(venueId)
        return ResponseEntity.ok(photos)
    }

    @PostMapping("/create")
    fun createVenuePhoto(@RequestBody request: VenuePhotoDTO): ResponseEntity<VenuePhoto> {
        val photo = venuePhotoService.saveVenuePhoto(
            VenuePhoto(
            venueId = request.venueId,
            photoBase64 = request.base64Image
        ))
        return ResponseEntity.ok(photo)
    }

    @PutMapping("/{venueId}")
    fun updateVenuePhoto(@PathVariable venueId : Int, photoInfo : VenuePhoto) : ResponseEntity<VenuePhoto> {
        val photo = venuePhotoService.updateVenuePhoto(venueId, photoInfo.photoBase64)
        if(photo.isPresent){
            return ResponseEntity.ok(photo.get())
        }
        return ResponseEntity.notFound().build()
    }

    // 장소 사진 삭제(단독 사용 가능성은 없을 것으로 예상)
    @Deprecated("Use any other venue management function")
    @Transactional
    @DeleteMapping("/{photoId}")
    fun deleteVenuePhoto(@PathVariable photoId: Int): ResponseEntity<Void> {
        venuePhotoService.deleteVenuePhotoByVenueId(photoId)
        return ResponseEntity.noContent().build()
    }
}

data class VenuePhotoDTO(
    val venueId : Int,
    val base64Image : String
)
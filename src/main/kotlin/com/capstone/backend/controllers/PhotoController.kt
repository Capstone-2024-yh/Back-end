package com.capstone.backend.controllers

import com.capstone.backend.Entity.VenuePhoto
import com.capstone.backend.Service.VenuePhotoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/photos")
class VenuePhotoController(
    private val venuePhotoService: VenuePhotoService
) {

    // 특정 장소의 사진 조회
    @GetMapping("/venue/{venueId}")
    fun getPhotosByVenueId(@PathVariable venueId: Int): ResponseEntity<List<VenuePhoto>> {
        val photos = venuePhotoService.getPhotosByVenueId(venueId)
        return ResponseEntity.ok(photos)
    }

    // 사진 추가
    @PostMapping("/add")
    fun addVenuePhoto(@RequestBody venuePhoto: VenuePhoto): ResponseEntity<VenuePhoto> {
        val savedPhoto = venuePhotoService.saveVenuePhoto(venuePhoto)
        return ResponseEntity.ok(savedPhoto)
    }

    // 사진 수정
    @PutMapping("/update/{photoId}")
    fun updateVenuePhoto(@PathVariable photoId: Int, @RequestBody updatedPhoto: String): ResponseEntity<VenuePhoto> {
        val photo = venuePhotoService.updateVenuePhoto(photoId, updatedPhoto)
        return if (photo.isPresent) {
            ResponseEntity.ok(photo.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 사진 삭제
    @DeleteMapping("/delete/{photoId}")
    fun deleteVenuePhoto(@PathVariable photoId: Int): ResponseEntity<Void> {
        venuePhotoService.deleteVenuePhoto(photoId)
        return ResponseEntity.noContent().build()
    }

    // 특정 장소의 모든 사진 삭제
    @DeleteMapping("/delete/venue/{venueId}")
    fun deletePhotosByVenueId(@PathVariable venueId: Int): ResponseEntity<Void> {
        venuePhotoService.deleteVenuePhotoByVenueId(venueId)
        return ResponseEntity.noContent().build()
    }
}

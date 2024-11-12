package com.capstone.backend.controllers

import com.capstone.backend.Entity.VenueTag
import com.capstone.backend.Service.VenueTagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tag")
class TagController(
    private val tagService: VenueTagService,
) {
    // venueId로 VenueTag 목록 조회
    @GetMapping("/venue/{venueId}")
    fun getVenueTagsByVenueId(@PathVariable venueId: Int): ResponseEntity<List<VenueTag>> {
        val tags = tagService.getVenueTagsByVenueId(venueId)
        return ResponseEntity.ok(tags)
    }

    @GetMapping("/population")
    fun getVenueTag(@RequestParam size : Int) : ResponseEntity<List<String>> {
        val tags = tagService.getVenueTagByPopulation(size)
        val response = tags.map {
            it.tag
        }
        return ResponseEntity.ok(response)
    }

    // VenueTag 삭제
    @DeleteMapping("/{id}")
    fun deleteVenueTag(@PathVariable id: Long): ResponseEntity<String> {
        tagService.deleteVenueTag(id)
        return ResponseEntity.ok("VenueTag with ID $id deleted successfully")
    }

    // tag로 VenueTag 목록 조회
    @GetMapping("/tag/{tag}")
    fun getVenueTagsByTag(@PathVariable tag: String): ResponseEntity<List<VenueTag>> {
        val tags = tagService.getVenueTagsByTag(tag)
        return ResponseEntity.ok(tags)
    }

    // 새로운 VenueTag 생성
    @PostMapping("/create")
    fun createVenueTag(@RequestBody venueTag: VenueTagRequest): ResponseEntity<List<VenueTag>> {
        val createdTag = tagService.createVenueTags(venueTag.venueId, venueTag.tags)
        return ResponseEntity.ok(createdTag)
    }
}

// DTO 정의
data class VenueTagRequest(
    val venueId: Int,
    val tags: List<String>
)

data class TagResponse(
    val tags: List<String>
)
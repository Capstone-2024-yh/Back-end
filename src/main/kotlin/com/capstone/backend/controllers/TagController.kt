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
    @PostMapping
    fun createVenueTag(@RequestBody venueTag: VenueTag): ResponseEntity<VenueTag> {
        val createdTag = tagService.createVenueTag(venueTag.venueId, venueTag.tag)
        return ResponseEntity.ok(createdTag)
    }

//        // 모든 VenueTag 레코드 조회
//        @GetMapping
//        fun getAllVenueTags(): ResponseEntity<List<VenueTag>> {
//            val tags = tagService.getAllVenueTags()
//            return ResponseEntity.ok(tags)
//        }
//
//

//

}
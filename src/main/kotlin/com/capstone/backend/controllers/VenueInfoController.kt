package com.capstone.backend.controllers

import com.capstone.backend.Entity.VenueInfo
import com.capstone.backend.Service.VenueInfoService
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Coordinate


@RestController
@RequestMapping("/venues")
class VenueInfoController(private val venueInfoService: VenueInfoService) {

    // 모든 장소 정보 조회
    @GetMapping
    fun getAllVenues(): ResponseEntity<List<VenueInfo>> {
        val venues = venueInfoService.getAllVenues()
        return ResponseEntity.ok(venues)
    }

    // ID로 특정 장소 조회
    @GetMapping("/{id}")
    fun getVenueById(@PathVariable id: Long): ResponseEntity<VenueInfo> {
        val venue = venueInfoService.getVenueById(id)
        return if (venue.isPresent) {
            ResponseEntity.ok(venue.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 새로운 장소 추가
    @PostMapping
    fun createVenue(@RequestBody venueInfo: VenueInfo): ResponseEntity<VenueInfo> {
        val createdVenue = venueInfoService.createVenue(venueInfo)
        return ResponseEntity.ok(createdVenue)
    }

    // 장소 정보 수정
    @PutMapping("/{id}")
    fun updateVenue(
        @PathVariable id: Long,
        @RequestBody updatedVenueInfo: VenueInfo
    ): ResponseEntity<VenueInfo> {
        val updatedVenue = venueInfoService.updateVenue(id, updatedVenueInfo)
        return if (updatedVenue.isPresent) {
            ResponseEntity.ok(updatedVenue.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }


    // 좌표 범위 내 장소 검색
    @GetMapping("/search")
    fun getVenuesWithinDistance(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double,
        @RequestParam("distance") distance: Double
    ): ResponseEntity<List<VenueInfo>> {
        val geometryFactory = GeometryFactory()
        val point: Point = geometryFactory.createPoint(Coordinate(longitude, latitude))  // 좌표는 (x, y) 순서로 사용
        val venues = venueInfoService.getVenuesWithinDistance(point, distance)
        return ResponseEntity.ok(venues)
    }

    // 장소 삭제
    @DeleteMapping("/{id}")
    fun deleteVenue(@PathVariable id: Long): ResponseEntity<Void> {
        venueInfoService.deleteVenue(id)
        return ResponseEntity.noContent().build()
    }
}
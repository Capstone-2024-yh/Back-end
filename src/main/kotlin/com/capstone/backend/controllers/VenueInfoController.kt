package com.capstone.backend.controllers

import com.capstone.backend.Entity.VenueInfo
import com.capstone.backend.Entity.VenuePhoto
import com.capstone.backend.Service.EquipmentService
import com.capstone.backend.Service.VenueInfoService
import com.capstone.backend.Service.VenuePhotoService
import org.locationtech.jts.geom.GeometryFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Coordinate


@RestController
@RequestMapping("/venues")
class VenueInfoController(
    private val venueInfoService: VenueInfoService,
    private val venuePhotoService: VenuePhotoService,
    private val equipmentService: EquipmentService
) {
    @Deprecated("Paging 기법으로 제공할 예정")
    @GetMapping("/AllSearch")
    fun getAllVenues(): ResponseEntity<List<VenueInfo>> {
        val venues = venueInfoService.getAllVenues()
        return ResponseEntity.ok(venues)
    }

    // ID로 특정 장소 조회
    @GetMapping("/{id}")
    fun getVenueById(@PathVariable id: Int): ResponseEntity<VenueInfo> {
        val venue = venueInfoService.getVenueById(id)
        return if (venue.isPresent) {
            ResponseEntity.ok(venue.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 새로운 장소 추가(정리 필요)
    @PostMapping("/create")
    fun createVenue(
        @RequestBody venueInfoDTO: VenueInfoDTO
    ): ResponseEntity<VenueInfoDTO> {
        val geometryFactory = GeometryFactory()
        val venueInfo = VenueInfo(
            ownerId = venueInfoDTO.ownerId,
            address = venueInfoDTO.address,
            rentalFee = venueInfoDTO.rentalFee,
            capacity = venueInfoDTO.capacity,
            area = venueInfoDTO.area,
            spaceType = venueInfoDTO.spaceType,
            location = geometryFactory.createPoint(Coordinate(venueInfoDTO.longitude, venueInfoDTO.latitude))
        )
        val createdVenue = venueInfoService.createVenue(venueInfo)

        val resp = createdVenue.location?.let {
            VenueInfoDTO(
                ownerId = createdVenue.ownerId,
                address = createdVenue.address,
                rentalFee = createdVenue.rentalFee!!,
                capacity = createdVenue.capacity!!,
                area = createdVenue.area,
                spaceType = createdVenue.spaceType!!,
                longitude = it.x,
                latitude = it.y
            )
        }
        return ResponseEntity.ok(resp)
    }

    // 장소 정보 수정(정리 필요)
    @PutMapping("/{id}")
    fun updateVenue(
        @PathVariable id: Int,
        @RequestBody updatedVenueInfo: VenueInfoDTO
    ): ResponseEntity<VenueInfoDTO> {
        val geometryFactory = GeometryFactory()
        val updatedInfo = VenueInfo(
            ownerId = updatedVenueInfo.ownerId,
            address = updatedVenueInfo.address,
            rentalFee = updatedVenueInfo.rentalFee,
            capacity = updatedVenueInfo.capacity,
            area = updatedVenueInfo.area,
            spaceType = updatedVenueInfo.spaceType,
            location = geometryFactory.createPoint(Coordinate(updatedVenueInfo.longitude, updatedVenueInfo.latitude))
        )
        val updatedVenue = venueInfoService.updateVenue(id, updatedInfo)
        return if (updatedVenue.isPresent) {
            ResponseEntity.ok(updatedInfo.location?.let {
                VenueInfoDTO(
                    ownerId = updatedInfo.ownerId,
                    address = updatedInfo.address,
                    rentalFee = updatedInfo.rentalFee!!,
                    capacity = updatedInfo.capacity!!,
                    area = updatedInfo.area,
                    spaceType = updatedInfo.spaceType!!,
                    longitude = it.x,
                    latitude = it.y
                )
            })
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 좌표 범위 내 장소 검색
    @GetMapping("/locationSearch")
    fun getVenuesWithinDistance(@RequestBody searchRequest: SearchRequest): ResponseEntity<List<VenueInfo>> {
        val geometryFactory = GeometryFactory()
        val point: Point = geometryFactory.createPoint(Coordinate(searchRequest.coordinateInfo.longitude, searchRequest.coordinateInfo.latitude))  // 좌표는 (x, y) 순서로 사용
        point.srid = 4326
        val venues = venueInfoService.getVenuesWithinDistance(point, searchRequest.coordinateInfo.distance)

        return ResponseEntity.ok(filtering(venues, searchRequest.filter))
    }

    // 장소 삭제
    @DeleteMapping("/{id}")
    fun deleteVenue(@PathVariable id: Int): ResponseEntity<Void> {
        venueInfoService.deleteVenue(id)
        if(venuePhotoService.getPhotosByVenueId(id).size > 0){
            venuePhotoService.deleteVenuePhotoByVenueId(id)
        }
        return ResponseEntity.noContent().build()
    }


    // 필터 기능(추후에 쿼리로 처리할 수 있게 변경 예정)
    fun filtering(venueList : List<VenueInfo>, filter: VenueFilter?) : List<VenueInfo> {
        return if(filter == null) {
            venueList
        }
        else{
            venueList.filter { venueInfo ->
                (filter.address == null || venueInfo.address.contains(filter.address)) &&

                (filter.minRentalFee == null || venueInfo.rentalFee!! >= filter.minRentalFee) &&
                (filter.maxRentalFee == null || venueInfo.rentalFee!! <= filter.maxRentalFee) &&

                (filter.minCapacity == null || venueInfo.capacity!! >= filter.minCapacity) &&

                (filter.minArea == null || venueInfo.area!! >= filter.minArea) &&
                (filter.maxArea == null || venueInfo.area!! <= filter.maxArea) &&

                (filter.spaceType == null || venueInfo.spaceType!! == filter.spaceType)
            }
        }
    }
}

data class SearchRequest(
    val coordinateInfo: CoordinateInfo,
    val filter: VenueFilter?
)

data class CoordinateInfo (
    val latitude: Double,
    val longitude: Double,
    val distance: Double
)

data class VenueInfoDTO(
    val ownerId : Int,
    val address : String,
    val rentalFee : Double,
    val capacity : Int,
    val area : Double?,
    val spaceType : String,
    val latitude: Double,
    val longitude: Double
)

data class VenueFilter(
    val address : String?,
    val minRentalFee : Double?,
    val maxRentalFee : Double?,
    val minCapacity : Int?,
    val minArea : Int?,
    val maxArea : Int?,
    val spaceType : String?
)
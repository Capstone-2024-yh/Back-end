package com.capstone.backend.Controller

import com.capstone.backend.Entity.VenueInfo
import com.capstone.backend.Service.VenueInfoService
import com.capstone.backend.Service.VenuePhotoService
import com.capstone.backend.Service.EquipmentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.GeometryFactory

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
    fun getVenueById(@PathVariable id: Int): ResponseEntity<VenueInfoResponse> {
        val venue = venueInfoService.getVenueById(id)
        return if (venue.isPresent) {
            ResponseEntity.ok(
                VenueInfoResponse(
                    venueId = venue.get().venueId,
                    ownerId = venue.get().ownerId,
                    address = venue.get().address,
                    rentalFee = venue.get().rentalFee!!,
                    area = venue.get().area,
                    capacity = venue.get().capacity!!,
                    spaceType = venue.get().spaceType!!,
                    latitude = venue.get().location!!.y,
                    longitude = venue.get().location!!.x,
                    simpleDescription = venue.get().simpleDescription,
                    description = venue.get().description,
                    facilityInfo = venue.get().facilityInfo,
                    precautions = venue.get().precautions,
                    refundPolicy = venue.get().refundPolicy,
                    websiteURL = venue.get().websiteURL,
                    detailAddress = venue.get().detailAddress
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 새로운 장소 추가
    @PostMapping("/create")
    fun createVenue(
        @RequestBody venueInfoDTO: VenueInfoDTO
    ): ResponseEntity<VenueInfoResponse> {
        val geometryFactory = GeometryFactory()
        val venueInfo = VenueInfo(
            ownerId = venueInfoDTO.ownerId,
            address = venueInfoDTO.address,
            rentalFee = venueInfoDTO.rentalFee,
            capacity = venueInfoDTO.capacity,
            area = venueInfoDTO.area,
            spaceType = venueInfoDTO.spaceType,
            location = geometryFactory.createPoint(Coordinate(venueInfoDTO.longitude, venueInfoDTO.latitude)),
            name = venueInfoDTO.name,
            simpleDescription = venueInfoDTO.simpleDescription,
            description = venueInfoDTO.description,
            facilityInfo = venueInfoDTO.facilityInfo,
            precautions = venueInfoDTO.precautions,
            refundPolicy = venueInfoDTO.refundPolicy,
            websiteURL = venueInfoDTO.websiteURL,
            detailAddress = venueInfoDTO.detailAddress
        )
        val createdVenue = venueInfoService.createVenue(venueInfo)

        val resp = createdVenue.location?.let {
            VenueInfoResponse(
                venueId = createdVenue.venueId,
                ownerId = createdVenue.ownerId,
                address = createdVenue.address,
                rentalFee = createdVenue.rentalFee!!,
                capacity = createdVenue.capacity!!,
                area = createdVenue.area,
                spaceType = createdVenue.spaceType!!,
                longitude = it.x,
                latitude = it.y,
                simpleDescription = createdVenue.simpleDescription,
                description = createdVenue.description,
                facilityInfo = createdVenue.facilityInfo,
                precautions = createdVenue.precautions,
                refundPolicy = createdVenue.refundPolicy,
                websiteURL = createdVenue.websiteURL,
                detailAddress = createdVenue.detailAddress
            )
        }
        return ResponseEntity.ok(resp)
    }

    // 장소 정보 수정
    @PutMapping("/{id}")
    fun updateVenue(
        @PathVariable id: Int,
        @RequestBody updatedVenueInfo: VenueInfoDTO
    ): ResponseEntity<VenueInfoResponse> {
        val geometryFactory = GeometryFactory()
        val updatedInfo = VenueInfo(
            ownerId = updatedVenueInfo.ownerId,
            address = updatedVenueInfo.address,
            rentalFee = updatedVenueInfo.rentalFee,
            capacity = updatedVenueInfo.capacity,
            area = updatedVenueInfo.area,
            spaceType = updatedVenueInfo.spaceType,
            location = geometryFactory.createPoint(Coordinate(updatedVenueInfo.longitude, updatedVenueInfo.latitude)),
            name = updatedVenueInfo.name,
            simpleDescription = updatedVenueInfo.simpleDescription,
            description = updatedVenueInfo.description,
            facilityInfo = updatedVenueInfo.facilityInfo,
            precautions = updatedVenueInfo.precautions,
            refundPolicy = updatedVenueInfo.refundPolicy,
            websiteURL = updatedVenueInfo.websiteURL,
            detailAddress = updatedVenueInfo.detailAddress
        )
        val updatedVenue = venueInfoService.updateVenue(id, updatedInfo)
        return if (updatedVenue.isPresent) {
            val resp = updatedVenue.get().location?.let {
                VenueInfoResponse(
                    venueId = updatedVenue.get().venueId,
                    ownerId = updatedVenue.get().ownerId,
                    address = updatedVenue.get().address,
                    rentalFee = updatedVenue.get().rentalFee!!,
                    capacity = updatedVenue.get().capacity!!,
                    area = updatedVenue.get().area,
                    spaceType = updatedVenue.get().spaceType!!,
                    longitude = it.x,
                    latitude = it.y,
                    simpleDescription = updatedVenueInfo.simpleDescription,
                    description = updatedVenueInfo.description,
                    facilityInfo = updatedVenueInfo.facilityInfo,
                    precautions = updatedVenueInfo.precautions,
                    refundPolicy = updatedVenueInfo.refundPolicy,
                    websiteURL = updatedVenueInfo.websiteURL,
                    detailAddress = updatedVenueInfo.detailAddress,
                )
            }
            ResponseEntity.ok(resp)
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
        if(venuePhotoService.getPhotosByVenueId(id).isNotEmpty()){
            venuePhotoService.deleteVenuePhotoByVenueId(id)
        }
        return ResponseEntity.noContent().build()
    }

    // 필터링 기능
    fun filtering(venueList: List<VenueInfo>, filter: VenueFilter?): List<VenueInfo> {
        return if (filter == null) {
            venueList
        } else {
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

// DTO 및 서브 클래스들

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
    val ownerId: Int,
    val address: String,
    val rentalFee: Double,
    val capacity: Int,
    val area: Double?,
    val spaceType: String,
    val latitude: Double,
    val longitude: Double,
    val name: String?,  // 추가
    val simpleDescription: String?,  // 추가
    val description: String?,  // 추가
    val facilityInfo: String?,  // 추가
    val precautions: String?,  // 추가
    val refundPolicy: String?,  // 추가
    val websiteURL: String?,  // 추가
    val detailAddress: String?  // 추가
)

data class VenueInfoResponse(
    val venueId : Int,
    val ownerId: Int,
    val address: String,
    val rentalFee: Double,
    val capacity: Int,
    val area: Double?,
    val spaceType: String,
    val latitude: Double,
    val longitude: Double,  // 추가
    val simpleDescription: String?,  // 추가
    val description: String?,  // 추가
    val facilityInfo: String?,  // 추가
    val precautions: String?,  // 추가
    val refundPolicy: String?,  // 추가
    val websiteURL: String?,  // 추가
    val detailAddress: String?  // 추가
)

data class VenueFilter(
    val address: String?,
    val minRentalFee: Double?,
    val maxRentalFee: Double?,
    val minCapacity: Int?,
    val minArea: Int?,
    val maxArea: Int?,
    val spaceType: String?
)

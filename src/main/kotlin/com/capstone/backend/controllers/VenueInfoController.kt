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

    // 모든 장소 정보 조회
//    @GetMapping
//    fun getAllVenues(): ResponseEntity<List<VenueInfo>> {
//        val venues = venueInfoService.getAllVenues()
//        return ResponseEntity.ok(venues)
//    }

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
    ): ResponseEntity<VenueInfoResponse> {
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
        if(venueInfoDTO.equipments != null){
            for(equip in venueInfoDTO.equipments){
                //equipmentService.addEquipment(createdVenue.venueId, venueInfoDTO.equipments)
            }
        }

        val venuePhoto = VenuePhoto(
            venueId = createdVenue.venueId,
            photoBase64 = venueInfoDTO.mainImage
        )
        venuePhotoService.saveVenuePhoto(venuePhoto)

        val resp = createdVenue.location?.let {
            VenueInfoResponse(
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
    ): ResponseEntity<VenueInfoResponse> {
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
            val photos = venuePhotoService.getPhotosByVenueId(id)
            venuePhotoService.updateVenuePhoto(photos[0].photoId, updatedVenueInfo.mainImage)
            ResponseEntity.ok(updatedInfo.location?.let {
                VenueInfoResponse(
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
    @GetMapping("/search")
    fun getVenuesWithinDistance(@RequestBody searchRequest: SearchRequest): ResponseEntity<List<VenueInfo>> {
        val geometryFactory = GeometryFactory()
        val point: Point = geometryFactory.createPoint(Coordinate(searchRequest.coordinateInfo.longitude, searchRequest.coordinateInfo.latitude))  // 좌표는 (x, y) 순서로 사용
        point.srid = 4326
        val venues = venueInfoService.getVenuesWithinDistance(point, searchRequest.coordinateInfo.distance)

        return ResponseEntity.ok(filtering(venues, searchRequest.filter))
    }

    @PostMapping("/search")
    fun getVenuesWithText() {
        // 검색어를 받아서 해당하는 장소를 반환
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

    // 장소 사진 조회
    @GetMapping("/photos/{venueId}")
    fun getPhotosByVenueId(@PathVariable venueId: Int): ResponseEntity<List<VenuePhoto>> {
        val photos = venuePhotoService.getPhotosByVenueId(venueId)
        return ResponseEntity.ok(photos)
    }

    // 장소 사진 추가(단독 사용 가능성은 없을 것으로 예상)
    @Deprecated("Use any other venue management function")
    @PostMapping("/photos")
    fun saveVenuePhoto(@RequestBody venuePhoto: VenuePhoto): ResponseEntity<VenuePhoto> {
        val savedPhoto = venuePhotoService.saveVenuePhoto(venuePhoto)
        return ResponseEntity.ok(savedPhoto)
    }

    // 장소 사진 삭제(단독 사용 가능성은 없을 것으로 예상)
    @Deprecated("Use any other venue management function")
    @DeleteMapping("/photos/{photoId}")
    fun deleteVenuePhoto(@PathVariable photoId: Int): ResponseEntity<Void> {
        venuePhotoService.deleteVenuePhoto(photoId)
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

    @GetMapping("/EquipList")
    fun getEquipmentList() : List<String>{
        return equipmentService.getAllEquipmentList()
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
    val equipments : List<String>?,
    val latitude: Double,
    val longitude: Double,
    val mainImage : String //base64 형태로 전달받음
)

data class VenueInfoResponse(
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
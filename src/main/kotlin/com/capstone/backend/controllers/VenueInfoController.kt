package com.capstone.backend.Controller

import com.capstone.backend.Entity.VenueInfo
import com.capstone.backend.Repository.SimpleVenue
import com.capstone.backend.Repository.VenueInfoRepository
import com.capstone.backend.Service.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.GeometryFactory
import java.util.Optional

@RestController
@RequestMapping("/venues")
class VenueInfoController(
    private val venueInfoService: VenueInfoService,
    private val venuePhotoService: VenuePhotoService,
    private val venueTagByDescriptionService: VenueTagByDescriptionService,
    private val gptService: GptService,
    private val rentalFeePolicyService: RentalFeePolicyService
) {
    @Deprecated("Paging 기법으로 제공할 예정")
    @GetMapping("/AllSearch")
    fun getAllVenues(): ResponseEntity<List<VenueInfoResponse>> {
        val venues = venueInfoService.getAllVenues()
        return if(!venues.isEmpty()){
            val ret : MutableList<VenueInfoResponse> = mutableListOf()
            for(element in venues){
                val t = toResponseVenueInfo(element)
                if(t.isPresent) ret.add(t.get())
            }
            if(ret.size > 0){
                ResponseEntity.ok(ret.toList())
            }
            else{
                ResponseEntity.notFound().build()
            }
        }
        else{
            ResponseEntity.notFound().build()
        }
    }

    // ID로 특정 장소 조회
    @GetMapping("/{id}")
    fun getVenueById(@PathVariable id: Int): ResponseEntity<VenueInfoResponse> {
        val venue = venueInfoService.getVenueById(id)
        return if (venue.isPresent) {
            ResponseEntity.ok(toResponseVenueInfo(venue.get()).get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/User/{id}")
    fun getVenueByUserId(@PathVariable id: Int): ResponseEntity<List<VenueInfoResponse>> {
        val venues = venueInfoService.getVenuesByUserId(id)

        if (venues.isPresent) {
            val list : MutableList<VenueInfoResponse> = mutableListOf()
            venues.get().forEach { venue ->
                val response = toResponseVenueInfo(venue)
                if(response.isPresent){
                    list.add(response.get())
                }
            }

            return ResponseEntity.ok(list.toList())
        }

        return ResponseEntity.notFound().build()
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
        createdVenue.rentalFee?.let { rentalFeePolicyService.addDefaultFee(createdVenue.venueId, it) }

        makeVenueTag(createdVenue.venueId,createdVenue.description +
                createdVenue.simpleDescription +
                createdVenue.facilityInfo +
                createdVenue.precautions +
                createdVenue.refundPolicy + ""
        )

        val resp = toResponseVenueInfo(createdVenue)
        return ResponseEntity.ok(resp.get())
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
            val resp = toResponseVenueInfo(updatedVenue.get())
            ResponseEntity.ok(resp.get())
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 좌표 범위 내 장소 검색
    @GetMapping("/locationSearch")
    fun getVenuesWithinDistance(@RequestParam latitude: Double,
                                @RequestParam longitude: Double,
                                @RequestParam distance: Double): ResponseEntity<List<SimpleVenue>> {
        val geometryFactory = GeometryFactory()
        val point: Point = geometryFactory.createPoint(Coordinate(longitude, latitude))  // 좌표는 (x, y) 순서로 사용
        point.srid = 4326
        val venues = venueInfoService.getVenuesWithinDistance(point, distance)

        return ResponseEntity.ok(venues)
    }

    //장소 유형으로 장소 목록 가져오기
    @GetMapping("/typeSearch")
    fun getVenueByVenueType(@RequestParam type : String): ResponseEntity<List<VenueInfoResponse>> {
        val list = venueInfoService.getVenuesByType(type)
        return if(!list.isPresent){
            ResponseEntity.notFound().build()
        }
        else{
            val ret : MutableList<VenueInfoResponse> = mutableListOf()
            for(element in list.get()){
                val t = toResponseVenueInfo(element)
                if(t.isPresent) ret.add(t.get())
            }
            if(ret.size > 0){
                ResponseEntity.ok(ret.toList())
            }
            else{
                ResponseEntity.notFound().build()
            }
        }
    }

    //지역명으로 장소 목록 가져오기
    @GetMapping("/regionNameSearch")
    fun getRandomVenue(@RequestBody regionName: regionName): ResponseEntity<List<VenueInfoResponse>> {
        val list = venueInfoService.getVenuesByAddressName(regionName.si)
        return if(list.isPresent) {
            val ret : MutableList<VenueInfoResponse> = mutableListOf()
            for(element in list.get()) {
                val t = toResponseVenueInfo(element)
                if(t.isPresent) ret.add(t.get())
            }
            if(ret.size > 0){
                ResponseEntity.ok(ret.toList())
            }
            else{
                ResponseEntity.notFound().build()
            }
        }
        else{
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/regionSearch")
    fun getRegionVenue(@RequestBody regionName: regionName): ResponseEntity<List<SimpleVenue>> {
        val list = venueInfoService.getVenueListByAddressName(regionName.si)
        return if(list.isPresent){
            ResponseEntity.ok(list.get())
        }
        else{
            ResponseEntity.notFound().build()
        }
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

    @GetMapping("/makeTag/{id}")
    fun makeVenueTagById(@PathVariable id: Int): ResponseEntity<List<String>> {
        val venueInfo = venueInfoService.getVenueById(id)
        if(venueInfo.isPresent){
            val tagList = makeVenueTag(id, venueInfo.get().description +
                    venueInfo.get().simpleDescription +
                    venueInfo.get().facilityInfo +
                    venueInfo.get().precautions +
                    venueInfo.get().refundPolicy + "")
            if(tagList.size > 0){
                return ResponseEntity.ok(tagList)
            }
        }
        return ResponseEntity.noContent().build()
    }

    fun makeVenueTag(venueId : Int, venueInfo: String): List<String> {
        if(venueInfoService.getVenueById(venueId).isPresent){
            //gpt 서비스로 토큰들 설명에 대한 토큰들 뽑아오기
            val tokens = gptService.getTokenToVenue(venueInfo)

            val tags : MutableList<String> = ArrayList()
            tokens?.Tokens?.forEach {
                if(it.Subject != "Strange" && it.Subject != "NULL"){
                    when(it.Subject){
                        "AdditionFee" -> {
                            rentalFeePolicyService.makeRentalFeeByToken(venueId, it)
                        }
                        "NearBy", "Policy", "Service", "Style", "Purpose"  -> {
                            tags.add(it.Token)
                        }
                    }
                }
            }

            //뽑아온 토큰들 벡터 만들어서 저장하기
            venueTagByDescriptionService.createVenueTags(venueId, tags)
            return tags.toList()
        }

        return listOf()
    }

    fun resetVenueTag(venueId : Int) {
        venueTagByDescriptionService.deleteVenueTag(venueId.toLong())
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

    fun toResponseVenueInfo(venueInfo: VenueInfo) : Optional<VenueInfoResponse> {
        return if(venueInfo.location != null) {
            Optional.of(
                VenueInfoResponse(
                    venueId = venueInfo.venueId,
                    ownerId = venueInfo.ownerId,
                    address = venueInfo.address,
                    rentalFee = venueInfo.rentalFee ?: 0.0,
                    capacity = venueInfo.capacity ?: 0,
                    area = venueInfo.area,
                    spaceType = venueInfo.spaceType + "",
                    longitude = venueInfo.location.x,
                    latitude = venueInfo.location.y,
                    name = venueInfo.name,
                    simpleDescription = venueInfo.simpleDescription,
                    description = venueInfo.description,
                    facilityInfo = venueInfo.facilityInfo,
                    precautions = venueInfo.precautions,
                    refundPolicy = venueInfo.refundPolicy,
                    websiteURL = venueInfo.websiteURL,
                    detailAddress = venueInfo.detailAddress,
                )
            )
        }
        else {
            Optional.empty()
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

data class regionName(
    val si : String
)

data class venueType(
    val type : String
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

//Location 데이터를 해석하지 못하는 문제를 해결하기 위한 DTO
data class VenueInfoResponse(
    val venueId : Int,
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

data class VenueFilter(
    val address: String?,
    val minRentalFee: Double?,
    val maxRentalFee: Double?,
    val minCapacity: Int?,
    val minArea: Int?,
    val maxArea: Int?,
    val spaceType: String?
)
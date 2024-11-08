package com.capstone.backend.Service

import com.capstone.backend.Entity.VenueInfo
import com.capstone.backend.Repository.SimpleVenue
import com.capstone.backend.Repository.VenueInfoRepository
import org.springframework.stereotype.Service
import java.util.*
import org.locationtech.jts.geom.Point


@Service
class VenueInfoService(private val venueInfoRepository: VenueInfoRepository) {

    // 모든 장소 정보 조회
    fun getAllVenues(): List<VenueInfo> {
        return venueInfoRepository.findAll()
    }

    // ID로 특정 장소 조회
    fun getVenueById(venueId: Int): Optional<VenueInfo> {
        return venueInfoRepository.findById(venueId)
    }

    // 새로운 장소 추가
    fun createVenue(venueInfo: VenueInfo): VenueInfo {
        return venueInfoRepository.save(venueInfo)
    }

    // 장소 정보 수정
    fun updateVenue(venueId: Int, updatedVenueInfo: VenueInfo): Optional<VenueInfo> {
        val existingVenue = venueInfoRepository.findById(venueId)
        return if (existingVenue.isPresent) {
            val venueToUpdate = existingVenue.get().copy(
                ownerId = updatedVenueInfo.ownerId,
                address = updatedVenueInfo.address,
                rentalFee = updatedVenueInfo.rentalFee,
                capacity = updatedVenueInfo.capacity,
                area = updatedVenueInfo.area,
                spaceType = updatedVenueInfo.spaceType,
                location = updatedVenueInfo.location,
                name = updatedVenueInfo.name,  // 추가된 필드
                simpleDescription = updatedVenueInfo.simpleDescription,  // 추가된 필드
                description = updatedVenueInfo.description,  // 추가된 필드
                facilityInfo = updatedVenueInfo.facilityInfo,  // 추가된 필드
                precautions = updatedVenueInfo.precautions,  // 추가된 필드
                refundPolicy = updatedVenueInfo.refundPolicy,  // 추가된 필드
                websiteURL = updatedVenueInfo.websiteURL,  // 추가된 필드
                detailAddress = updatedVenueInfo.detailAddress  // 추가된 필드
            )
            Optional.of(venueInfoRepository.save(venueToUpdate))
        } else {
            Optional.empty()
        }
    }

    // 좌표 범위 내 장소 검색
    fun getVenuesWithinDistance(point: Point, distance: Double): List<SimpleVenue> {
        return venueInfoRepository.findVenuesWithinDistance(point, distance)
    }

    //장소 유형으로 장소 검색
    fun getVenuesByType(type: String): Optional<List<VenueInfo>> {
        val list = venueInfoRepository.findVenueInfoBySpaceType(type)
        return if (!list.isPresent){
            Optional.empty()
        }
        else{
            list
        }
    }

    fun getVenuesByAddressName(addressName: String): Optional<List<VenueInfo>> {
        val list = venueInfoRepository.findVenueInfoByAddressName(addressName)
        return if(list.isPresent){
            list
        }
        else{
            Optional.empty()
        }
    }

    fun getVenueListByAddressName(addressName: String) : Optional<List<SimpleVenue>>{
        val list = venueInfoRepository.findSimpleVenueInfoByAddressName(addressName)
        return if(list.get().isNotEmpty()){
            list
        }
        else{
            Optional.empty()
        }
    }

    // 장소 삭제
    fun deleteVenue(venueId: Int) {
        venueInfoRepository.deleteById(venueId)
    }
}
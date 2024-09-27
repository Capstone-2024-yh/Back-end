package com.capstone.backend.Service

import com.capstone.backend.Entity.VenueInfo
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
    fun getVenueById(venueId: Long): Optional<VenueInfo> {
        return venueInfoRepository.findById(venueId)
    }

    // 새로운 장소 추가
    fun createVenue(venueInfo: VenueInfo): VenueInfo {
        return venueInfoRepository.save(venueInfo)
    }

    // 장소 정보 수정
    fun updateVenue(venueId: Long, updatedVenueInfo: VenueInfo): Optional<VenueInfo> {
        val existingVenue = venueInfoRepository.findById(venueId)
        return if (existingVenue.isPresent) {
            val venueToUpdate = existingVenue.get().copy(
                ownerId = updatedVenueInfo.ownerId,
                photo = updatedVenueInfo.photo,
                address = updatedVenueInfo.address,
                rentalFee = updatedVenueInfo.rentalFee,
                capacity = updatedVenueInfo.capacity,
                area = updatedVenueInfo.area,
                spaceType = updatedVenueInfo.spaceType,
                location = updatedVenueInfo.location
            )
            Optional.of(venueInfoRepository.save(venueToUpdate))
        } else {
            Optional.empty()
        }
    }

    // 좌표 범위 내 장소 검색
    fun getVenuesWithinDistance(point: Point, distance: Double): List<VenueInfo> {
        return venueInfoRepository.findVenuesWithinDistance(point, distance)
    }

    // 장소 삭제
    fun deleteVenue(venueId: Long) {
        venueInfoRepository.deleteById(venueId)
    }
}
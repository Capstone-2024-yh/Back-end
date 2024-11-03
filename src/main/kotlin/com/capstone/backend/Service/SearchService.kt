package com.capstone.backend.Service

import org.springframework.stereotype.Service

@Service
class SearchService() {

//    fun searchVenues(searchRequest: SearchRequest): List<VenueInfoResponse> {
//        val results = searchVenueRepository.findByCriteria(
//            keyword = searchRequest.keyword,
//            location = searchRequest.location,
//            minCapacity = searchRequest.minCapacity,
//            maxCapacity = searchRequest.maxCapacity,
//            minFee = searchRequest.minFee,
//            maxFee = searchRequest.maxFee,
//            spaceType = searchRequest.spaceType
//        )
//        return results.map { VenueInfoResponse.from(it) }
//    }


}

data class SearchRequest(
    val uid: Int,
    val keyword: String,
    val location: String?,
    val minCapacity: Int?,
    val maxCapacity: Int?,
    val minFee: Double?,
    val maxFee: Double?,
    val spaceType: String?
)
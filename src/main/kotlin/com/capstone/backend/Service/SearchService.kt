package com.capstone.backend.Service

import com.capstone.backend.Controller.VenueInfoResponse
import com.capstone.backend.Repository.SearchVenueRepository
import org.springframework.stereotype.Service
import java.rmi.server.UID

@Service
class SearchService(
    private val searchVenueRepository: SearchVenueRepository
) {

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
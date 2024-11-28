package com.capstone.backend.controllers

import com.capstone.backend.Service.SearchTokenService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search-token")
class SearchTokenController(
    private val searchTokenService: SearchTokenService
) {

    @GetMapping("/cluster")
    fun clusterSearchTokens(): List<Triple<Int, Int, String>> {
        // DBSCAN 군집화 수행 후 결과 반환
        return searchTokenService.performDBSCANClustering()
    }
}

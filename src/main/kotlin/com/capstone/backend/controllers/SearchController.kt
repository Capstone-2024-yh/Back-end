package com.capstone.backend.controllers

import com.capstone.backend.Controller.VenueInfoResponse
import com.capstone.backend.Service.GptService
import com.capstone.backend.Service.SearchRequest
import com.capstone.backend.Service.SearchService
import com.capstone.backend.Service.Word2VectorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService,
    private val word2VectorService: Word2VectorService,
    private val gptService: GptService
) {

    @PostMapping
    fun searchVenues(@RequestBody searchRequest: SearchRequest): ResponseEntity<List<VenueInfoResponse>> {

//        여기도 searchRequest.keyweord 에서 gpt로 토끈 뽑아 오는거 작업 해주세요

//        gpt로 태그 뽑아 오면 서치 토큰에 저장 하는 로직 넣아야 함
//        val tags = word2VectorService.getWord2Vector()
//        val searchResults = searchService.searchVenues(searchRequest)
//        return ResponseEntity.ok(searchResults)
        return ResponseEntity.ok(emptyList())
    }
}




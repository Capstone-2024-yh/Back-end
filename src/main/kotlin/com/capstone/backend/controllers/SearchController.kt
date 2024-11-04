package com.capstone.backend.controllers

import com.capstone.backend.Controller.VenueInfoResponse
import com.capstone.backend.Entity.SearchToken
import com.capstone.backend.Service.*
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService,
    private val searchTokenService: SearchTokenService,
    private val gptService: GptService
) {

    @PostMapping("/searchKeyword")
    fun searchVenues(@RequestBody searchRequest: SearchRequest): ResponseEntity<List<VenueInfoResponse>> {

//        gpt로 태그 뽑아 오면 서치 토큰에 저장 하는 로직 넣아야 함
        val tokens = gptService.getToken(searchRequest.keyword)
        val str : MutableList<String> = ArrayList()
        tokens?.Tokens?.forEach {
            if(it.Subject != "Strange" && it.Subject != "NULL"){
                str.add(it.Token)
            }
        }

        // 검색에 활용된 토큰들을 저장 함
        val searchTokens = searchTokenService.saveSearchTokens(searchRequest.uid, str)

//        val searchResults = searchService.searchVenues(searchRequest)
//        return ResponseEntity.ok(searchResults)
        return ResponseEntity.ok(emptyList())
    }
}




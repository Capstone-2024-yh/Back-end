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
    private val word2VectorService: Word2VectorService,
    private val gptService: GptService
) {

    @PostMapping("/searchKeyword")
    fun searchVenues(@RequestBody searchRequest: SearchRequest): ResponseEntity<List<VenueInfoResponse>> {

//        여기도 searchRequest.keyweord 에서 gpt로 토큰 뽑아 오는거 작업 해주세요

//        gpt로 태그 뽑아 오면 서치 토큰에 저장 하는 로직 넣아야 함
        if(searchRequest.keyword.trim() != ""){
            val tokens = gptService.getToken(searchRequest.keyword)
            val str : MutableList<String> = ArrayList()

            if(tokens != null){
                for(token in tokens.Tokens){
                    if(token.Subject != "Strange" && token.Subject != "NULL"){
                        str.add(token.Token)
                    }
                }
            }

            val tags = runBlocking {
                word2VectorService.getWord2Vector(str.toList());
            }
            for (tag in tags){
                searchTokenService.saveSearchToken(
                    SearchToken(
                        token = tag.key,
                        vector = tag.value
                    )
                )
            }
        }

//        val searchResults = searchService.searchVenues(searchRequest)
//        return ResponseEntity.ok(searchResults)
        return ResponseEntity.ok(emptyList())
    }
}




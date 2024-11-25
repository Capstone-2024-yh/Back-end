package com.capstone.backend.controllers

import com.capstone.backend.Entity.SearchRecord
import com.capstone.backend.Service.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService,
    private val searchTokenService: SearchTokenService,
    private val searchRecordService: SearchRecordService,
    private val gptService: GptService,
    private val rentalFeePolicyService: RentalFeePolicyService
) {
    val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm[:ss]")
    .toFormatter()

    @PostMapping("/searchKeyword")
    fun searchVenues(@RequestBody searchRequest: SearchRequest): ResponseEntity<List<VenueScoreResponse>> {
        searchRecordService.addRecord(SearchRecord(
            userId = 0,
            searchString = searchRequest.keyword
        ))

        //필터를 위한 값
        val tokens = gptService.getTokenToSearch(searchRequest.keyword)

        //토큰 리스트
        val tokenList : MutableList<String> = ArrayList()

        val filter : Filter = Filter()
        filter.Capacity = searchRequest.maxCapacity?.toDouble() ?: 0.0
        searchRequest.location?.let { filter.Address.add(it) }
        filter.Fee = searchRequest.maxFee?: 0.0

        tokens?.Tokens?.forEach { it ->
            if(it.Subject != "Strange" && it.Subject != "NULL"){
                when(it.Subject){
                    "Address" -> { //필터에 주소 항목 추가
                        filter.Address.add(it.Token)
                    }
                    "NearBy" -> {
                        // 검색어 기반 좌표 검색을 한다면 Kakao Api 호출 필요
                        tokenList.add(it.Token)
                    }
                    "Equipment" -> { //필터에 기자재 목록 추가
                        tokenList.add(it.Token)
                        if(it.Token[0] == 'O'){
                            filter.EquipmentList.add(it.Token)
                        }
                    }
                    "Date" -> { //예약 기간 확인을 위한 일정 시작, 끝 필터
                        try{
                            val time = LocalDateTime.parse(it.Token, formatter)

                            if(filter.Date.first == LocalDateTime.MIN){
                                filter.Date = Pair(time, time)
                            }
                            else if(filter.Date.first > time){
                                filter.Date = Pair(time, filter.Date.second)
                            }
                            else if(filter.Date.second < time){
                                filter.Date = Pair(filter.Date.first, time)
                            }
                        }
                        catch(e: Exception){
                            println(e.message)
                        }
                    }
                    "Capacity" -> { //수용 인원 필터 추가
                        val value : Double = it.Token.substring(2, it.Token.length).toDouble()
                        filter.Capacity = if(filter.Capacity < value) value else filter.Capacity
                    }
                    "Area" -> { //면적 필터 추가
                        val value : Double = it.Token.substring(2, it.Token.length).toDouble()
                        filter.Area = if(filter.Area < value) value else filter.Area
                    }
                    "SpaceType" -> { //장소 종류 리스트에 추가
                        tokenList.add(it.Token)
                        if(it.Token[0] == 'O') {
                            filter.SpaceType.add(it.Token.substring(2, it.Token.length))
                        }
                    }
                    "Policy", "Service", "Purpose" -> { //서치 토큰만 추가
                        tokenList.add(it.Token)
                    }
                    "Feedback" -> { //응답에서 Feedback 항목만 분리해서 전송
                    }
                }
            }
        }

        // 검색에 활용된 토큰들을 저장 함
        val searchTokens = searchTokenService.saveSearchTokens(searchRequest.uid, tokenList)
        val vectors = searchTokens.map { it.vector }

        val searchResults = searchService.findTopVenuesBySimilarity(vectors)
        return ResponseEntity.ok(searchResults)
    }
}

data class Filter(
    var Address : MutableList<String> = mutableListOf(), //주소 기반
    var Date : Pair<LocalDateTime, LocalDateTime> = Pair(LocalDateTime.MIN, LocalDateTime.MIN), //시작 날짜, 끝 날짜
    var SpaceType : MutableList<String> = mutableListOf(), //장소 종류
    var Capacity : Double = 0.0, //수용 인원
    var Area : Double = 0.0, //장소 크기
    var Fee : Double = 0.0, //대관 비용
    var EquipmentList : MutableList<String> = mutableListOf(), //기자재 목록
)
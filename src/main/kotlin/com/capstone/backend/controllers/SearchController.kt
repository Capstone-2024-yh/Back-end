package com.capstone.backend.controllers

import com.capstone.backend.Entity.SearchRecord
import com.capstone.backend.Service.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
    private val venueInfoService: VenueInfoService,
    private val rentalFeePolicyService: RentalFeePolicyService
) {
    val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd HH:mm[:ss]")
    .toFormatter()

    @PostMapping("/searchKeyword")
    suspend fun searchVenues(@RequestBody searchRequest: SearchRequest): Map<String, List<Any>>  {
        searchRecordService.addRecord(SearchRecord(
            userId = 0,
            searchString = searchRequest.keyword
        ))

        val tokens = gptService.getTokenToSearch(searchRequest.keyword)

        val filter : Filter = Filter()

        filter.Capacity = searchRequest.maxCapacity?.toDouble() ?: 0.0
        searchRequest.location?.let { filter.Address.add(it) }
        filter.Fee = searchRequest.maxFee?: 0.0

        val tokenList = makeSearchToken(tokens, filter)

        // 검색에 활용된 토큰들을 저장 함
        val searchTokens = withContext(Dispatchers.IO) {
            searchTokenService.saveSearchTokens(searchRequest.uid, tokenList.first)
        }
        val vectors = searchTokens.map { it.vector }

        val searchResults = searchService.findTopVenuesBySimilarity(vectors)

        val map :Map<String, List<Any>> = mapOf(
            "searchResults" to searchResults,
            "feedback" to tokenList.second
        )
        return map
    }

//    @PostMapping("/searchAssist")
//    suspend fun searchAssist(@RequestBody searchRequest: SearchRequest) : ResponseEntity<FeedbackDataDTO>{
//        val tokens = gptService.getTokenToSearch(searchRequest.keyword)
//
//        val filter : Filter = Filter()
//
//        filter.Capacity = searchRequest.maxCapacity?.toDouble() ?: 0.0
//        searchRequest.location?.let { filter.Address.add(it) }
//        filter.Fee = searchRequest.maxFee?: 0.0
//
//        val tokenList = makeSearchToken(tokens, filter)
//
//        // 검색에 활용된 토큰들을 저장 함
//        val searchTokens = withContext(Dispatchers.IO) {
//            searchTokenService.saveSearchTokens(searchRequest.uid, tokenList)
//        }
//        val vectors = searchTokens.map { it.vector }
//
//        val searchResults = searchService.findTopVenuesBySimilarity(vectors)
//
//        return if(tokens != null && searchResults.isNotEmpty()){
//            val response = makeResponse(tokens, searchResults.map { it.venueId }, filter)
//            ResponseEntity.ok(response)
//        }
//        else {
//            ResponseEntity.noContent().build()
//        }
//    }

    @PostMapping("/getResponse/{id}")
    suspend fun getVenueFeedback(@PathVariable id : Int, @RequestBody tokens: TokenListDTO) : ResponseEntity<VenueResDTO> {
        val venueOption = venueInfoService.getVenueById(id)
        if (venueOption.isPresent) {
            val venue = venueOption.get()
            if (venue.name != null && venue.description != null && venue.precautions != null) {
                val response = makeResponse(tokens, listOf(venue.venueId), null)
                if(response != null && response.venueInfo.size > 0){
                    return ResponseEntity.ok(response.venueInfo.get(0))
                }
            }
        }
        return ResponseEntity.notFound().build()
    }
    
    private fun makeSearchToken(tokens : TokenListDTO?, filter : Filter) : Pair<List<String>,List<String>> {
        //토큰 리스트
        val tokenList : MutableList<String> = ArrayList()
        val feedbackList : MutableList<String> = ArrayList()

        if(tokens != null) {
            tokens.Tokens.forEach { it ->
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
                                } else if(filter.Date.first > time){
                                    filter.Date = Pair(time, filter.Date.second)
                                } else if(filter.Date.second < time){
                                    filter.Date = Pair(filter.Date.first, time)
                                }
                            } catch(e: Exception){
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
                            feedbackList.add(it.Summary)
                        }
                    }
                }
            }
        }

        return Pair(tokenList, feedbackList)
    }

    fun makeResponse(tokenList : TokenListDTO, venueIdList: List<Int>, filter: Filter?) : FeedbackDataDTO? {
        val venueList = venueInfoService.getVenuesById(venueIdList)
        val venueInfoList = venueList
            .filter { it.name != null && it.description != null && it.facilityInfo != null }
            .map {
                VenueInfoDTO(
                    it.venueId,
                    it.name!!,
                    it.description!!,
                    listOf(),
                    it.facilityInfo!!
                )
            }

        val feedback = gptService.getFeedback(FeedbackDTO(
            tokenList.Tokens,
            venueInfoList,
            listOf()
        ))

        val venueRes = feedback?.Messages
            ?.filter { message ->
                val venue = venueList.find { it.venueId == message.venueId }
                venue?.name != null && venue?.simpleDescription != null
            }
            ?.map { message ->
                val venue = venueList.find { it.venueId == message.venueId }!!
                VenueResDTO(
                    venue.venueId,
                    venue.name!!,
                    venue.address + " " + venue.detailAddress,
                    rentalFeePolicyService.getRentalFeeFromFilter(venue.venueId, filter).toInt(),
                    venue.simpleDescription!!,
                    message.caution,
                    message.reason
                )
            }

        val message = venueRes?.let {
            FeedbackDataDTO(
                feedback = listOf(),
                venueIdList = venueIdList,
                venueInfo = it
            )
        }

        return message
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


data class FeedbackDataDTO(
    val feedback: List<String>,
    val venueIdList: List<Int>,
    val venueInfo: List<VenueResDTO>
)

data class VenueResDTO(
    val id: Int,
    val name: String,
    val location: String,
    val amount: Int,
    val simpleDesc: String,
    val caution: List<String>,
    val recommand: List<String>
)
package com.capstone.backend.controllers

import com.capstone.backend.Service.GptService
import com.capstone.backend.Service.TokenListDTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/gptCall")
class GptController(
    private val gptService: GptService
) {
    @PostMapping("/Call")
    fun callGPT(
        @RequestBody question: Question
    ): ResponseEntity<String> {
        val respon = gptService.getResponse(question.question)
        return ResponseEntity.ok(respon)
    }

    @PostMapping("/MakeToken")
    fun makeToken(@RequestBody question: Question): ResponseEntity<TokenListDTO> {
        val response = gptService.getTokenToSearch(question.question)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/MakeToken/Search")
    fun makeTokenSearch(@RequestBody question: Question): ResponseEntity<TokenListDTO> {
        val response = gptService.getTokenToSearch(question.question)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/MakeToken/Venue")
    fun makeTokenVenue(@RequestBody question: Question): ResponseEntity<TokenListDTO> {
        val response = gptService.getTokenToVenue(question.question)
        return ResponseEntity.ok(response)
    }
}

data class Question(
    val question: String
)
package com.capstone.backend.controllers

import com.capstone.backend.Service.GptService
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
}

data class Question(
    val question: String
)
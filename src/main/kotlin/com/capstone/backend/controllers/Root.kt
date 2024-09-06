package com.capstone.backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Root {
    @GetMapping("/")
    fun root(): String {
        return "Hello, World!"
    }
}
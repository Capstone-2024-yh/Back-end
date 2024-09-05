package com.capstone.backend.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class root {
    @GetMapping("/")
    fun root(): String {
        return "Hello, World!"
    }
}
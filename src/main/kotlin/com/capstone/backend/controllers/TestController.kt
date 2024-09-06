package com.capstone.backend.controllers

import com.capstone.backend.Entity.ExampleTable
import com.capstone.backend.Service.ExampleTableService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExampleTableController(private val exampleTableService: ExampleTableService) {

    @GetMapping("/example")
    fun getAll(): List<ExampleTable> {
        return exampleTableService.getAll()
    }
}
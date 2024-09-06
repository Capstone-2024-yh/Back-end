package com.capstone.backend.Service

import com.capstone.backend.Entity.ExampleTable
import com.capstone.backend.Repository.ExampleTableRepository
import org.springframework.stereotype.Service

@Service
class ExampleTableService(private val exampleTableRepository: ExampleTableRepository) {

    fun getAll(): List<ExampleTable> {
        return exampleTableRepository.findAll()
    }

    fun save(text: String): ExampleTable {
        return exampleTableRepository.save(ExampleTable(text = text))
    }
}
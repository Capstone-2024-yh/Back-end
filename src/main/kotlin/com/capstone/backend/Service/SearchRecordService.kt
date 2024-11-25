package com.capstone.backend.Service

import com.capstone.backend.Entity.SearchRecord
import com.capstone.backend.Repository.SearchRecordRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchRecordService(
    private val searchRecordRepository: SearchRecordRepository
) {
    fun addRecord(record: SearchRecord) {
        //searchRecordRepository.save(record)
    }

    fun getSearchRecords(): List<SearchRecord> {
        return searchRecordRepository.findAll()
    }

    fun getSearchRecordById(id: Int): Optional<SearchRecord> {
        return searchRecordRepository.findById(id)
    }
}
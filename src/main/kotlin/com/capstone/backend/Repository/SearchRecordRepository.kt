package com.capstone.backend.Repository

import com.capstone.backend.Entity.SearchRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SearchRecordRepository : JpaRepository<SearchRecord, Int> {

}
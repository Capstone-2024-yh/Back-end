package com.capstone.backend.Repository

import com.capstone.backend.Entity.ExampleTable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExampleTableRepository : JpaRepository<ExampleTable, Long> {
    // 추가적인 쿼리 메서드가 필요하다면 여기에 선언할 수 있습니다.
}
package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenueInfo
import org.springframework.data.jpa.repository.JpaRepository

interface VenueInfoRepository : JpaRepository<VenueInfo, Long> {
    // 추가적인 쿼리 메소드 정의 가능
}

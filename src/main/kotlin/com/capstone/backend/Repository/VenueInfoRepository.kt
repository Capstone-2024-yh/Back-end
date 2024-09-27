package com.capstone.backend.Repository

import com.capstone.backend.Entity.VenueInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.jpa.repository.Query
import org.locationtech.jts.geom.Point

interface VenueInfoRepository : JpaRepository<VenueInfo, Long> {

    // 좌표 범위 내 장소 검색 (반경 내에 있는 장소를 검색)
    @Query(value = """
        SELECT * FROM venue_info v 
        WHERE ST_DWithin(v.location, :point, :distance)
    """, nativeQuery = true)
    fun findVenuesWithinDistance(
        @Param("point") point: Point,
        @Param("distance") distance: Double
    ): List<VenueInfo>

}
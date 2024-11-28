package com.capstone.backend.Repository

import com.capstone.backend.Entity.ClusterSummary
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ClusterSummaryRepository : JpaRepository<ClusterSummary, Int> {

    // 최신 날짜에 해당하는 모든 ClusterSummary 가져오기
    @Query("SELECT c FROM ClusterSummary c WHERE c.createdAt = :latestDate")
    fun findAllByLatestCreatedAt(@Param("latestDate") latestDate: LocalDateTime): List<ClusterSummary>

    // 가장 최신 날짜 찾기
    @Query("SELECT MAX(c.createdAt) FROM ClusterSummary c")
    fun findLatestCreatedAt(): LocalDateTime?


    @Query("""
        SELECT * 
        FROM cluster_summary cs
        WHERE cs.created_at = (
            SELECT MAX(cs2.created_at) FROM cluster_summary cs2
        )
        ORDER BY cs.cluster_size DESC
        LIMIT 10
    """, nativeQuery = true)
    fun findLatestClustersOrderedBySize(): List<ClusterSummary>

}

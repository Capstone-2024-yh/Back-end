package com.capstone.backend.controllers

import com.capstone.backend.Entity.ClusterSummary
import com.capstone.backend.Service.ClusterSummaryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search-summary")
class ClusterSummaryController(
    private val clusterSummaryService: ClusterSummaryService
) {

    @GetMapping("/cluster")
    fun clusterAndSaveSummaries(): String {
        clusterSummaryService.performAndSaveClustering()
        return "Clustering completed and summaries saved."
    }

    @GetMapping("/latest")
    fun getLatestClusterSummaries(): List<ClusterSummary> {
        return clusterSummaryService.getLatestClusterSummaries()
    }
}
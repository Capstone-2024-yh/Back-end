package com.capstone.backend.Repository

import com.capstone.backend.Entity.Equipment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EquipmentRepository : JpaRepository<Equipment, Long> {

    // venueId로 Equipment 목록을 검색하는 메서드
    fun findByVenueId(venueId: Int): List<Equipment>

    // venueId와 EquipmentId로 목록을 검색하는 메서드
    fun findByVenueIdAndEquipmentId(venueId: Int, equipmentId: Int) : Equipment?
}

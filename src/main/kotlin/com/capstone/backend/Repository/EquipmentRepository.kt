package com.capstone.backend.Repository

import com.capstone.backend.Entity.Equipment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EquipmentRepository : JpaRepository<Equipment, Long> {

    // venueId로 Equipment 목록을 검색하는 메서드
    fun findByVenueId(venueId: Int): List<Equipment>

    // venueId와 EquipmentId로 목록을 검색하는 메서드
    fun findByVenueIdAndEquipmentId(venueId: Int, equipmentId: Int) : List<Equipment>?
    
    // 특정 venueId의 정보를 저장한 목록을 모두 삭제
    fun deleteByVenueId(venueId: Int)

    fun deleteByVenueIdAndEquipmentId(equipmentId: Int, venueId: Int)

    @Modifying
    @Query("""
        DELETE FROM equipment WHERE venue_id = :venueId AND equipment_type_id IN :equipmentTypeIds
    """, nativeQuery = true)
    fun deleteByVenueIdAndEquipmentTypeIds(@Param("venueId") venueId: Int, @Param("equipmentTypeIds") equipmentTypeIds: List<Int>)
}

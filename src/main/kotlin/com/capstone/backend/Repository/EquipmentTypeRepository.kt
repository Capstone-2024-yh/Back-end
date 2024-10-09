package com.capstone.backend.Repository

import com.capstone.backend.Entity.EquipmentType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface EquipmentTypeRepository : JpaRepository<EquipmentType, Int>{
    //모든 EquipmentType 데이터를 String 형태로 전달
    @Query("select t.equipment from EquipmentType t")
    fun getAllEquipmentTypeList(): List<String>

    fun findByEquipment(equipment: String): List<EquipmentType>

    fun getIdByEquipment(equipment: String): Int
}
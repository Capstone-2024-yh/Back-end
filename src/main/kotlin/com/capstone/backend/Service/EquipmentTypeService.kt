package com.capstone.backend.Service

import com.capstone.backend.Entity.EquipmentType
import com.capstone.backend.Repository.EquipmentTypeRepository
import org.springframework.stereotype.Service

@Service
class EquipmentTypeService(private val equipmentTypeRepository: EquipmentTypeRepository) {
    fun addEquipmentType(equipmentType: String) {
        val type = equipmentTypeRepository.findByEquipment(equipmentType)
        if(type == null){
            val equipType = EquipmentType(
                equipment = equipmentType
            )
            equipmentTypeRepository.save(equipType)
        }
    }
}
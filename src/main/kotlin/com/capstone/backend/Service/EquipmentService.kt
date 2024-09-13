package com.capstone.backend.Service

import com.capstone.backend.Entity.Equipment
import com.capstone.backend.Repository.EquipmentRepository
import org.springframework.stereotype.Service

@Service
class EquipmentService(private val equipmentRepository: EquipmentRepository) {

    fun getEquipmentByVenueId(venueId: Int): List<Equipment> {
        return equipmentRepository.findByVenueId(venueId)
    }

    fun getEquipmentByType(equipment: String): List<Equipment> {
        return equipmentRepository.findByEquipment(equipment)
    }
}

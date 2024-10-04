package com.capstone.backend.Service

import com.capstone.backend.Entity.Equipment
import com.capstone.backend.Repository.EquipmentRepository
import com.capstone.backend.Repository.EquipmentTypeRepository
import org.springframework.stereotype.Service

@Service
class EquipmentService(
    private val equipmentRepository: EquipmentRepository,
    private val equipmentTypeRepository: EquipmentTypeRepository
) {

    fun getEquipmentByVenueId(venueId: Int): List<Equipment> {
        return equipmentRepository.findByVenueId(venueId)
    }

    fun addEquipment(venueId: Int, equipmentId: Int) {
        val e = Equipment(
            venueId = venueId,
            equipmentId = equipmentId,
            hasEquipment = 1
        )
        equipmentRepository.save(e)
    }

    fun addEquipments(venueId: Int, equipments: List<Equipment>) {
        for(equipment in equipments) {
            val e = Equipment(
                venueId = venueId,
                equipmentId = equipment.equipmentId,
                hasEquipment = 1
            )
            equipmentRepository.save(e)
        }
    }

    fun addEquipmentsByString(venueId: Int, equipments: List<String>) {
        if(equipments.isEmpty() || equipmentRepository.findByVenueId(venueId).size == 0) return
        for(equipment in equipments) {
            val e = Equipment(
                venueId = venueId,
                equipmentId = equipmentTypeRepository.findByEquipment(equipment).id,
                hasEquipment = 1
            )
            equipmentRepository.save(e)
        }
    }

    fun updateEquipments(venueId: Int, equipments: List<String>) {
        for(equipment in equipments) {
            val id = equipmentTypeRepository.findByEquipment(equipment).id
            val e = equipmentRepository.findByVenueIdAndEquipmentId(venueId, id)
            if(e == null){
                val equip = Equipment(
                    venueId = venueId,
                    equipmentId = id,
                    hasEquipment = 1
                )
                equipmentRepository.save(equip)
            }
        }
    }

    fun getAllEquipmentList() : List<String>{
        return equipmentTypeRepository.getAllEquipmentTypeList()
    }
}

package com.capstone.backend.Service

import com.capstone.backend.Entity.Equipment
import com.capstone.backend.Repository.EquipmentRepository
import com.capstone.backend.Repository.EquipmentTypeRepository
import org.springframework.stereotype.Service

@Service
class EquipmentService(
    private val equipmentRepository: EquipmentRepository,
    private val equipmentTypeRepository: EquipmentTypeRepository,
    private val equipmentTypeService: EquipmentTypeService
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
                hasEquipment = equipment.hasEquipment
            )
            equipmentRepository.save(e)
        }
    }

    fun addEquipmentsByString(venueId: Int, equipments: List<String>) {
        if(equipments.isEmpty()) return
        for(equipment in equipments) {
            val equip = Equipment(
                venueId = venueId,
                equipmentId = equipmentTypeRepository.getIdByEquipment(equipment),
                hasEquipment = 1
            )
            equipmentRepository.save(equip)
        }
    }

    fun updateEquipments(venueId: Int, equipmentTypeIds: List<Int>) {
        // 입력받은 equipment_type_id 리스트가 비어 있으면 작업을 하지 않음
        if (equipmentTypeIds.isEmpty()) return

        // DB에서 해당 venueId의 모든 equipment_type_id를 가져옴
        val existingEquipments = equipmentRepository.findByVenueId(venueId)

        // DB에 있는 equipment_type_id 목록
        val existingEquipmentTypeIds = existingEquipments.map { it.equipmentId }

        // 입력받은 equipment_type_id 리스트 중 DB에 없는 항목 추가
        val equipmentToAdd = equipmentTypeIds.filterNot { existingEquipmentTypeIds.contains(it) }
        if (equipmentToAdd.isNotEmpty()) {
            for(equipmentId in equipmentToAdd) {
                equipmentRepository.save(Equipment(
                    venueId =  venueId,
                    equipmentId = equipmentId,
                    hasEquipment = 1
                ))
            }
        }

        // 입력받은 equipment_type_id 리스트에 없는 DB 항목 삭제
        val equipmentToRemove = existingEquipmentTypeIds.filterNot { equipmentTypeIds.contains(it) }
        if (equipmentToRemove.isNotEmpty()) {
            equipmentRepository.deleteByVenueIdAndEquipmentTypeIds(venueId, equipmentToRemove)
        }
    }

    fun deleteEquipment(venueId: Int, equipmentId: Int) {
        val equip = equipmentRepository.findByVenueIdAndEquipmentId(venueId, equipmentId)
        if (equip != null) {
            for(e in equip){
                equipmentRepository.delete(e)
            }
        }
    }

    fun deleteEquipments(venueId: Int, equipments: List<String>) {
        for(equipment in equipments) {
            equipmentRepository.deleteByVenueIdAndEquipmentId(venueId,
                equipmentTypeRepository.getIdByEquipment(equipment))
        }
    }

    fun deleteEquipmentByVenueId(venueId: Int) {
        equipmentRepository.deleteByVenueId(venueId)
    }

    fun getAllEquipmentList() : List<String>{
        return equipmentTypeRepository.getAllEquipmentTypeList()
    }
}

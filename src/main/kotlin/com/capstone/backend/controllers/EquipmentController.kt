package com.capstone.backend.controllers

import com.capstone.backend.Entity.Equipment
import com.capstone.backend.Service.EquipmentService
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/equipment")
class EquipmentController(
    private val equipmentService: EquipmentService
) {
    @GetMapping("/AllType")
    fun getEquipmentTypeList() : ResponseEntity<List<String>> {
        return ResponseEntity.ok(equipmentService.getAllEquipmentList())
    }

    @GetMapping("/{venueId}")
    fun getEquipmentList(@PathVariable("venueId") venueId : Int) : ResponseEntity<List<Equipment>> {
        val list = equipmentService.getEquipmentByVenueId(venueId)
        return ResponseEntity.ok(list)
    }

    @PostMapping("/create")
    fun createEquipment(@RequestBody equipInfo : EquipmentsDTO) : ResponseEntity<List<Equipment>> {
        val list : ArrayList<Equipment> = arrayListOf<Equipment>()
        for(equip in equipInfo.equipments){
            val e = equipmentService.addEquipment(
                venueId = equipInfo.venueId,
                equipmentId = equip
            )
            list.add(e)
        }
        return ResponseEntity.ok(list)
    }

    @Transactional
    @PutMapping("/{venueId}")
    fun editEquipmentInfo(@PathVariable venueId : Int, @RequestBody equipInfo : EquipmentsDTO) : ResponseEntity<List<Equipment>> {
        val equips = equipmentService.updateEquipments(venueId, equipInfo.equipments)
        if(equips.isPresent){
            return ResponseEntity.ok(equips.get())
        }
        return ResponseEntity.notFound().build()
    }

    @Transactional
    @DeleteMapping("/{venueId}")
    fun deleteEquipmentByVenueId(@PathVariable("venueId") venueId: Int) : ResponseEntity<Void> {
        equipmentService.deleteEquipmentByVenueId(venueId)
        return ResponseEntity.noContent().build()
    }
}

data class EquipmentsDTO(
    val venueId : Int,
    val equipments : List<Int>
)
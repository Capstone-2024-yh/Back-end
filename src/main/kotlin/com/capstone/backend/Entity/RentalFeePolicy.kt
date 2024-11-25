package com.capstone.backend.Entity

import jakarta.persistence.*

@Entity
@Table(name = "rentalfeepolicy")
data class RentalFeePolicy (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Int = 0,

    @Column(name = "policy_type", nullable = false)
    val policyType : Int,

    @Column(name = "amount", nullable = false)
    val amount : Int,

    @Column(name = "description", nullable = false)
    val description : String,

    @Column(name = "expression", nullable = false)
    val expression : String,

    @Column(name = "venue_id", nullable = false)
    val venueId : Int,

    @Column(name = "timedependent", nullable = false)
    val timeDependent : Boolean
)
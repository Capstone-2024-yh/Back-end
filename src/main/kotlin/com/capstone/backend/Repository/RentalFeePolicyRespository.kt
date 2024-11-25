package com.capstone.backend.Repository

import com.capstone.backend.Entity.RentalFeePolicy
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RentalFeePolicyRespository : JpaRepository<RentalFeePolicy, Int> {
    fun getRentalFeePoliciesByVenueId(venueId: Int): List<RentalFeePolicy>
}
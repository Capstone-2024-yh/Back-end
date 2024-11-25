package com.capstone.backend.Service

import com.capstone.backend.Entity.RentalFeePolicy
import com.capstone.backend.Repository.RentalFeePolicyRespository
import com.capstone.backend.controllers.Filter
import org.springframework.stereotype.Service
import java.util.*

@Service
class RentalFeePolicyService(
    private val rentalFeePolicyRespository: RentalFeePolicyRespository
) {
    fun addDefaultFee(venueId: Int, defaultFee : Double) : Optional<RentalFeePolicy> {
        val fee = rentalFeePolicyRespository.save(
            RentalFeePolicy(
                venueId = venueId,
                amount = defaultFee.toInt(),
                policyType = FeeType.default.ordinal,
                description = "기본 비용",
                expression = "Default",
                timeDependent = true
            )
        )
        return Optional.of(fee)
    }

    fun getRentalFeeFromFilter(venueId : Int, filter : Filter) : Double {
        val list = rentalFeePolicyRespository.getRentalFeePoliciesByVenueId(venueId)

        val timeDepList = mutableListOf<RentalFeePolicy>()
        val nonTimeDepList = mutableListOf<RentalFeePolicy>()

        var capacity : RentalFeePolicy? = null
        var secue : Double = 0.0

        list.map {
            when(it.policyType){
                FeeType.default.ordinal -> {
                    if(it.timeDependent){
                        timeDepList.add(it)
                    }
                    else{
                        nonTimeDepList.add(it)
                    }
                }
                FeeType.security.ordinal -> {
                    secue += it.amount
                }
                FeeType.weekend.ordinal -> {
                    if(filter.Date.first.dayOfWeek.value >= 6 || filter.Date.second.dayOfWeek.value >= 6
                        || filter.Date.first.dayOfWeek.value > filter.Date.second.dayOfWeek.value) {
                        if(it.timeDependent){
                            timeDepList.add(it)
                        }
                        else{
                            nonTimeDepList.add(it)
                        }
                    }
                    else { }
                }
                FeeType.capacity.ordinal -> {
                    if (filter.Capacity < it.expression.toDouble()
                        && (capacity?.expression?.toDouble() ?: 0.0) < it.expression.toDouble()){
                        capacity = it
                    }
                    else { }
                }
                FeeType.service.ordinal -> {
                    //서비스끼리의 비교가 가능할 경우,
                }
                else -> {}
            }
        }

        val tValue = timeDepList.sumOf { it.amount } * 9
        val ntValue = nonTimeDepList.sumOf { it.amount }

        return if(tValue < ntValue) {
            ntValue + ((capacity?.amount ?: 0) + secue)
        }
        else {
            tValue + ((capacity?.amount ?: 0) + secue)
        }
    }

    fun makeRentalFeeByToken(venueId: Int, token : TokenDTO) : Optional<RentalFeePolicy> {
        if(token.Subject == "AdditionFee"){
            val expression = token.Token.split(':')
            val policy = when(expression[0]){
                "security", "service" -> {
                    rentalFeePolicyRespository.save(RentalFeePolicy(
                        policyType = if(expression[0] == "security") FeeType.security.ordinal else FeeType.service.ordinal,
                        amount = expression[1].toInt(),
                        description = token.Require,
                        expression = token.Token,
                        venueId = venueId,
                        timeDependent = false
                    ))
                }
                "weekend" -> {
                    rentalFeePolicyRespository.save(RentalFeePolicy(
                        policyType = FeeType.security.ordinal,
                        amount = expression[1].toInt(),
                        description = token.Require,
                        expression = token.Token,
                        venueId = venueId,
                        timeDependent = true
                    ))
                }
                "capacity" -> {
                    rentalFeePolicyRespository.save(RentalFeePolicy(
                        policyType = FeeType.capacity.ordinal,
                        amount = expression[2].toInt(),
                        description = token.Require,
                        expression = expression[1],
                        venueId = venueId,
                        timeDependent = false
                    ))
                }
                else -> { null }
            }
            if(policy != null){
                return Optional.of(policy)
            }
        }
        return Optional.empty()
    }
}

enum class FeeType{
    default, //디폴트
    security, //보증금
    weekend, //주말
    capacity, //인원수
    service //서비스 비용
}

data class FeePolicy(
    val type : FeeType,
    val value : Double
)

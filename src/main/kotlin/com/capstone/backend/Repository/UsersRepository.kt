package com.capstone.backend.Repository

import com.capstone.backend.Entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    // 사용자 이름으로 사용자 찾기 (Optional 반환)
    fun findByUsername(username: String): User?

    // 이메일로 사용자 찾기 (Optional 반환)
    fun findByEmail(email: String): User?
}
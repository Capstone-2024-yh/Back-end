package com.capstone.backend.Service

import com.capstone.backend.Entity.User
import com.capstone.backend.Repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional
    fun createUser(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun updateUser(user: User): User {
        return userRepository.save(user)
    }

    @Transactional
    fun deleteUserById(userId: Long) {
        userRepository.deleteById(userId)
    }
}

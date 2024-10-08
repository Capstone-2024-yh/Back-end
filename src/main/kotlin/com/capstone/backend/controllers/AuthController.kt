package com.capstone.backend.controllers

import com.capstone.backend.Service.AuthService
import com.capstone.backend.core.aop.RequiresLogin
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    // 회원가입
    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<String> {
        try {
            authService.registerUser(registerRequest.username, registerRequest.email, registerRequest.password)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("User registration failed")
        }
        return ResponseEntity.ok("User registered successfully")
    }

    // 로그인
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Int> {
        val (success, userId) = authService.login(loginRequest.email, loginRequest.password)
        return if (success) {
            ResponseEntity.ok(userId)
        } else {
            ResponseEntity.status(401).body(0)
        }
    }

    // 로그아웃
    @RequiresLogin
    @PostMapping("/logout")
    fun logout(): ResponseEntity<String> {
        authService.logout()
        return ResponseEntity.ok("Logged out successfully")
    }

    // 로그인 상태 확인
    @GetMapping("/status")
    fun loginStatus(): ResponseEntity<String> {
        return if (authService.isLoggedIn()) {
            ResponseEntity.ok("User is logged in")
        } else {
            ResponseEntity.status(401).body("User is not logged in")
        }
    }
}

// DTO 정의
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)
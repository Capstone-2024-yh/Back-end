package com.capstone.backend.controllers

import com.capstone.backend.Service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    // 회원가입
    @PostMapping("/register")
    fun register(
        @RequestParam username: String,
        @RequestParam email: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        try {
            authService.registerUser(username, email, password)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body("User registration failed")
        }
        return ResponseEntity.ok("User registered successfully")
    }

    // 로그인
    @PostMapping("/login")
    fun login(
        @RequestParam email: String,
        @RequestParam password: String
    ): ResponseEntity<String> {
        val success = authService.login(email, password)
        return if (success) {
            ResponseEntity.ok("Login successful")
        } else {
            ResponseEntity.status(401).body("Invalid credentials")
        }
    }

    // 로그아웃
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
